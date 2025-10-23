package org.epsda.bookmanager.pojo.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.epsda.bookmanager.pojo.response.vo.PurchaseRecordResp;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 18483
 * Date: 2025/10/23
 * Time: 19:04
 *
 * @Author: 憨八嘎
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QueryPurchaseRecordResp {
    private Long currentPage;
    private Long totalPages;
    private Long totalCount;
    private List<PurchaseRecordResp> currentPurchaseRecords;
}
