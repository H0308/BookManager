package org.epsda.bookmanager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.epsda.bookmanager.constants.Constants;
import org.epsda.bookmanager.exception.BookManagerException;
import org.epsda.bookmanager.mapper.BookMapper;
import org.epsda.bookmanager.mapper.BorrowRecordMapper;
import org.epsda.bookmanager.mapper.PurchaseRecordMapper;
import org.epsda.bookmanager.mapper.UserMapper;
import org.epsda.bookmanager.pojo.Book;
import org.epsda.bookmanager.pojo.BorrowRecord;
import org.epsda.bookmanager.pojo.PurchaseRecord;
import org.epsda.bookmanager.pojo.User;
import org.epsda.bookmanager.pojo.response.AdminIndexDataResp;
import org.epsda.bookmanager.pojo.response.UserIndexDataResp;
import org.epsda.bookmanager.service.IndexService;
import org.epsda.bookmanager.utils.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 18483
 * Date: 2025/10/26
 * Time: 15:28
 *
 * @Author: 憨八嘎
 */
@Service
public class IndexServiceImpl implements IndexService {

    @Value("admin.admin-name")
    private String adminName;

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private BorrowRecordMapper borrowRecordMapper;
    @Autowired
    private PurchaseRecordMapper purchaseRecordMapper;
    @Autowired
    private BookMapper bookMapper;

    @Override
    public AdminIndexDataResp queryAdminHeaderData() {
        if (!Constants.ADMIN_FLAG.equals(SecurityUtil.getRoleIdFromPrinciple())) {
            throw new BookManagerException("当前用户无权获取管理员信息");
        }

        Long userCount = userMapper.selectCount(new LambdaQueryWrapper<User>().ne(User::getUsername, adminName));
        Long borrowCount = borrowRecordMapper.selectCount(new LambdaQueryWrapper<BorrowRecord>()
                .eq(BorrowRecord::getDeleteFlag, Constants.NOT_DELETE_FIELD_FLAG));
        Long purchaseCount = purchaseRecordMapper.selectCount(null);
        Long bookCount = bookMapper.selectCount(new LambdaQueryWrapper<Book>()
                .eq(Book::getStatus, Constants.BOOK_AVAILABLE_FLAG)
                .eq(Book::getDeleteFlag, Constants.NOT_DELETE_FIELD_FLAG));

        return new AdminIndexDataResp(userCount, bookCount, borrowCount, purchaseCount);
    }

    @Override
    public UserIndexDataResp queryUserHeaderData(Long userId) {
        SecurityUtil.checkHorizontalOverstepped(userId);

        Long borrowCount = borrowRecordMapper.selectCount(new LambdaQueryWrapper<BorrowRecord>().
                eq(BorrowRecord::getUserId, userId));
        Long unpaidCount = purchaseRecordMapper.selectCount(new LambdaQueryWrapper<PurchaseRecord>()
                .eq(PurchaseRecord::getUserId, userId)
                .eq(PurchaseRecord::getStatus, Constants.BOOK_UNPAID_FLAG));
        Long paidCount = purchaseRecordMapper.selectCount(new LambdaQueryWrapper<PurchaseRecord>()
                .eq(PurchaseRecord::getUserId, userId)
                .eq(PurchaseRecord::getStatus, Constants.BOOK_PAID_FLAG));
        User user = userMapper.selectById(userId);
        Long registerDays = ChronoUnit.DAYS.between(user.getCreateTime(), LocalDateTime.now());

        return new UserIndexDataResp(borrowCount, unpaidCount, paidCount, registerDays);
    }
}
