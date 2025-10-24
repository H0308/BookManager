package org.epsda.bookmanager.pojo.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 18483
 * Date: 2025/10/24
 * Time: 19:55
 *
 * @Author: 憨八嘎
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QueryNoticeReq {
    private Integer pageNum = 1;
    private Integer pageSize = 10;
    private String title;
    private String content;
    private Integer type;
    private Integer status;
}
