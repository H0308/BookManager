package org.epsda.bookmanager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.epsda.bookmanager.constants.Constants;
import org.epsda.bookmanager.exception.BookManagerException;
import org.epsda.bookmanager.mapper.BookMapper;
import org.epsda.bookmanager.mapper.BorrowRecordMapper;
import org.epsda.bookmanager.mapper.UserMapper;
import org.epsda.bookmanager.pojo.Book;
import org.epsda.bookmanager.pojo.BorrowRecord;
import org.epsda.bookmanager.pojo.User;
import org.epsda.bookmanager.pojo.request.QueryBorrowRecordReq;
import org.epsda.bookmanager.pojo.response.QueryBorrowRecordResp;
import org.epsda.bookmanager.pojo.response.vo.BorrowRecordResp;
import org.epsda.bookmanager.service.BillRecordService;
import org.epsda.bookmanager.service.BookService;
import org.epsda.bookmanager.service.BorrowRecordService;
import org.epsda.bookmanager.utils.BeanUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 18483
 * Date: 2025/10/23
 * Time: 9:16
 *
 * @Author: 憨八嘎
 */
@Service
public class BorrowRecordServiceImpl implements BorrowRecordService {

    @Autowired
    private BorrowRecordMapper borrowRecordMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private BookMapper bookMapper;
    @Autowired
    private BookService bookService;
    @Autowired
    private BillRecordService billRecordService;

    @Override
    public QueryBorrowRecordResp queryBorrowRecords(QueryBorrowRecordReq borrowRecordReq) {
        Integer pageNum = borrowRecordReq.getPageNum();
        Integer pageSize = borrowRecordReq.getPageSize();

        Page<BorrowRecord> page = new Page<>(pageNum, pageSize);

        LambdaQueryWrapper<BorrowRecord> wrapper = new LambdaQueryWrapper<>();
        String username = borrowRecordReq.getUsername();
        String phone = borrowRecordReq.getPhone();
        String email = borrowRecordReq.getEmail();
        String bookName = borrowRecordReq.getBookName();
        Integer timeDesc = borrowRecordReq.getTimeDesc();
        LocalDateTime startTime = borrowRecordReq.getStartTime();
        LocalDateTime endTime = borrowRecordReq.getEndTime();
        if (timeDesc != null && (startTime == null || endTime == null)) {
            throw new BookManagerException("请正确设置起始日期或者结束日期");
        }

        List<Long> usernameIds = new ArrayList<>();
        usernameIds.add(0L);
        if (StringUtils.hasText(username)) {
            List<User> users = userMapper.selectByUsername(username);
            if (users != null && !users.isEmpty()) {
                usernameIds = users.stream().map(User::getId).toList();
            }
        }
        List<Long> phoneIds = new ArrayList<>();
        phoneIds.add(0L);
        if (StringUtils.hasText(phone)) {
            List<User> users = userMapper.selectByPhone(phone);
            if (users != null && !users.isEmpty()) {
                phoneIds = users.stream().map(User::getId).toList();
            }
        }
        List<Long> emailIds = new ArrayList<>();
        emailIds.add(0L);
        if (StringUtils.hasText(phone)) {
            List<User> users = userMapper.selectByEmail(email);
            if (users != null && !users.isEmpty()) {
                emailIds = users.stream().map(User::getId).toList();
            }
        }
        List<Long> bookNameIds = new ArrayList<>();
        bookNameIds.add(0L);
        if (StringUtils.hasText(bookName)) {
            List<Book> books = bookMapper.selectByBookName(bookName);
            if (books != null && !books.isEmpty()) {
                bookNameIds = books.stream().map(Book::getId).toList();
            }
        }

        // 除了日期以外的字段不排序
        wrapper.in(StringUtils.hasText(username), BorrowRecord::getUserId, usernameIds)
                .in(StringUtils.hasText(email), BorrowRecord::getUserId, emailIds)
                .in(StringUtils.hasText(phone), BorrowRecord::getUserId, phoneIds)
                .in(StringUtils.hasText(bookName), BorrowRecord::getBookId, bookNameIds)
                .eq(borrowRecordReq.getStatus() != null, BorrowRecord::getStatus, borrowRecordReq.getStatus())
                .eq(BorrowRecord::getDeleteFlag, Constants.NOT_DELETE_FIELD_FLAG);

        // 日期字段按照升序排序
        wrapper.between(timeDesc != null && timeDesc.equals(Constants.BORROW_TIME_DESC),
                        BorrowRecord::getBorrowTime, startTime, endTime).orderByAsc(BorrowRecord::getBorrowTime)
                .between(timeDesc != null && timeDesc.equals(Constants.PRE_RETURN_TIME_DESC),
                        BorrowRecord::getPreReturnTime, startTime, endTime).orderByAsc(BorrowRecord::getPreReturnTime)
                .between(timeDesc != null && timeDesc.equals(Constants.REAL_RETURN_TIME_DESC),
                        BorrowRecord::getRealReturnTime, startTime, endTime).orderByAsc(BorrowRecord::getRealReturnTime)
                .eq(BorrowRecord::getDeleteFlag, Constants.NOT_DELETE_FIELD_FLAG);

        Page<BorrowRecord> pages = borrowRecordMapper.selectPage(page, wrapper);
        List<BorrowRecord> records = pages.getRecords();
        List<BorrowRecordResp> borrowRecordResps = new ArrayList<>();
        for (BorrowRecord record : records) {
            // 根据获取到的ID获取到用户信息
            User user = userMapper.selectById(record.getUserId());
            // 根据获取到的ID获取到图书信息
            Book book = bookMapper.selectById(record.getBookId());
            String recordUsername = user.getUsername();
            String recordEmail = user.getEmail();
            String recordPhone = user.getPhone();
            String recordBookName = book.getBookName();
            String recordIsbn = book.getIsbn();
            LocalDateTime borrowTime = record.getBorrowTime();
            LocalDateTime preReturnTime = record.getPreReturnTime();
            LocalDateTime realReturnTime = record.getRealReturnTime();
            Integer status = record.getStatus();
            BigDecimal fine = record.getFine();
            BorrowRecordResp borrowRecordResp = BeanUtil.generateBorrowRecordResp(recordUsername,
                    recordPhone, recordEmail, recordBookName, recordIsbn,
                    borrowTime, preReturnTime, realReturnTime,
                    status, fine);
            borrowRecordResps.add(borrowRecordResp);
        }

        return new QueryBorrowRecordResp(pages.getCurrent(), pages.getPages(), pages.getTotal(), borrowRecordResps);
    }

