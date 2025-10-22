package org.epsda.bookmanager.pojo.response.vo;

import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 18483
 * Date: 2025/10/22
 * Time: 18:31
 *
 * @Author: 憨八嘎
 */
@Data
public class UserResp {
    private String username;
    private String phone;
    private String userIdCard;
    private String email;
    private String address;
    private Integer status;
    private Integer borrowRecordCount;
    private Integer purchaseRecordCount;
}
