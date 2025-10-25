package org.epsda.bookmanager.controller;

import com.alibaba.excel.EasyExcel;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.SneakyThrows;
import org.epsda.bookmanager.common.ResultWrapper;
import org.epsda.bookmanager.pojo.BorrowRecord;
import org.epsda.bookmanager.pojo.PurchaseRecord;
import org.epsda.bookmanager.pojo.request.QueryBillRecordReq;
import org.epsda.bookmanager.pojo.response.DetailedBillRecord;
import org.epsda.bookmanager.pojo.response.QueryBillRecordResp;
import org.epsda.bookmanager.pojo.response.dto.BillRecordExcel;
import org.epsda.bookmanager.service.BillRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
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
@RequestMapping("/bill")
@RestController
public class BillRecordController {

    @Autowired
    private BillRecordService billRecordService;

    @RequestMapping("/query")
    public ResultWrapper<QueryBillRecordResp> queryBillRecords(@Validated @RequestBody QueryBillRecordReq queryBillRecordReq) {
        return ResultWrapper.normal(billRecordService.queryBillRecords(queryBillRecordReq));
    }

    @RequestMapping("/get")
    public ResultWrapper<DetailedBillRecord> getDetailedBill(@NotNull Long billId) {
        return ResultWrapper.normal(billRecordService.getDetailedBill(billId));
    }

    @RequestMapping("/pay")
    public ResultWrapper<Boolean> payBillRecord(@NotNull Long billId) {
        return ResultWrapper.normal(billRecordService.payBillRecord(billId));
    }

    @RequestMapping("/delete")
    public ResultWrapper<Boolean> deleteBillRecord(@NotNull Long billId) {
        return ResultWrapper.normal(billRecordService.deleteBillRecord(billId));
    }

    @RequestMapping("/batchDelete")
    public ResultWrapper<Boolean> batchDeleteBillRecord(@RequestParam List<Long> billIds) {
        return ResultWrapper.normal(billRecordService.batchDeleteBillRecord(billIds));
    }

    @RequestMapping("/excel")
    @SneakyThrows
    public void downloadBillRecordToExcel(HttpServletResponse response) {
        List<BillRecordExcel> billRecordExcelList = billRecordService.queryBillRecordsForExcel();

        // 设置响应头
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        response.setCharacterEncoding("utf-8");
        String filename = "book-manager-" + LocalDateTime.now() + "-账单明细表";
        // 防止中文乱码
        String fileName = URLEncoder.encode(filename, StandardCharsets.UTF_8).
                replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
        // 使用 EasyExcel 写入到响应输出流
        EasyExcel.write(response.getOutputStream(), BillRecordExcel.class)
                .sheet("账单明细表")
                .doWrite(billRecordExcelList);
    }

    @RequestMapping("/insertOrUpdateBillRecordWithBorrowRecord")
    public ResultWrapper<Boolean> insertOrUpdateBillRecordWithBorrowRecord(@RequestBody BorrowRecord borrowRecord) {
        return ResultWrapper.normal(billRecordService.insertOrUpdateBillRecordWithBorrowRecord(borrowRecord));
    }

    @RequestMapping("/insertOrUpdateBillRecordWithPurchaseRecord")
    public ResultWrapper<Boolean> insertOrUpdateBillRecordWithPurchaseRecord(@RequestBody PurchaseRecord purchaseRecord) {
        return ResultWrapper.normal(billRecordService.insertOrUpdateBillRecordWithPurchaseRecord(purchaseRecord));
    }

    @RequestMapping("/deleteBorrowRecordInBillRecord")
    public ResultWrapper<Boolean> deleteBorrowRecordInBillRecord(@RequestBody Long borrowId) {
        return ResultWrapper.normal(billRecordService.deleteBorrowRecordInBillRecord(borrowId));
    }

    @RequestMapping("/deletePurchaseRecordInBillRecord")
    public ResultWrapper<Boolean> deletePurchaseRecordInBillRecord(@RequestBody Long purchaseId) {
        return ResultWrapper.normal(billRecordService.deletePurchaseRecordInBillRecord(purchaseId));
    }
}
