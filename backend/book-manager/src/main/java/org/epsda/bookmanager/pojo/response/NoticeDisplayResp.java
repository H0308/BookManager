package org.epsda.bookmanager.pojo.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.epsda.bookmanager.pojo.response.dto.NoticeDisplay;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 18483
 * Date: 2025/10/24
 * Time: 20:31
 *
 * @Author: 憨八嘎
 */
@Data
@AllArgsConstructor
public class NoticeDisplayResp {
    private List<NoticeDisplay> noticeDisplays;
}
