package org.epsda.bookmanager.controller;

import jakarta.validation.constraints.NotNull;
import org.epsda.bookmanager.common.ResultWrapper;
import org.epsda.bookmanager.constants.Constants;
import org.epsda.bookmanager.pojo.BorrowRecord;
import org.epsda.bookmanager.pojo.request.QueryBorrowRecordReq;
import org.epsda.bookmanager.pojo.response.QueryBorrowRecordResp;
import org.epsda.bookmanager.service.BorrowRecordService;
import org.epsda.bookmanager.utils.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 18483
 * Date: 2025/10/23
 * Time: 9:14
 *
 * @Author: 憨八嘎
 */
@RestController
@RequestMapping("/borrow")
@PreAuthorize("hasAnyRole('管理员', '普通用户')")
public class BorrowRecordController {

    @Autowired
    private BorrowRecordService borrowRecordService;

    // 获取所有借阅记录
    // 管理员获取所有用户的未删除的借阅记录
    // 普通用户只获取到自己的未删除的借阅记录
    @RequestMapping("/query")
    public ResultWrapper<QueryBorrowRecordResp> queryBorrowRecords(@Validated @RequestBody QueryBorrowRecordReq borrowRecordReq) {
        // 只对普通用户进行水平越权校验
        if (Constants.USER_FLAG.equals(SecurityUtil.getRoleIdFromPrinciple())) {
            SecurityUtil.checkHorizontalOverstepped(borrowRecordReq.getUserId());
        }
        return ResultWrapper.normal(borrowRecordService.queryBorrowRecords(borrowRecordReq));
    }

    // 添加借阅记录
    // 管理员可以为任何用户添加借阅记录
    // 普通用户只能为自己添加借阅记录
    @RequestMapping("/add")
    public ResultWrapper<Boolean> addBorrowRecord(@Validated @RequestBody BorrowRecord borrowRecord) {
        // 只对普通用户进行水平越权校验
        SecurityUtil.checkHorizontalOverstepped(borrowRecord.getUserId());
        return ResultWrapper.normal(borrowRecordService.addBorrowRecord(borrowRecord));
    }

    // 获取指定的借阅记录
    // 管理员可以获取任意用户的借阅记录
    // 普通用户只能获取自己的借阅记录
    @RequestMapping("/get")
    public ResultWrapper<BorrowRecord> getBorrowRecordById(@NotNull Long borrowId, @NotNull Long userId) {
        // 只对普通用户进行水平越权校验
        SecurityUtil.checkHorizontalOverstepped(userId);
        return ResultWrapper.normal(borrowRecordService.getBorrowRecordById(borrowId));
    }

    // 编辑指定的借阅记录
    // 管理员可以编辑任意用户的借阅记录
    // 普通用户只能编辑自己的借阅记录
    @RequestMapping("/edit")
    public ResultWrapper<Boolean> editBorrowRecord(@Validated @RequestBody BorrowRecord borrowRecord) {
        SecurityUtil.checkHorizontalOverstepped(borrowRecord.getUserId());
        return ResultWrapper.normal(borrowRecordService.editBorrowRecord(borrowRecord));
    }

    // 删除指定的借阅记录
    // 管理员可以删除任意用户的借阅记录
    // 普通用户只能删除自己的借阅记录
    @RequestMapping("/delete")
    public ResultWrapper<Boolean> deleteBorrowRecord(@NotNull Long borrowId, @NotNull Long userId) {
        SecurityUtil.checkHorizontalOverstepped(userId);
        return ResultWrapper.normal(borrowRecordService.deleteBorrowRecord(borrowId));
    }

    // 归还书籍
    // 管理员可以归还任意用户借阅的书籍
    // 普通用户只能归还自己借阅的书籍
    @RequestMapping("/returnBook")
    public ResultWrapper<Boolean> returnBook(@NotNull Long borrowId, @NotNull Long userId) {
        SecurityUtil.checkHorizontalOverstepped(userId);
        return ResultWrapper.normal(borrowRecordService.returnBook(borrowId));
    }

    // 续借书籍
    // 管理员可以续借任意用户借阅的书籍
    // 普通用户只能续借自己借阅的书籍
    @RequestMapping("/renew")
    public ResultWrapper<Boolean> renewBook(@NotNull Long borrowId, @NotNull Long userId) {
        SecurityUtil.checkHorizontalOverstepped(userId);
        return ResultWrapper.normal(borrowRecordService.renewBook(borrowId));
    }

    @RequestMapping("/batchDelete")
    public ResultWrapper<Boolean> batchDeleteBorrowRecords(@RequestParam List<Long> borrowIds, @NotNull Long userId) {
        SecurityUtil.checkHorizontalOverstepped(userId);
        return ResultWrapper.normal(borrowRecordService.batchDeleteBorrowRecords(borrowIds));
    }
}
