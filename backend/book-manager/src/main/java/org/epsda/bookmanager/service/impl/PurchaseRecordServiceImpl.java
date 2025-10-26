package org.epsda.bookmanager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.epsda.bookmanager.constants.Constants;
import org.epsda.bookmanager.exception.BookManagerException;
import org.epsda.bookmanager.mapper.BookMapper;
import org.epsda.bookmanager.mapper.PurchaseRecordMapper;
import org.epsda.bookmanager.mapper.UserMapper;
import org.epsda.bookmanager.pojo.Book;
import org.epsda.bookmanager.pojo.PurchaseRecord;
import org.epsda.bookmanager.pojo.User;
import org.epsda.bookmanager.pojo.request.QueryPurchaseRecordReq;
import org.epsda.bookmanager.pojo.response.QueryPurchaseRecordResp;
import org.epsda.bookmanager.pojo.response.vo.PurchaseRecordResp;
import org.epsda.bookmanager.service.BillRecordService;
import org.epsda.bookmanager.service.BookService;
import org.epsda.bookmanager.service.PurchaseRecordService;
import org.epsda.bookmanager.utils.BeanUtil;
import org.epsda.bookmanager.utils.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 18483
 * Date: 2025/10/23
 * Time: 18:59
 *
 * @Author: 憨八嘎
 */
