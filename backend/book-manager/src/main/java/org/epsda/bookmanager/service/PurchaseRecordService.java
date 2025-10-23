package org.epsda.bookmanager.service;

import jakarta.validation.constraints.NotNull;
import org.epsda.bookmanager.pojo.PurchaseRecord;
import org.epsda.bookmanager.pojo.request.QueryPurchaseRecordReq;
import org.epsda.bookmanager.pojo.response.QueryPurchaseRecordResp;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 18483
 * Date: 2025/10/23
 * Time: 18:59
 *
 * @Author: 憨八嘎
 */
public interface PurchaseRecordService {
    QueryPurchaseRecordResp queryPurchaseRecords(QueryPurchaseRecordReq queryPurchaseRecordReq);

    Boolean addPurchaseRecord(PurchaseRecord purchaseRecord);

    PurchaseRecord getPurchaseRecordByPurchaseId(Long purchaseId);

    Boolean editPurchaseRecord(PurchaseRecord purchaseRecord);

    Boolean cancelPurchasing(@NotNull Long purchaseId);
}
