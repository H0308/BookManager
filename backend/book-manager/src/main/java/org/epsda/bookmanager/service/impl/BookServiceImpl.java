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
import org.epsda.bookmanager.pojo.response.BookWithAvailableCount;
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
        if (StringUtils.hasLength(categoryName)) {
            List<Category> categories = categoryMapper.selectCategoryLikeCategoryName(categoryName);
            if (!categories.isEmpty()) {
                // 转换为ID集合
                categoryIds = categories.stream().map(Category::getId).toList();
            }
        }
        // 五种查询条件
        wrapper.like(StringUtils.hasLength(bookName), Book::getBookName, bookName)
                .like(StringUtils.hasLength(isbn), Book::getIsbn, isbn)
                .like(StringUtils.hasLength(author), Book::getAuthor, author)
                .like(StringUtils.hasLength(publisher), Book::getPublisher, publisher)
                .in(StringUtils.hasLength(categoryName) && !categoryIds.isEmpty(),
                        Book::getCategoryId, categoryIds)
                .eq(Book::getDeleteFlag, 0);

        Page<Book> bookPage = bookMapper.selectPage(page, wrapper);

        List<Book> allBooks = bookPage.getRecords();
        List<BookWithAvailableCount> availables = new ArrayList<>();
        // 需要设置图书是否被借阅或者被购买，计算出可用量
        for (var book : allBooks) {
            // 得到借阅量
            List<BorrowRecord> borrowRecordByBookId = borrowRecordMapper.getBorrowRecordByBookId(book.getId());
            int borrows = 0;
            if (borrowRecordByBookId != null && !borrowRecordByBookId.isEmpty()) {
                borrows = borrowRecordByBookId.size();
            }
            // 得到购买量
            int purchases = 0;
            List<BorrowRecord> purchaseRecordByBookId = purchaseRecordMapper.getPurchaseRecordByBookId(book.getId());
            if (purchaseRecordByBookId != null && !purchaseRecordByBookId.isEmpty()) {
                purchases = purchaseRecordByBookId.size();
            }
            // 得到总量
            Integer totalCount = book.getTotalCount();
            if (!(totalCount > 0)) {
                continue;
            }
            // 计算可用量
            int availableCount = totalCount - borrows - purchases;
            BookWithAvailableCount bookWithAvailableCount = BeanUtil.convert(book);
            bookWithAvailableCount.setAvailableCount(availableCount);
            bookWithAvailableCount.setCategory(categoryMapper.selectById(book.getCategoryId()).getCategoryName());
            availables.add(bookWithAvailableCount);
        }

        return new QueryBookResp(bookPage.getCurrent(), bookPage.getPages(), bookPage.getTotal(), availables);
    }

    @Override
    public Boolean addBook(Book book) {
        // 先查询是否有标记为被删除的书籍
        LambdaQueryWrapper<Book> wrapper = new LambdaQueryWrapper<Book>().eq(Book::getIsbn, book.getIsbn()).eq(Book::getDeleteFlag, 1);
        Book existed = bookMapper.selectOne(wrapper);
        if (existed != null) {
            // 说明存在虚拟删除的书籍，此时只需要更新状态值即可
            Book newBook = new Book();
            newBook.setDeleteFlag(0);
            return bookMapper.update(newBook, new LambdaQueryWrapper<Book>().eq(Book::getIsbn, book.getIsbn())) == 1;
        }

        return bookMapper.insert(book) == 1;
    }

    @Override
    public Boolean editBook(Book book) {
        LambdaQueryWrapper<Book> wrapper = new LambdaQueryWrapper<Book>().eq(Book::getId, book.getId());
        return bookMapper.update(book, wrapper) == 1;
    }

    @Override
    public Book getBookById(Long id) {
        return bookMapper.selectById(id);
    }

    @Override
    public Boolean deleteBook(Long bookId) {
        // 先判断当前书籍是否有人借阅（借阅中或者逾期）
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

        // 上面两种情况都没有时可以删除图书
        Book deleted = new Book();
        deleted.setDeleteFlag(1);
        LambdaQueryWrapper<Book> wrapper = new LambdaQueryWrapper<Book>().eq(Book::getId, bookId);
        return bookMapper.update(deleted, wrapper) == 1;
    }

    @Override
    public Boolean batchDeleteBook(List<Long> bookIds) {
        var count = 0;
        for (var bookId : bookIds) {
            try {
                Boolean ret = deleteBook(bookId);
                if (ret) {
                    count++;
                }
            } catch (BookManagerException e) {
                throw new BookManagerException("当前图书有读者预购或者借阅，无法删除");
            }
        }

        return count == bookIds.size();
    }

    // 实际删除
    @Scheduled(fixedDelay = Constants.REAL_DELETE_EXAMINE_TIMEOUT)
    public void realBatchDelete() {
        // 查找到超过三天时间且删除标记为“已删除”的书籍
        List<Book> books = bookMapper.selectList(new LambdaQueryWrapper<Book>().eq(Book::getDeleteFlag, 1));
        if (books != null && !books.isEmpty()) {
            for (var book : books) {
                // 获取到时间
                LocalDateTime updateTime = book.getUpdateTime();
                LocalDateTime now = LocalDateTime.now();
                long between = ChronoUnit.DAYS.between(updateTime, now);
                // 日期间隔3天进行删除
                if (between >= 3) {
                    int ret = bookMapper.delete(new LambdaQueryWrapper<Book>().eq(Book::getId, book.getId()));
                    if (ret != 1) {
                        log.info("删除id为：{}的书籍失败", book.getId());
                    }
                }
            }
        }
    }
}
