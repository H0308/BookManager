package org.epsda.bookmanager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.epsda.bookmanager.constants.Constants;
import org.epsda.bookmanager.exception.BookManagerException;
import org.epsda.bookmanager.mapper.*;
import org.epsda.bookmanager.pojo.*;
import org.epsda.bookmanager.pojo.request.QueryBillRecordReq;
import org.epsda.bookmanager.pojo.response.DetailedBillRecord;
import org.epsda.bookmanager.pojo.response.QueryBillRecordResp;
import org.epsda.bookmanager.pojo.response.dto.BillRecordExcel;
import org.epsda.bookmanager.pojo.response.vo.BillRecordResp;
import org.epsda.bookmanager.service.BillRecordService;
import org.epsda.bookmanager.utils.BeanUtil;
import org.epsda.bookmanager.utils.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 18483
 * Date: 2025/10/24
 * Time: 9:26
 *
 * @Author: 憨八嘎
 */
@Service
public class BillRecordServiceImpl implements BillRecordService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private BillRecordMapper billRecordMapper;
    @Autowired
    private BorrowRecordMapper borrowRecordMapper;
    @Autowired
    private PurchaseRecordMapper purchaseRecordMapper;
    @Autowired
    private BookMapper bookMapper;

    // 获取账单信息
    // 管理员：获取所有订单信息
    // 普通用户：只获取自己的订单信息
    @Override
    public QueryBillRecordResp queryBillRecords(QueryBillRecordReq queryBillRecordReq) {
        Long userId = queryBillRecordReq.getUserId();
        // 防止水平越权
        SecurityUtil.checkHorizontalOverstepped(userId);

        Integer pageNum = queryBillRecordReq.getPageNum();
        Integer pageSize = queryBillRecordReq.getPageSize();

        Page<BillRecord> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<BillRecord> wrapper = new LambdaQueryWrapper<>();
        Integer status = queryBillRecordReq.getStatus();

        // 普通用户的逻辑
        if (Constants.USER_FLAG.equals(SecurityUtil.getRoleIdFromPrinciple())) {
            wrapper.eq(BillRecord::getUserId, userId) // 需要查找指定用户id的用户账单
                    .eq(status != null, BillRecord::getStatus, status); // 可以根据订单状态查询
        } else if (Constants.ADMIN_FLAG.equals(SecurityUtil.getRoleIdFromPrinciple())) {
            // 下面是管理员的逻辑
            String username = queryBillRecordReq.getUsername();
            String email = queryBillRecordReq.getEmail();
            String phone = queryBillRecordReq.getPhone();
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

            wrapper.in(StringUtils.hasText(username), BillRecord::getUserId, usernameIds)
                    .in(StringUtils.hasText(email), BillRecord::getUserId, emailIds)
                    .in(StringUtils.hasText(phone), BillRecord::getUserId, phoneIds)
                    .eq(status != null, BillRecord::getStatus, status);
        }

        Page<BillRecord> pages = billRecordMapper.selectPage(page, wrapper);
        List<BillRecord> records = pages.getRecords();
        List<BillRecordResp> billRecordResps = new ArrayList<>();
        for (var record : records) {
            User user = userMapper.selectById(record.getUserId());
            String recordUsername = user.getUsername();
            String recordEmail = user.getEmail();
            String recordPhone = user.getPhone();
            Integer recordStatus = record.getStatus();
            Long borrowId = record.getBorrowId();
            Long purchaseId = record.getPurchaseId();
            BigDecimal bills = generateBill(borrowId, purchaseId);
            BillRecordResp billRecordResp = BeanUtil.generateBillRecordResp(recordUsername, recordEmail, recordPhone, recordStatus, bills);
            billRecordResps.add(billRecordResp);
        }

        return new QueryBillRecordResp(pages.getCurrent(), pages.getPages(), pages.getTotal(), billRecordResps);
    }

    // 更新借阅状态
    @Override
    public Boolean insertOrUpdateBillRecordWithBorrowRecord(BorrowRecord borrowRecord) {
        Long borrowId = 0L;
        Long userId = 0L;
        // 防止水平越权
        if (borrowRecord != null) {
            SecurityUtil.checkHorizontalOverstepped(borrowRecord.getUserId());
        }
        if (borrowRecord != null && Constants.OVERDUE_FLAG.equals(borrowRecord.getStatus())) {
            borrowId = borrowRecord.getId();
            userId = borrowRecord.getUserId();
        }

        if (borrowId > 0 && userId > 0) {
            BillRecord billRecord = billRecordMapper.selectBillRecordByUserId(userId);
            if (billRecord != null) {
                // 此时说明存在一条账单，直接更新即可
                billRecord.setBorrowId(borrowId);
                return billRecordMapper.update(billRecord, new LambdaQueryWrapper<BillRecord>().eq(BillRecord::getUserId, userId)) == 1;
            } else {
                // 此时说明没有账单，直接新建
                BillRecord newBillRecord = new BillRecord();
                newBillRecord.setUserId(userId);
                newBillRecord.setBorrowId(borrowId);
                newBillRecord.setPurchaseId(0L);
                return billRecordMapper.insert(newBillRecord) == 1;
            }
        }

        // 如果此处返回了，说明并没有产生罚金，而是正常期内已归还的状态
        return true;
    }

    @Override
    public Boolean insertOrUpdateBillRecordWithPurchaseRecord(PurchaseRecord purchaseRecord) {
        Long purchaseId = 0L;
        Long userId = 0L;
        // 防止水平越权
        // 只对普通用户进行水平越权校验
        if (purchaseRecord != null) {
            SecurityUtil.checkHorizontalOverstepped(purchaseRecord.getUserId());
        }
        if (purchaseRecord != null && Constants.BOOK_UNPAID_FLAG.equals(purchaseRecord.getStatus())) {
            purchaseId = purchaseRecord.getId();
            userId = purchaseRecord.getUserId();
        }

        if (purchaseId > 0 && userId > 0) {
            BillRecord billRecord = billRecordMapper.selectBillRecordByUserId(userId);
            if (billRecord != null) {
                // 此时说明存在一条账单，直接更新即可
                billRecord.setPurchaseId(purchaseId);
                return billRecordMapper.update(billRecord, new LambdaQueryWrapper<BillRecord>().eq(BillRecord::getUserId, userId)) == 1;
            } else {
                // 此时说明没有账单，直接新建
                BillRecord newBillRecord = new BillRecord();
                newBillRecord.setUserId(userId);
                newBillRecord.setPurchaseId(purchaseId);
                newBillRecord.setBorrowId(0L);
                return billRecordMapper.insert(newBillRecord) == 1;
            }
        }

        // 如果此处返回了，说明并没有产生罚金，而是正常期内已归还的状态
        return true;
    }

    // 获取订单详情
    @Override
    public DetailedBillRecord getDetailedBill(Long billId) {
        BillRecord billRecord = billRecordMapper.selectById(billId);
        SecurityUtil.checkHorizontalOverstepped(billRecord.getUserId());
        // 获取到借阅ID和账单ID
        Long borrowId = billRecord.getBorrowId();
        Long purchaseId = billRecord.getPurchaseId();
        BigDecimal fine = new BigDecimal(0);
        BigDecimal purchasePrice = new BigDecimal(0);
        if (borrowId > 0) {
            BorrowRecord borrowRecord = borrowRecordMapper.selectById(borrowId);
            fine = borrowRecord.getFine();
        }
        if (purchaseId > 0) {
            PurchaseRecord purchaseRecord = purchaseRecordMapper.selectById(purchaseId);
            purchasePrice = purchaseRecord.getPurchasePrice();
        }

        return new DetailedBillRecord(fine, purchasePrice, fine.add(purchasePrice));
    }

    @Override
    public Boolean payBillRecord(Long billId) {
        // 获取到具体的账单
        BillRecord billRecord = billRecordMapper.selectById(billId);
        SecurityUtil.checkHorizontalOverstepped(billRecord.getUserId());
        if (Constants.BILL_PAID_FLAG.equals(billRecord.getStatus())) {
            throw new BookManagerException("该账单已经支付，无法二次支付");
        }
        // 获取到借阅id和购买id
        Long borrowId = billRecord.getBorrowId();
        Long purchaseId = billRecord.getPurchaseId();
        boolean borrowUpdate = true;
        boolean purchaseUpdate = true;
        if (borrowId > 0) {
            // 说明存在罚金
            BorrowRecord borrowRecord = borrowRecordMapper.selectById(borrowId);
            // 更新状态为已归还
            borrowRecord.setStatus(Constants.RETURN_FLAG);
            borrowUpdate = borrowRecordMapper.update(borrowRecord,
                    new LambdaQueryWrapper<BorrowRecord>().eq(BorrowRecord::getId, borrowRecord.getId())) == 1;
        }

        if (purchaseId > 0) {
            // 说明存在购买记录
            PurchaseRecord purchaseRecord = purchaseRecordMapper.selectById(purchaseId);
            purchaseRecord.setStatus(Constants.BOOK_PAID_FLAG);
            purchaseUpdate = purchaseRecordMapper.update(purchaseRecord,
                    new LambdaQueryWrapper<PurchaseRecord>().eq(PurchaseRecord::getId, purchaseRecord.getId())) == 1;
        }

        // 如果借阅记录和购买记录都处理了，那么此时的支付状态可以设置为已支付
        if (borrowUpdate && purchaseUpdate) {
            billRecord.setStatus(Constants.BILL_PAID_FLAG);
        }

        return borrowUpdate && purchaseUpdate &&
                (billRecordMapper.update(billRecord, new LambdaQueryWrapper<BillRecord>().eq(BillRecord::getId, billId)) == 1);
    }

    @Override
    public Boolean deleteBillRecord(Long billId) {
        // 删除需要判断当前是否账单的状态为未支付
        BillRecord billRecord = billRecordMapper.selectById(billId);
        SecurityUtil.checkHorizontalOverstepped(billRecord.getUserId());
        if (Constants.BILL_UNPAID_FLAG.equals(billRecord.getStatus())) {
            throw new BookManagerException("当前账单处于未支付状态，无法删除");
        }

        // 否则可以直接删除
        return billRecordMapper.delete(new LambdaQueryWrapper<BillRecord>().eq(BillRecord::getId, billId)) == 1;
    }

    @Override
    public List<BillRecordExcel> queryBillRecordsForExcel() {
        // 校验登录的用户的角色是否是管理员
        if (!Constants.ADMIN_FLAG.equals(SecurityUtil.getRoleIdFromPrinciple())) {
            throw new BookManagerException("当前用户无权导出账单明细");
        }
        List<BillRecord> billRecords = billRecordMapper.selectList(null);
        List<BillRecordExcel> billRecordExcels = new ArrayList<>();
        for (var billRecord : billRecords) {
            Long id = billRecord.getId();
            Long userId = billRecord.getUserId();
            User user = userMapper.selectById(userId);
            String username = user.getUsername();
            String phone = user.getPhone();
            String email = user.getEmail();
            Long borrowId = billRecord.getBorrowId();
            String borrowBookName = null;
            String borrowBookIsbn = null;
            BigDecimal fine = new BigDecimal(0);
            if (borrowId > 0) {
                BorrowRecord borrowRecord = borrowRecordMapper.selectById(borrowId);
                Long borrowRecordBookId = borrowRecord.getBookId();
                Book borrowBook = bookMapper.selectById(borrowRecordBookId);
                borrowBookName = borrowBook.getBookName();
                borrowBookIsbn = borrowBook.getIsbn();
                fine = borrowRecord.getFine();
            }

            Long purchaseId = billRecord.getPurchaseId();
            String purchaseBookName = null;
            String purchaseBookIsbn = null;
            BigDecimal purchasePrice = new BigDecimal(0);
            if (purchaseId > 0) {
                PurchaseRecord purchaseRecord = purchaseRecordMapper.selectById(purchaseId);
                Long purchaseRecordBookId = purchaseRecord.getBookId();
                Book purchaseBook = bookMapper.selectById(purchaseRecordBookId);
                purchaseBookName = purchaseBook.getBookName();
                purchaseBookIsbn = purchaseBook.getIsbn();
                purchasePrice = purchaseRecord.getPurchasePrice();
            }

            Integer status = billRecord.getStatus();
            LocalDateTime createTime = billRecord.getCreateTime();
            LocalDateTime updateTime = billRecord.getUpdateTime();

            BillRecordExcel billRecordExcel = BeanUtil.generateBilRecordExcel(id, userId, username, phone, email,
                    borrowId, borrowBookName, borrowBookIsbn, purchaseId, purchaseBookName,
                    purchaseBookIsbn, fine, purchasePrice, fine.add(purchasePrice), status, createTime, updateTime);

            billRecordExcels.add(billRecordExcel);
        }

        return billRecordExcels;
    }

    @Override
    public Boolean deleteBorrowRecordInBillRecord(Long borrowId) {
        BillRecord billRecord = billRecordMapper.selectBillRecordByBorrowId(borrowId);
        if (billRecord == null) {
            throw new BookManagerException("指定的账单记录不存在");
        }
        SecurityUtil.checkHorizontalOverstepped(billRecord.getUserId());

        billRecord.setBorrowId(0L);

        // 如果此时购买记录也为0，那么直接删除这条记录
        if (billRecord.getPurchaseId() == 0) {
            return billRecordMapper.delete(new LambdaQueryWrapper<BillRecord>().eq(BillRecord::getBorrowId, borrowId)) == 1;
        }

        return billRecordMapper.update(billRecord,
                new LambdaQueryWrapper<BillRecord>().eq(BillRecord::getBorrowId, borrowId)) == 1;
    }

    @Override
    public Boolean deletePurchaseRecordInBillRecord(Long purchaseId) {
        BillRecord billRecord = billRecordMapper.selectBillRecordByPurchaseId(purchaseId);
        if (billRecord == null) {
            throw new BookManagerException("指定的账单记录不存在");
        }
        SecurityUtil.checkHorizontalOverstepped(billRecord.getUserId());

        billRecord.setPurchaseId(0L);

        // 如果此时购买记录也为0，那么直接删除这条记录
        if (billRecord.getBorrowId() == 0) {
            return billRecordMapper.delete(new LambdaQueryWrapper<BillRecord>().eq(BillRecord::getPurchaseId, purchaseId)) == 1;
        }

        return billRecordMapper.update(billRecord,
                new LambdaQueryWrapper<BillRecord>().eq(BillRecord::getPurchaseId, purchaseId)) == 1;
    }

    @Override
    public Boolean batchDeleteBillRecord(List<Long> billIds) {
        var count = 0;
        for (var billId : billIds) {
            Boolean ret = deleteBillRecord(billId);
            if (ret) {
                count++;
            }
        }

        return count == billIds.size();
    }

    private BigDecimal generateBill(Long borrowId, Long purchaseId) {
        BigDecimal borrowBill = new BigDecimal(0);
        BigDecimal purchaseBill = new BigDecimal(0);
        BorrowRecord borrowRecord = borrowRecordMapper.selectById(borrowId);
        if (borrowRecord != null && borrowRecord.getFine().compareTo(new BigDecimal(0)) > 0) {
            borrowBill = borrowRecord.getFine();
        }
        PurchaseRecord purchaseRecord = purchaseRecordMapper.selectById(purchaseId);
        if (purchaseRecord != null && purchaseRecord.getPurchasePrice().compareTo(new BigDecimal(0)) > 0) {
            purchaseBill = purchaseRecord.getPurchasePrice();
        }
        return borrowBill.add(purchaseBill);
    }
}
