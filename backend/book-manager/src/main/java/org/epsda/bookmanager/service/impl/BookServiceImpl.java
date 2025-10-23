package org.epsda.bookmanager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.epsda.bookmanager.constants.Constants;
import org.epsda.bookmanager.exception.BookManagerException;
import org.epsda.bookmanager.mapper.BookMapper;
import org.epsda.bookmanager.mapper.BorrowRecordMapper;
import org.epsda.bookmanager.mapper.CategoryMapper;
import org.epsda.bookmanager.mapper.PurchaseRecordMapper;
import org.epsda.bookmanager.pojo.Book;
import org.epsda.bookmanager.pojo.BorrowRecord;
import org.epsda.bookmanager.pojo.Category;
import org.epsda.bookmanager.pojo.request.QueryBookReq;
import org.epsda.bookmanager.pojo.response.vo.BookResp;
import org.epsda.bookmanager.pojo.response.QueryBookResp;
import org.epsda.bookmanager.service.BookService;
import org.epsda.bookmanager.utils.BeanUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 18483
 * Date: 2025/10/21
 * Time: 10:30
 *
 * @Author: 憨八嘎
 */
@Service
@Slf4j
public class BookServiceImpl implements BookService {

    @Autowired
    private BookMapper bookMapper;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private BorrowRecordMapper borrowRecordMapper;
    @Autowired
    private PurchaseRecordMapper purchaseRecordMapper;

    @Override
    public QueryBookResp queryBooks(QueryBookReq queryBookReq) {
        Integer pageNum = queryBookReq.getPageNum();
        Integer pageSize = queryBookReq.getPageSize();
        // 创建分页
        Page<Book> page = new Page<>(pageNum, pageSize);
        // 根据不同的字段条件进行分页
        LambdaQueryWrapper<Book> wrapper = new LambdaQueryWrapper<>();
        String bookName = queryBookReq.getBookName();
        String isbn = queryBookReq.getIsbn();
        String author = queryBookReq.getAuthor();
        String publisher = queryBookReq.getPublisher();
        String categoryName = queryBookReq.getCategoryName();
        List<Long> categoryIds = new ArrayList<>();
        categoryIds.add(0L); // id in [0]，确保没有通过分类查找到数据时不会返回所有数据
        if (StringUtils.hasText(categoryName)) {
            List<Category> categories = categoryMapper.selectCategoryLikeCategoryName(categoryName);
            if (categories != null && !categories.isEmpty()) {
                // 转换为ID集合
                categoryIds = categories.stream().map(Category::getId).toList();
            }
        }
        // 五种查询条件
        wrapper.like(StringUtils.hasText(bookName), Book::getBookName, bookName)
                .like(StringUtils.hasText(isbn), Book::getIsbn, isbn)
                .like(StringUtils.hasText(author), Book::getAuthor, author)
                .like(StringUtils.hasText(publisher), Book::getPublisher, publisher)
                .in(StringUtils.hasText(categoryName), Book::getCategoryId, categoryIds)
                .eq(Book::getDeleteFlag, Constants.NOT_DELETE_FIELD_FLAG);

        Page<Book> bookPage = bookMapper.selectPage(page, wrapper);

        List<Book> allBooks = bookPage.getRecords();
        List<BookResp> availables = new ArrayList<>();
        // 需要设置图书是否被借阅或者被购买，计算出可用量
        for (var book : allBooks) {
            // 计算可用量
            int availableCount = getAvailableCount(book);
            BookResp bookResp = BeanUtil.convert(book);
            bookResp.setAvailableCount(availableCount);
            log.info("当前图书：{}的可用数量为：{}", book.getBookName(), availableCount);
            bookResp.setCategory(categoryMapper.selectById(book.getCategoryId()).getCategoryName());
            availables.add(bookResp);
        }

        return new QueryBookResp(bookPage.getCurrent(), bookPage.getPages(), bookPage.getTotal(), availables);
    }

    @Override
    public Boolean addBook(Book book) {
        // 查看是否已经有存在的书籍
        LambdaQueryWrapper<Book> isbnWrapper = new LambdaQueryWrapper<Book>().eq(Book::getIsbn, book.getIsbn());
        Book existedNotDeleted = bookMapper.selectOne(isbnWrapper.eq(Book::getDeleteFlag, Constants.NOT_DELETE_FIELD_FLAG));
        if (existedNotDeleted != null) {
            // 存在时直接更新图书数量
            existedNotDeleted.setTotalCount(existedNotDeleted.getTotalCount() + 1);
            return bookMapper.update(existedNotDeleted, isbnWrapper) == 1;
        }

        // 先查询是否有标记为被删除的书籍
        LambdaQueryWrapper<Book> wrapper = new LambdaQueryWrapper<Book>().eq(Book::getIsbn, book.getIsbn()).eq(Book::getDeleteFlag, Constants.DELETED_FIELD_FLAG);
        Book deleted = bookMapper.selectOne(wrapper);
        if (deleted != null) {
            // 说明存在虚拟删除的书籍，此时只需要更新状态值即可
            Book newBook = new Book();
            newBook.setDeleteFlag(Constants.NOT_DELETE_FIELD_FLAG);
            boolean bookUpdate = bookMapper.update(newBook, wrapper) == 1;
            // 增加对应分类的图书数量
            return bookUpdate && bookAddDeleteChangeCategoryCount(deleted.getCategoryId(), true);
        }

        boolean bookUpdate = bookMapper.insert(book) == 1;
        return bookUpdate && bookAddDeleteChangeCategoryCount(book.getCategoryId(), true);
    }

