package org.epsda.bookmanager.service;

import org.epsda.bookmanager.pojo.BorrowRecord;
import org.epsda.bookmanager.pojo.PurchaseRecord;
import org.epsda.bookmanager.pojo.request.QueryBillRecordReq;
import org.epsda.bookmanager.pojo.response.DetailedBillRecord;
import org.epsda.bookmanager.pojo.response.QueryBillRecordResp;
import org.epsda.bookmanager.pojo.response.dto.BillRecordExcel;

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
public interface BillRecordService {
    QueryBillRecordResp queryBillRecords(QueryBillRecordReq queryBillRecordReq);

    Boolean insertOrUpdateBillRecordWithBorrowRecord(BorrowRecord borrowRecord);

    Boolean insertOrUpdateBillRecordWithPurchaseRecord(PurchaseRecord purchaseRecord);

    DetailedBillRecord getDetailedBill(Long billId);

    Boolean payBillRecord(Long billId);

    Boolean deleteBillRecord(Long billId);

    List<BillRecordExcel> queryBillRecordsForExcel();

    Boolean deleteBorrowRecordInBillRecord(Long borrowId);

    Boolean deletePurchaseRecordInBillRecord(Long purchaseId);

    Boolean batchDeleteBillRecord(List<Long> billIds);
}