    @Override
    public Boolean addBorrowRecord(BorrowRecord borrowRecord) {
        Long userId = borrowRecord.getUserId();
        Long bookId = borrowRecord.getBookId();

        List<BorrowRecord> borrowRecordByUserId = borrowRecordMapper.getBorrowRecordByUserId(userId);
        User user = userMapper.selectById(userId);
        // 检查用户是否存在
        if (user == null) {
            throw new BookManagerException("当前用户不存在，无法发起借阅");
        }

        // 如果用户有逾期未缴费的书籍，必须先缴费
        for (var br : borrowRecordByUserId) {
            if (Constants.OVERDUE_FLAG.equals(br.getStatus())) {
                throw new BookManagerException("当前用户存在逾期未缴费的书籍，无法继续借阅任何书籍");
            }
        }

        // 需要判断借阅用户是否有同一本书且处于"借阅中"
        for (var br : borrowRecordByUserId) {
            if (bookId.equals(br.getBookId()) && Constants.BORROWING_FLAG.equals(br.getStatus())) {
                // 存在状态为处于"借阅中"的书籍
                throw new BookManagerException("当前用户存在同一册书籍正在借阅，无法继续借阅当前书籍");
            }
        }

        // 需要判断借阅书籍是否标记为无效
        Book book = bookMapper.selectById(bookId);
        // 检查图书是否存在
        if (book == null) {
            throw new BookManagerException("当前图书不存在，无法借阅");
        }

        if (Constants.BOOK_UNAVAILABLE_FLAG.equals(book.getStatus())) {
            throw new BookManagerException("当前书籍已没有空闲数量，无法继续借阅");
        }
        // 需要判断借阅书籍是否标记为删除
        if (Constants.DELETED_FIELD_FLAG.equals(book.getDeleteFlag())) {
            throw new BookManagerException("当前书籍已下架，无法继续借阅");
        }
        // 需要判断用户是否被标记为删除
        if (Constants.DELETED_FIELD_FLAG.equals(user.getDeleteFlag())) {
            throw new BookManagerException("当前用户已注销，无法借阅书籍");
        }

        // 可以添加借阅记录
        // 添加借阅
        boolean insertRet = borrowRecordMapper.insert(borrowRecord) == 1;
        // 修改用户的借阅量
        user.setBorrowRecordCount(user.getBorrowRecordCount() + 1);
        boolean userUpdateRet = userMapper.update(user,
                new LambdaQueryWrapper<User>().eq(User::getId, user.getId())) == 1;

        // 计算出可用量
        boolean bookUpdateRet = updateBookStatus(book);

        return insertRet && userUpdateRet && bookUpdateRet;
    }

