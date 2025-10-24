package org.epsda.bookmanager.pojo.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.epsda.bookmanager.pojo.response.vo.NoticeResp;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 18483
 * Date: 2025/10/24
 * Time: 19:57
 *
 * @Author: 憨八嘎
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QueryNoticeResp {
    private Long currentPage;
    private Long totalPages;
    private Long totalCount;
    private List<NoticeResp> noticeResps;
}
