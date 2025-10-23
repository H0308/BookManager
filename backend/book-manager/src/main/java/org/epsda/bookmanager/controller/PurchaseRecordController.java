package org.epsda.bookmanager.controller;

import jakarta.validation.constraints.NotNull;
import org.epsda.bookmanager.common.ResultWrapper;
import org.epsda.bookmanager.pojo.PurchaseRecord;
import org.epsda.bookmanager.pojo.request.QueryPurchaseRecordReq;
import org.epsda.bookmanager.pojo.response.QueryPurchaseRecordResp;
import org.epsda.bookmanager.service.PurchaseRecordService;
import org.springframework.beans.factory.annotation.Autowired;
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
public class PurchaseRecordController {

    @Autowired
    private PurchaseRecordService purchaseRecordService;

    @RequestMapping("/query")
    public ResultWrapper<QueryPurchaseRecordResp> queryPurchaseRecords(@Validated @RequestBody QueryPurchaseRecordReq queryPurchaseRecordReq) {
        return ResultWrapper.normal(purchaseRecordService.queryPurchaseRecords(queryPurchaseRecordReq));
    }

    @RequestMapping("/add")
    public ResultWrapper<Boolean> addPurchaseRecord(@Validated @RequestBody PurchaseRecord purchaseRecord) {
        return ResultWrapper.normal(purchaseRecordService.addPurchaseRecord(purchaseRecord));
    }

    @RequestMapping("/get")
    public ResultWrapper<PurchaseRecord> getPurchaseRecordByPurchaseId(@NotNull Long purchaseId) {
        return ResultWrapper.normal(purchaseRecordService.getPurchaseRecordByPurchaseId(purchaseId));
    }

    @RequestMapping("/edit")
    public ResultWrapper<Boolean> editPurchaseRecord(@Validated @RequestBody PurchaseRecord purchaseRecord) {
        return ResultWrapper.normal(purchaseRecordService.editPurchaseRecord(purchaseRecord));
    }

    @RequestMapping("/cancel")
    public ResultWrapper<Boolean> cancelPurchasing(@NotNull Long purchaseId) {
        return ResultWrapper.normal(purchaseRecordService.cancelPurchasing(purchaseId));
    }
}