    @Override
    public BorrowRecord getBorrowRecordById(Long borrowId) {
        return borrowRecordMapper.selectById(borrowId);
    }

    @Override
    public Boolean deleteBorrowRecord(Long borrowId) {
        // 需要判断当前借阅记录状态是否是已归还且未被比较为删除
        // 如果不是则无法进行删除
        BorrowRecord borrowRecord = borrowRecordMapper.selectById(borrowId);
        if (Constants.BORROWING_FLAG.equals(borrowRecord.getStatus()) ||
            Constants.OVERDUE_FLAG.equals(borrowRecord.getStatus()) ||
            Constants.DELETED_FIELD_FLAG.equals(borrowRecord.getDeleteFlag())) {
            throw new BookManagerException("当前借阅记录处于未归还、逾期未缴费或者已经被删除状态，无法进行删除");
        }
        // 先更新账单记录
        Boolean billUpdateRet = billRecordService.deleteBorrowRecordInBillRecord(borrowId);
        // 否则可以进行删除
        borrowRecord.setDeleteFlag(Constants.DELETED_FIELD_FLAG);
        boolean borrowUpdateRet = borrowRecordMapper.update(borrowRecord,
                new LambdaQueryWrapper<BorrowRecord>().eq(BorrowRecord::getId, borrowId)) == 1;
        Long bookId = borrowRecord.getBookId();
        Book book = bookMapper.selectById(bookId);
        // 计算出可用量
        boolean bookUpdateRet = updateBookStatus(book);

        return borrowUpdateRet && bookUpdateRet && billUpdateRet;
    }

    @Override
    public Boolean returnBook(Long borrowId) {
        // 归还图书需要检查状态是否逾期，如果逾期，需要计算出罚金
        // 否则设为已归还即可
        LocalDateTime now = LocalDateTime.now();
        BorrowRecord borrowRecord = borrowRecordMapper.selectById(borrowId);
        Long bookId = borrowRecord.getBookId();
        Book book = bookMapper.selectById(bookId);
        LocalDateTime preReturnTime = borrowRecord.getPreReturnTime();
        borrowRecord.setRealReturnTime(now);

        // 如果是已归还状态，不允许再次归还
        if (Constants.RETURN_FLAG.equals(borrowRecord.getStatus())) {
            throw new BookManagerException("当前图书已归还，不允许二次归还图书");
        }

        if (Constants.OVERDUE_FLAG.equals(this.generateStatus(preReturnTime, now))) {
            // 出现逾期
            BigDecimal fine = this.generateFine(book.getPrice(), preReturnTime, now);
            borrowRecord.setFine(fine);
            borrowRecord.setStatus(Constants.OVERDUE_FLAG);
        } else if (Constants.RETURN_FLAG.equals(this.generateStatus(preReturnTime, now))) {
            // 未出现逾期
            borrowRecord.setStatus(Constants.RETURN_FLAG);
        }

        return updateBorrowRecordDecreaseUserBorrowCount(borrowRecord, borrowId) && updateBookStatus(book) &&
                billRecordService.insertOrUpdateBillRecordWithBorrowRecord(borrowRecord);
    }

