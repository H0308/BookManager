package org.epsda.bookmanager.pojo.request;

import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 18483
 * Date: 2025/10/22
 * Time: 18:25
 *
 * @Author: 憨八嘎
 */
@Data
public class QueryUserReq {
    private Integer pageNum = 1;
    private Integer pageSize = 10;
    private String username;
    private String phone;
    private String userIdCard;
    private String address;
}
