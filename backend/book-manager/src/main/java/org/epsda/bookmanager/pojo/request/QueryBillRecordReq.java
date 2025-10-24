package org.epsda.bookmanager.pojo.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 18483
 * Date: 2025/10/24
 * Time: 9:29
 *
 * @Author: 憨八嘎
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QueryBillRecordReq {
    private Integer pageNum = 1;
    // 默认一页内容为10条
    private Integer pageSize = 10;
    private String username;
    private String email;
    private String phone;
    private Integer status;
}
