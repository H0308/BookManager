package org.epsda.bookmanager.pojo.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.epsda.bookmanager.pojo.response.vo.BillRecordResp;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 18483
 * Date: 2025/10/24
 * Time: 9:28
 *
 * @Author: 憨八嘎
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QueryBillRecordResp {
    private Long currentPage;
    private Long totalPages;
    private Long totalCount;
    private List<BillRecordResp> billRecordResps;
}
