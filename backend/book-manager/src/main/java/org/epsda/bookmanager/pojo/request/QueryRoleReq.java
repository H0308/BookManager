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
    private Integer pageNum;
    private Integer pageSize;
    private String role;
}
