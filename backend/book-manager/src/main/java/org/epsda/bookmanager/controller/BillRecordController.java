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
import org.epsda.bookmanager.utils.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
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
@PreAuthorize("hasAnyRole('管理员', '普通用户')")
public class BillRecordController {

    @Autowired
    private BillRecordService billRecordService;

    // 获取账单详情
    // 管理员获取所有账单信息
    // 普通用户只获取到自己的账单信息
    @RequestMapping("/query")
    public ResultWrapper<QueryBillRecordResp> queryBillRecords(@Validated @RequestBody QueryBillRecordReq queryBillRecordReq) {
        SecurityUtil.checkHorizontalOverstepped(queryBillRecordReq.getUserId());
        return ResultWrapper.normal(billRecordService.queryBillRecords(queryBillRecordReq));
    }

    // 获取单个账单详情
    // 管理员可以获取到任意用户的账单
    // 普通用户必须确保获取的账单只属于自己
    @RequestMapping("/get")
    public ResultWrapper<DetailedBillRecord> getDetailedBill(@NotNull Long billId, @NotNull Long userId) {
        // 防止水平越权
        SecurityUtil.checkHorizontalOverstepped(userId);
        return ResultWrapper.normal(billRecordService.getDetailedBill(billId));
    }

    // 支付订单
    // 管理员可以支付任意用户的账单
    // 普通用户必须确保待支付的账单只属于自己
    @RequestMapping("/pay")
    public ResultWrapper<Boolean> payBillRecord(@NotNull Long billId, @NotNull Long userId) {
        // 防止水平越权
        SecurityUtil.checkHorizontalOverstepped(userId);
        return ResultWrapper.normal(billRecordService.payBillRecord(billId));
    }

    // 删除订单
    // 管理员可以删除任意用户的账单
    // 普通用户必须确保待删除的账单只属于自己
    @RequestMapping("/delete")
    public ResultWrapper<Boolean> deleteBillRecord(@NotNull Long billId, @NotNull Long userId) {
        // 防止水平越权
        SecurityUtil.checkHorizontalOverstepped(userId);
        return ResultWrapper.normal(billRecordService.deleteBillRecord(billId));
    }

    // 批量删除，逻辑同单一删除
    @RequestMapping("/batchDelete")
    public ResultWrapper<Boolean> batchDeleteBillRecord(@RequestParam List<Long> billIds, @NotNull Long userId) {
        // 防止水平越权
        SecurityUtil.checkHorizontalOverstepped(userId);
        return ResultWrapper.normal(billRecordService.batchDeleteBillRecord(billIds));
    }

    // 导出订单详情
    // 只提供给管理员，用于系统做报表统计
    @RequestMapping("/excel")
    @PreAuthorize("hasRole('管理员')")
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

    // 下面的接口是提供给内部服务进行调用的，不会在前端调用
    // 但是校验方便，所以在controller层也可以进行一次校验
    @RequestMapping("/insertOrUpdateBillRecordWithBorrowRecord")
    public ResultWrapper<Boolean> insertOrUpdateBillRecordWithBorrowRecord(@RequestBody BorrowRecord borrowRecord) {
        if (borrowRecord != null) {
            SecurityUtil.checkHorizontalOverstepped(borrowRecord.getUserId());
        }
        return ResultWrapper.normal(billRecordService.insertOrUpdateBillRecordWithBorrowRecord(borrowRecord));
    }

    // 下面的接口是提供给内部服务进行调用的，不会在前端调用
    // 但是校验方便，所以在controller层也可以进行一次校验
    @RequestMapping("/insertOrUpdateBillRecordWithPurchaseRecord")
    public ResultWrapper<Boolean> insertOrUpdateBillRecordWithPurchaseRecord(@RequestBody PurchaseRecord purchaseRecord) {
        if (purchaseRecord != null) {
            SecurityUtil.checkHorizontalOverstepped(purchaseRecord.getUserId());
        }
        return ResultWrapper.normal(billRecordService.insertOrUpdateBillRecordWithPurchaseRecord(purchaseRecord));
    }

    // 下面的接口是提供给内部服务进行调用的，不会在前端调用
    // 所以本次为了减少复杂度，只在service层进行水平越权校验
    @RequestMapping("/deleteBorrowRecordInBillRecord")
    public ResultWrapper<Boolean> deleteBorrowRecordInBillRecord(@RequestBody Long borrowId) {
        return ResultWrapper.normal(billRecordService.deleteBorrowRecordInBillRecord(borrowId));
    }

    // 下面的接口是提供给内部服务进行调用的，不会在前端调用
    // 所以本次为了减少复杂度，只在service层进行水平越权校验
    @RequestMapping("/deletePurchaseRecordInBillRecord")
    public ResultWrapper<Boolean> deletePurchaseRecordInBillRecord(@RequestBody Long purchaseId) {
        return ResultWrapper.normal(billRecordService.deletePurchaseRecordInBillRecord(purchaseId));
    }
}