    @Override
    public Boolean renewBook(Long borrowId) {
        // 默认续借30天
        BorrowRecord borrowRecord = borrowRecordMapper.selectById(borrowId);

        // 当前图书不能是已经删除的图书或者已经处于逾期或者已归还的图书
        if (Constants.OVERDUE_FLAG.equals(borrowRecord.getStatus())) {
            throw new BookManagerException("当前图书已经计算出罚金，不允许续借");
        }

        if (Constants.RETURN_FLAG.equals(borrowRecord.getStatus())) {
            throw new BookManagerException("当前图书已经归还，不允许续借");
        }

        LocalDateTime preReturnTime = borrowRecord.getPreReturnTime();
        LocalDateTime newPreReturnTime = preReturnTime.plusDays(30);
        borrowRecord.setPreReturnTime(newPreReturnTime);

        return borrowRecordMapper.update(borrowRecord,
                new LambdaQueryWrapper<BorrowRecord>().eq(BorrowRecord::getId, borrowId)) == 1;
    }

    @Override
    public Boolean batchDeleteBorrowRecords(List<Long> borrowIds) {
        var count = 0;
        for (var borrowId : borrowIds) {
            Boolean ret = deleteBorrowRecord(borrowId);
            if (ret) {
                count++;
            }
        }

        return count == borrowIds.size();
    }

    @Override
    public Boolean editBorrowRecord(BorrowRecord borrowRecord) {
        // 如果是修改借阅者，那么需要判断该借阅者是否满足借阅条件
        // 1. 没有逾期未缴费的书籍
        // 2. 没有借阅当前书籍
        BorrowRecord oldBorrowRecord = borrowRecordMapper.selectById(borrowRecord.getId());
        Long newBookId = borrowRecord.getBookId();
        Long newUserId = borrowRecord.getUserId();
        Long oldUserId = oldBorrowRecord.getUserId();
        Long oldBookId = oldBorrowRecord.getBookId();
        User newUser = userMapper.selectById(newUserId);
        if (newUser == null) {
            throw new BookManagerException("当前用户不存在，修改失败");
        }

        if (!newUserId.equals(oldUserId)) {
            List<BorrowRecord> borrowRecordByUserId = borrowRecordMapper.getBorrowRecordByUserId(newUserId);
            // 如果用户有逾期未缴费的书籍，必须先缴费
            for (var br : borrowRecordByUserId) {
                if (Constants.OVERDUE_FLAG.equals(br.getStatus())) {
                    throw new BookManagerException("当前用户存在逾期未缴费的书籍，无法修改为该用户");
                }
            }

            // 需要判断借阅用户是否有同一本书且处于"借阅中"
            for (var br : borrowRecordByUserId) {
                if (newBookId.equals(br.getBookId()) && Constants.BORROWING_FLAG.equals(br.getStatus())) {
                    // 存在状态为处于"借阅中"的书籍
                    throw new BookManagerException("当前用户存在同一册书籍正在借阅，无法修改为该用户");
                }
            }

            // 需要判断用户是否被标记为删除
            if (Constants.DELETED_FIELD_FLAG.equals(newUser.getDeleteFlag())) {
                throw new BookManagerException("当前用户已注销，无法借阅书籍");
            }
        }
        // 如果是修改借阅图书，那么需要判断该图书是否满足可借阅条件
        // 1. 图书空闲数量足够
        // 2. 图书没有被删除
        Book newBook = bookMapper.selectById(newBookId);
        if (newBook == null) {
            throw new BookManagerException("当前图书不存在，无法借阅");
        }
        if (!newBookId.equals(oldBookId) &&
            !(newBook.getStatus().equals(Constants.BOOK_AVAILABLE_FLAG) &&
            newBook.getDeleteFlag().equals(Constants.NOT_DELETE_FIELD_FLAG))) {
            throw new BookManagerException("当前图书数量不足或者图书已被下架，修改借阅失败");
        }

        // 如果有实际归还日期，那么需要判断状态是逾期还是已归还
        // 如果是逾期，那么就需要设置罚金和状态
        // 如果是已归还，那么只需要设置状态即可
        Book book = bookMapper.selectById(borrowRecord.getBookId());
        LocalDateTime newPreReturnTime = borrowRecord.getPreReturnTime();
        // 获取最新的归还日期（不论是修改了实际归还日期，还是没有修改，都可以保证计算出的罚金是正确且最新的）
        LocalDateTime newRealReturnTime = borrowRecord.getRealReturnTime();
        if (newRealReturnTime != null && Constants.OVERDUE_FLAG.equals(this.generateStatus(newPreReturnTime, newRealReturnTime))) {
            // 计算罚金并修改状态
            BigDecimal fine = this.generateFine(book.getPrice(), newPreReturnTime, newRealReturnTime);
            borrowRecord.setFine(fine);
            borrowRecord.setStatus(Constants.OVERDUE_FLAG);
            return updateBorrowRecordDecreaseUserBorrowCount(borrowRecord, borrowRecord.getId()) &&
                    updateBookStatus(book) && // 还要注意修改图书的状态
                    billRecordService.insertOrUpdateBillRecordWithBorrowRecord(borrowRecord); // 注意添加账单记录
        } else if (Constants.RETURN_FLAG.equals(this.generateStatus(newPreReturnTime, newRealReturnTime))) {
            // 此时说明是已归还状态，仅更新状态即可
            borrowRecord.setStatus(Constants.RETURN_FLAG);
            return updateBorrowRecordDecreaseUserBorrowCount(borrowRecord, borrowRecord.getId()) && updateBookStatus(book);
        }

        // 发起其他情况的修改
        return borrowRecordMapper.update(borrowRecord,
                new LambdaQueryWrapper<BorrowRecord>().eq(BorrowRecord::getId, borrowRecord.getId())) == 1;
    }

