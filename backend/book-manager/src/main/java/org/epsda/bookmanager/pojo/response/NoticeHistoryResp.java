package org.epsda.bookmanager.pojo.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.epsda.bookmanager.pojo.response.dto.NoticeDisplay;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 18483
 * Date: 2025/10/26
 * Time: 19:37
 *
 * @Author: 憨八嘎
 */
@Data
@AllArgsConstructor
public class NoticeHistoryResp {
    private Long currentPage;
    private Long totalPages;
    private Long totalCount;
    private List<NoticeDisplay> noticeDisplays;
}
