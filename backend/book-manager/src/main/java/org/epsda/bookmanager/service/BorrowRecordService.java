package org.epsda.bookmanager.service;

import jakarta.validation.constraints.NotNull;
import org.epsda.bookmanager.pojo.BorrowRecord;
import org.epsda.bookmanager.pojo.request.QueryBorrowRecordReq;
import org.epsda.bookmanager.pojo.response.QueryBorrowRecordResp;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 18483
 * Date: 2025/10/23
 * Time: 9:15
 *
 * @Author: 憨八嘎
 */
public interface BorrowRecordService {
    QueryBorrowRecordResp queryBorrowRecords(QueryBorrowRecordReq borrowRecordReq);

    Boolean addBorrowRecord(BorrowRecord borrowRecord);

    Boolean editBorrowRecord(BorrowRecord borrowRecord);

    BorrowRecord getBorrowRecordById(Long borrowId);

    Boolean deleteBorrowRecord(Long borrowId);

    Boolean returnBook(Long borrowId);

    Boolean renewBook(@NotNull Long borrowId);

    Boolean batchDeleteBorrowRecords(List<Long> borrowIds);
}