@Service
public class PurchaseRecordServiceImpl implements PurchaseRecordService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private BookMapper bookMapper;
    @Autowired
    private PurchaseRecordMapper purchaseRecordMapper;
    @Autowired
    private BookService bookService;
    @Autowired
    private BillRecordService billRecordService;

    @Override
    public QueryPurchaseRecordResp queryPurchaseRecords(QueryPurchaseRecordReq queryPurchaseRecordReq) {
        // 先进行水平越权校验
        SecurityUtil.checkHorizontalOverstepped(queryPurchaseRecordReq.getUserId());

        Integer pageNum = queryPurchaseRecordReq.getPageNum();
        Integer pageSize = queryPurchaseRecordReq.getPageSize();

        Page<PurchaseRecord> page = new Page<>(pageNum, pageSize);

        LambdaQueryWrapper<PurchaseRecord> wrapper = new LambdaQueryWrapper<>();
        String bookName = queryPurchaseRecordReq.getBookName();

        // 根据书名获取到图书id
        List<Long> bookNameIds = new ArrayList<>();
        bookNameIds.add(0L);
        if (StringUtils.hasText(bookName)) {
            List<Book> books = bookMapper.selectByBookName(bookName);
            if (books != null && !books.isEmpty()) {
                bookNameIds = books.stream().map(Book::getId).toList();
            }
        }

        if (Constants.USER_FLAG.equals(SecurityUtil.getRoleIdFromPrinciple())) {
            // 普通用户逻辑
            wrapper.in(StringUtils.hasText(bookName), PurchaseRecord::getBookId, bookNameIds)
                    .eq(queryPurchaseRecordReq.getStatus() != null,
                            PurchaseRecord::getStatus, queryPurchaseRecordReq.getStatus())
                    .eq(PurchaseRecord::getUserId, queryPurchaseRecordReq.getUserId());
        } else if (Constants.ADMIN_FLAG.equals(SecurityUtil.getRoleIdFromPrinciple())) {
            // 管理员逻辑
            String username = queryPurchaseRecordReq.getUsername();
            String email = queryPurchaseRecordReq.getEmail();
            String phone = queryPurchaseRecordReq.getPhone();
            // 根据用户名获取到用户id
            List<Long> usernameIds = new ArrayList<>();
            usernameIds.add(0L);
            if (StringUtils.hasText(username)) {
                List<User> users = userMapper.selectByUsername(username);
                if (users != null && !users.isEmpty()) {
                    usernameIds = users.stream().map(User::getId).toList();
                }
            }
            // 根据邮箱获取到用户id
            List<Long> emailIds = new ArrayList<>();
            emailIds.add(0L);
            if (StringUtils.hasText(email)) {
                List<User> users = userMapper.selectByEmail(email);
                if (users != null && !users.isEmpty()) {
                    emailIds = users.stream().map(User::getId).toList();
                }
            }
            // 根据电话获取到用户id
            List<Long> phoneIds = new ArrayList<>();
            phoneIds.add(0L);
            if (StringUtils.hasText(phone)) {
                List<User> users = userMapper.selectByPhone(phone);
                if (users != null && !users.isEmpty()) {
                    phoneIds = users.stream().map(User::getId).toList();
                }
            }

            wrapper.in(StringUtils.hasText(username), PurchaseRecord::getUserId, usernameIds)
                    .in(StringUtils.hasText(email), PurchaseRecord::getUserId, emailIds)
                    .in(StringUtils.hasText(phone), PurchaseRecord::getUserId, phoneIds)
                    .in(StringUtils.hasText(bookName), PurchaseRecord::getBookId, bookNameIds)
                    .eq(queryPurchaseRecordReq.getStatus() != null,
                            PurchaseRecord::getStatus, queryPurchaseRecordReq.getStatus());
        }

        Page<PurchaseRecord> pages = purchaseRecordMapper.selectPage(page, wrapper);
        List<PurchaseRecord> records = pages.getRecords();
        List<PurchaseRecordResp> purchaseRecordResps = new ArrayList<>();
        for (PurchaseRecord record : records) {
            // 根据获取到的ID获取到用户信息
            User user = userMapper.selectById(record.getUserId());
            // 根据获取到的ID获取到图书信息
            Book book = bookMapper.selectById(record.getBookId());
            String recordUsername = user.getUsername();
            String recordEmail = user.getEmail();
            String recordPhone = user.getPhone();
            String recordBookName = book.getBookName();
            Integer status = record.getStatus();
            Integer purchaseCount = record.getPurchaseCount();
            BigDecimal purchasePrice = record.getPurchasePrice();
            PurchaseRecordResp purchaseRecordResp = BeanUtil.generatePurchaseRecordResp(recordUsername,
                    recordPhone, recordEmail, recordBookName, status, purchaseCount, purchasePrice);
            purchaseRecordResps.add(purchaseRecordResp);
        }

        return new QueryPurchaseRecordResp(pages.getCurrent(), pages.getPages(), pages.getTotal(), purchaseRecordResps);
    }

    @Override
    public Boolean addPurchaseRecord(PurchaseRecord purchaseRecord) {
        Long userId = purchaseRecord.getUserId();
        User user = userMapper.selectById(userId);
        SecurityUtil.checkHorizontalOverstepped(userId);
        // 添加借阅记录必须保证书籍有空闲数量且书籍没有被标记为删除
        Long bookId = purchaseRecord.getBookId();
        Book book = bookMapper.selectById(bookId);
        if (purchaseRecord.getPurchaseCount() > bookService.getAvailableCount(book) ||
            Constants.BOOK_UNAVAILABLE_FLAG.equals(book.getStatus()) ||
            Constants.DELETED_FIELD_FLAG.equals(book.getDeleteFlag())) {
            throw new BookManagerException("当前书籍没有没有足够的数量或者已经下架，无法购买");
        }

        // 借阅者必须处于未注销的状态
        if (Constants.DELETED_FIELD_FLAG.equals(user.getDeleteFlag())) {
            throw new BookManagerException("当前用户处于注销状态，无法购买书籍");
        }

        // 如果购买的书籍是已经在订单列表中存在且未支付的，则直接更新数量
        LambdaQueryWrapper<PurchaseRecord> wrapper = new LambdaQueryWrapper<PurchaseRecord>()
                .eq(PurchaseRecord::getUserId, purchaseRecord.getUserId())
                .eq(PurchaseRecord::getBookId, purchaseRecord.getBookId())
                .eq(PurchaseRecord::getStatus, Constants.BOOK_UNPAID_FLAG);
        PurchaseRecord existed = purchaseRecordMapper.selectOne(wrapper);
        if (existed != null) {
            if (purchaseRecord.getPurchaseCount() > bookService.getAvailableCount(book)) {
                throw new BookManagerException("购买数量大于现有量，无法购买");
            } else {
                existed.setPurchasePrice(book.getPrice().multiply(new BigDecimal(purchaseRecord.getPurchaseCount())).add(existed.getPurchasePrice()));
                existed.setPurchaseCount(existed.getPurchaseCount() + purchaseRecord.getPurchaseCount());
                user.setPurchaseRecordCount(user.getPurchaseRecordCount() + purchaseRecord.getPurchaseCount());
                return purchaseRecordMapper.update(existed,
                        new LambdaQueryWrapper<PurchaseRecord>().eq(PurchaseRecord::getId, existed.getId())) == 1 &&
                        userMapper.update(user, new LambdaQueryWrapper<User>().eq(User::getId, user.getId())) == 1 &&
                        updateBookStatus(book);
            }
        }

        // 此时可以购买书籍
        purchaseRecord.setPurchasePrice(book.getPrice().multiply(new BigDecimal(purchaseRecord.getPurchaseCount())));
        boolean insertRet = purchaseRecordMapper.insert(purchaseRecord) == 1;
        // 增加用户的购买量和购买金额
        user.setPurchaseRecordCount(user.getPurchaseRecordCount() + purchaseRecord.getPurchaseCount());
        boolean updateRet = userMapper.update(user, new LambdaQueryWrapper<User>().eq(User::getId, user.getId())) == 1;
        // 此处需要显式设置购买状态，数据库默认设置为0，但是此时并没有同步给purchaseRecord变量
        // 导致下面insertOrUpdateBillRecordWithPurchaseRecord插入的对象status为空
        purchaseRecord.setStatus(0);
        return insertRet && updateRet && updateBookStatus(book) && // 需要检查是否需要修改书籍状态
                billRecordService.insertOrUpdateBillRecordWithPurchaseRecord(purchaseRecord);
    }

    @Override
    public PurchaseRecord getPurchaseRecordByPurchaseId(Long purchaseId) {
        PurchaseRecord purchaseRecord = purchaseRecordMapper.selectById(purchaseId);
        SecurityUtil.checkHorizontalOverstepped(purchaseRecord.getUserId());

        return purchaseRecord;
    }

    @Override
    public Boolean editPurchaseRecord(PurchaseRecord purchaseRecord) {
        SecurityUtil.checkHorizontalOverstepped(purchaseRecord.getUserId());
        // 如果修改的是读者，那么需要确保该读者有购买书籍的资格
        Long purchaseRecordId = purchaseRecord.getId();
        PurchaseRecord oldPurchaseRecord = purchaseRecordMapper.selectById(purchaseRecordId);
        Long newUserId = purchaseRecord.getUserId();
        User newUser = userMapper.selectById(newUserId);
        if (!oldPurchaseRecord.getUserId().equals(newUserId)) {
            if (Constants.DELETED_FIELD_FLAG.equals(newUser.getDeleteFlag())) {
                throw new BookManagerException("当前用户处于注销状态，无法购买书籍");
            }
        }

        // 如果修改的是书籍，那么需要确保书籍有被选择的资格
        Integer oldPurchaseCount = oldPurchaseRecord.getPurchaseCount();
        Integer newPurchaseCount = purchaseRecord.getPurchaseCount();
        Long newBookId = purchaseRecord.getBookId();
        Book newBook = bookMapper.selectById(newBookId);
        if (!oldPurchaseRecord.getBookId().equals(newBookId)) {
            // 添加借阅记录必须保证书籍有空闲数量且书籍没有被标记为删除
            if (newPurchaseCount > bookService.getAvailableCount(newBook) || // 修改了书籍，但是没有修改数量，需要判断选择的新书籍数量是否足够购买
                Constants.BOOK_UNAVAILABLE_FLAG.equals(newBook.getStatus()) ||
                Constants.DELETED_FIELD_FLAG.equals(newBook.getDeleteFlag())) {
                throw new BookManagerException("当前书籍没有没有足够的数量或者已经下架，无法购买");
            }
        }

        if (newPurchaseCount <= 0) {
            throw new BookManagerException("图书购买量必须大于0");
        }

        // 小于0的直接设置数量即可
        if (newPurchaseCount - oldPurchaseCount < 0) {
            newUser.setPurchaseRecordCount(newPurchaseCount);
            purchaseRecord.setPurchasePrice(newBook.getPrice().multiply(new BigDecimal(newPurchaseCount)));
            return purchaseRecordMapper.update(purchaseRecord,
                    new LambdaQueryWrapper<PurchaseRecord>().eq(PurchaseRecord::getId, purchaseRecord.getId())) == 1 &&
                    userMapper.update(newUser, new LambdaQueryWrapper<User>().eq(User::getId, newUserId)) == 1 &&
                    updateBookStatus(newBook);
        }


        // 但是不同的书数量不一定相同，此时还有判断当前切换后的书籍空闲量足够购买
        if ((newPurchaseCount - oldPurchaseCount > 0) &&
                (newPurchaseCount - oldPurchaseCount > bookService.getAvailableCount(newBook))) {
            throw new BookManagerException("当前图书可用数量不足需求量，请修改购买量后重试");
        }

        newUser.setPurchaseRecordCount(purchaseRecord.getPurchaseCount());
        purchaseRecord.setPurchasePrice(newBook.getPrice().multiply(new BigDecimal(purchaseRecord.getPurchaseCount())));
        return purchaseRecordMapper.update(purchaseRecord,
                new LambdaQueryWrapper<PurchaseRecord>().eq(PurchaseRecord::getId, purchaseRecord.getId())) == 1 &&
                userMapper.update(newUser, new LambdaQueryWrapper<User>().eq(User::getId, newUserId)) == 1 &&
                updateBookStatus(newBook);
    }

    @Override
    public Boolean cancelPurchasing(Long purchaseId) {
        PurchaseRecord purchaseRecord = purchaseRecordMapper.selectById(purchaseId);
        User user = userMapper.selectById(purchaseRecord.getUserId());
        SecurityUtil.checkHorizontalOverstepped(user.getId());

        // 如果状态是未付款状态，那么就是取消
        // 否则是退款
        // 但是本项目不考虑退款，所以二者处理方式是一致的
        Book book = bookMapper.selectById(purchaseRecord.getBookId());
        user.setPurchaseRecordCount(user.getPurchaseRecordCount() - purchaseRecord.getPurchaseCount());
        return purchaseRecordMapper.delete(new LambdaQueryWrapper<PurchaseRecord>().eq(PurchaseRecord::getId, purchaseId)) == 1 &&
                userMapper.update(user, new LambdaQueryWrapper<User>().eq(User::getId, user.getId())) == 1 &&
                updateBookStatus(book) && billRecordService.deletePurchaseRecordInBillRecord(purchaseId);
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
