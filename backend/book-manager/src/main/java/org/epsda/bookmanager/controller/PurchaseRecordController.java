package org.epsda.bookmanager.controller;

import jakarta.validation.constraints.NotNull;
import org.epsda.bookmanager.common.ResultWrapper;
import org.epsda.bookmanager.pojo.PurchaseRecord;
import org.epsda.bookmanager.pojo.request.QueryPurchaseRecordReq;
import org.epsda.bookmanager.pojo.response.QueryPurchaseRecordResp;
import org.epsda.bookmanager.service.PurchaseRecordService;
import org.epsda.bookmanager.utils.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 18483
 * Date: 2025/10/23
 * Time: 18:58
 *
 * @Author: 憨八嘎
 */
@RequestMapping("/purchase")
@RestController
@PreAuthorize("hasAnyRole('管理员', '普通用户')")
public class PurchaseRecordController {

    @Autowired
    private PurchaseRecordService purchaseRecordService;

    @RequestMapping("/query")
    public ResultWrapper<QueryPurchaseRecordResp> queryPurchaseRecords(@Validated @RequestBody QueryPurchaseRecordReq queryPurchaseRecordReq) {
        SecurityUtil.checkHorizontalOverstepped(queryPurchaseRecordReq.getUserId());
        return ResultWrapper.normal(purchaseRecordService.queryPurchaseRecords(queryPurchaseRecordReq));
    }

    @RequestMapping("/add")
    public ResultWrapper<Boolean> addPurchaseRecord(@Validated @RequestBody PurchaseRecord purchaseRecord) {
        SecurityUtil.checkHorizontalOverstepped(purchaseRecord.getUserId());
        return ResultWrapper.normal(purchaseRecordService.addPurchaseRecord(purchaseRecord));
    }

    @RequestMapping("/get")
    public ResultWrapper<PurchaseRecord> getPurchaseRecordByPurchaseId(@NotNull Long purchaseId, @NotNull Long userId) {
        SecurityUtil.checkHorizontalOverstepped(userId);
        return ResultWrapper.normal(purchaseRecordService.getPurchaseRecordByPurchaseId(purchaseId));
    }

    @RequestMapping("/edit")
    public ResultWrapper<Boolean> editPurchaseRecord(@Validated @RequestBody PurchaseRecord purchaseRecord) {
        SecurityUtil.checkHorizontalOverstepped(purchaseRecord.getUserId());
        return ResultWrapper.normal(purchaseRecordService.editPurchaseRecord(purchaseRecord));
    }

    @RequestMapping("/cancel")
    public ResultWrapper<Boolean> cancelPurchasing(@NotNull Long purchaseId, @NotNull Long userId) {
        SecurityUtil.checkHorizontalOverstepped(userId);
        return ResultWrapper.normal(purchaseRecordService.cancelPurchasing(purchaseId));
    }
}
