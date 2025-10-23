package org.epsda.bookmanager.controller;

import jakarta.validation.constraints.NotNull;
import org.epsda.bookmanager.common.ResultWrapper;
import org.epsda.bookmanager.pojo.BorrowRecord;
import org.epsda.bookmanager.pojo.request.QueryBorrowRecordReq;
import org.epsda.bookmanager.pojo.response.QueryBorrowRecordResp;
import org.epsda.bookmanager.service.BorrowRecordService;
import org.springframework.beans.factory.annotation.Autowired;
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
public class BorrowRecordController {

    @Autowired
    private BorrowRecordService borrowRecordService;

    @RequestMapping("/query")
    public ResultWrapper<QueryBorrowRecordResp> queryBorrowRecords(@Validated @RequestBody QueryBorrowRecordReq borrowRecordReq) {
        return ResultWrapper.normal(borrowRecordService.queryBorrowRecords(borrowRecordReq));
    }

    @RequestMapping("/add")
    public ResultWrapper<Boolean> addBorrowRecord(@Validated @RequestBody BorrowRecord borrowRecord) {
        return ResultWrapper.normal(borrowRecordService.addBorrowRecord(borrowRecord));
    }

    @RequestMapping("/get")
    public ResultWrapper<BorrowRecord> getBorrowRecordById(@NotNull Long borrowId) {
        return ResultWrapper.normal(borrowRecordService.getBorrowRecordById(borrowId));
    }

    @RequestMapping("/edit")
    public ResultWrapper<Boolean> editBorrowRecord(@Validated @RequestBody BorrowRecord borrowRecord) {
        return ResultWrapper.normal(borrowRecordService.editBorrowRecord(borrowRecord));
    }

    @RequestMapping("/delete")
    public ResultWrapper<Boolean> deleteBorrowRecord(@NotNull Long borrowId) {
        return ResultWrapper.normal(borrowRecordService.deleteBorrowRecord(borrowId));
    }

    @RequestMapping("/returnBook")
    public ResultWrapper<Boolean> returnBook(@NotNull Long borrowId) {
        return ResultWrapper.normal(borrowRecordService.returnBook(borrowId));
    }

    @RequestMapping("/renew")
    public ResultWrapper<Boolean> renewBook(@NotNull Long borrowId) {
        return ResultWrapper.normal(borrowRecordService.renewBook(borrowId));
    }

    @RequestMapping("/batchDelete")
    public ResultWrapper<Boolean> batchDeleteBorrowRecords(@RequestParam List<Long> borrowIds) {
        return ResultWrapper.normal(borrowRecordService.batchDeleteBorrowRecords(borrowIds));
    }
}
