package org.epsda.bookmanager.pojo.request;

import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 18483
 * Date: 2025/10/23
 * Time: 23:17
 *
 * @Author: 憨八嘎
 */
@Data
public class QueryRoleReq {
    private Long userId;
    private Integer pageNum = 1;
    private Integer pageSize = 10;
    private String role;
}