    // 计算日期生成罚金
    private BigDecimal generateFine(BigDecimal price, LocalDateTime preReturnTime, LocalDateTime realReturnTime) {
        // 如果实际归还日期在应还日期之后，计算单价*逾期天数
        long between = ChronoUnit.DAYS.between(preReturnTime, realReturnTime);
        if (between > 0) {
            BigDecimal betweenBigDecimal = new BigDecimal(between);
            return price.multiply(betweenBigDecimal);
        }

        return new BigDecimal(0);
    }

    // 获取借阅状态
    private Integer generateStatus(LocalDateTime preReturnTime, LocalDateTime realReturnTime) {
        // 如果实际归还日期为null，代表当前书籍还没有归还，直接返回借阅中状态即可
        if (realReturnTime == null) {
            return Constants.BORROWING_FLAG;
        }

        // 如果实际归还日期大于应还日期，状态为2
        if (realReturnTime.isAfter(preReturnTime)) {
            return Constants.OVERDUE_FLAG;
        }

        return Constants.RETURN_FLAG; // 同一天的情况依旧算已归还
    }

    private Boolean updateBorrowRecordDecreaseUserBorrowCount(BorrowRecord borrowRecord, Long borrowId) {
        boolean borrowUpdateRet = borrowRecordMapper.update(borrowRecord,
                new LambdaQueryWrapper<BorrowRecord>().eq(BorrowRecord::getId, borrowId)) == 1;
        Long userId = borrowRecord.getUserId();
        User user = userMapper.selectById(userId);
        user.setBorrowRecordCount(user.getBorrowRecordCount() - 1);
        boolean userUpdateRet = userMapper.update(user,
                new LambdaQueryWrapper<User>().eq(User::getId, user.getId())) == 1;
        return borrowUpdateRet && userUpdateRet;
    }

    private boolean updateBookStatus(Book book) {
        int availableCount = bookService.getAvailableCount(book);
        if (availableCount == 0) {
            book.setStatus(Constants.BOOK_UNAVAILABLE_FLAG);
        } else {
            book.setStatus(Constants.BOOK_AVAILABLE_FLAG);
        }

        return bookMapper.update(book, new LambdaQueryWrapper<Book>().eq(Book::getId, book.getId())) == 1;
    }
}
