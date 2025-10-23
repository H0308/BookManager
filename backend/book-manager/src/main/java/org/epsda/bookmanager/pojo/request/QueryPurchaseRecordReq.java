package org.epsda.bookmanager.pojo.request;

import lombok.Data;

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
public class QueryPurchaseRecordReq {
    private Integer pageNum;
    private Integer pageSize;
    private String username;
    private String email;
    private String phone;
    private String bookName;
    private Integer status;
}