    @Override
    public Boolean editBook(Book book) {
        LambdaQueryWrapper<Book> wrapper = new LambdaQueryWrapper<Book>().eq(Book::getId, book.getId()).eq(Book::getDeleteFlag, Constants.NOT_DELETE_FIELD_FLAG);
        return bookMapper.update(book, wrapper) == 1;
    }

    @Override
    public Book getBookById(Long id) {
        return bookMapper.selectById(id);
    }

    @Override
    public Boolean deleteBook(Long bookId) {
        // 先判断当前书籍是否有人借阅（借阅中）
        List<BorrowRecord> borrowRecordByBookId = borrowRecordMapper.getBorrowRecordByBookId(bookId);
        if (!borrowRecordByBookId.isEmpty()) {
            throw new BookManagerException("当前图书有读者借阅，无法删除");
        }

        // 判断有人是否已经预定
        // 不需要考虑已支付，因为书已经到读者手上了
        List<BorrowRecord> purchaseRecordByBookId = purchaseRecordMapper.getPurchaseRecordByBookId(bookId);
        if (!purchaseRecordByBookId.isEmpty()) {
            throw new BookManagerException("当前图书有读者预购，无法删除");
        }

        LambdaQueryWrapper<Book> wrapper = new LambdaQueryWrapper<Book>().eq(Book::getId, bookId);
        // 再判断图书是否是处于虚拟删除
        Book book = bookMapper.selectOne(wrapper);
        if (Constants.DELETED_FIELD_FLAG.equals(book.getDeleteFlag())) {
            throw new BookManagerException("图书已经删除，无法再次删除");
        }

        // 上面三种情况都没有时可以删除图书，同时还要修改对应分类的数量
        Book deleted = new Book();
        deleted.setDeleteFlag(Constants.DELETED_FIELD_FLAG);
        Long categoryId = book.getCategoryId();
        return bookMapper.update(deleted, wrapper) == 1 && bookAddDeleteChangeCategoryCount(categoryId, false);
    }

    @Override
    public Boolean batchDeleteBook(List<Long> bookIds) {
        var count = 0;
        for (var bookId : bookIds) {
            Boolean ret = deleteBook(bookId);
            if (ret) {
                count++;
            }
        }

        return count == bookIds.size();
    }

    @Override
    public Integer getAvailableCount(Book book) {
        // 检查图书可用量确保图书状态正确
        int totalCount = book.getTotalCount();
        Long bookId = book.getId();
        List<BorrowRecord> borrowRecordByBookId = borrowRecordMapper.getBorrowRecordByBookId(bookId);
        int borrows = 0;
        if (borrowRecordByBookId != null) {
            borrows = borrowRecordByBookId.size();
        }
        List<BorrowRecord> purchaseRecordByBookId = purchaseRecordMapper.getPurchaseRecordByBookId(bookId);
        int purchases = 0;
        if (purchaseRecordByBookId != null) {
            purchases = purchaseRecordByBookId.size();
        }
        return totalCount - purchases - borrows;
    }

    // 实际删除
    // 每三天检查一次
    // @Scheduled(fixedDelay = Constants.REAL_DELETE_EXAMINE_TIMEOUT)
    public void realBatchDelete() {
        // 查找到超过三天时间且删除标记为“已删除”的书籍
        List<Book> books = bookMapper.selectList(new LambdaQueryWrapper<Book>().eq(Book::getDeleteFlag, Constants.DELETED_FIELD_FLAG));
        if (books != null && !books.isEmpty()) {
            for (var book : books) {
                // 获取到时间
                LocalDateTime updateTime = book.getUpdateTime();
                LocalDateTime now = LocalDateTime.now();
                long between = ChronoUnit.DAYS.between(updateTime, now);
                // 日期间隔3天进行删除
                if (between >= Constants.REAL_DELETE_EXAMINE_TIMEOUT) {
                    int ret = bookMapper.delete(new LambdaQueryWrapper<Book>().eq(Book::getId, book.getId()));
                    if (ret != 1) {
                        log.info("删除id为：{}的书籍失败", book.getId());
                    }
                }
            }
        }
    }

    private Boolean bookAddDeleteChangeCategoryCount(Long categoryId, Boolean addFlag) {
        LambdaQueryWrapper<Category> categoryWrapper = new LambdaQueryWrapper<Category>().eq(Category::getId, categoryId);
        Category category = categoryMapper.selectOne(categoryWrapper);
        if (addFlag) {
            category.setCategoryCount(category.getCategoryCount() + 1);
        } else {
            category.setCategoryCount(category.getCategoryCount() - 1);
        }
        return categoryMapper.update(category, categoryWrapper) == 1;
    }

}
