package org.epsda.bookmanager.pojo.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 18483
 * Date: 2025/10/25
 * Time: 16:43
 *
 * @Author: 憨八嘎
 */
@Data
public class RegisterReq {
    @NotNull
    @Length(max = 20, message = "用户名最长不超过20位")
    private String username;
    @NotNull
    @Length(max = 20, message = "密码最长不超过20位")
    private String password;
    @NotNull
    @Length(max = 20, message = "电话最长不超过20位")
    private String phone;
    @NotNull
    @Length(max = 30, message = "用户身份证最长不超过30位")
    private String userIdCard;
    @NotNull
    @Length(max = 30, message = "邮箱最长不超过30位")
    private String email;
    @NotNull
    @Length(max = 30, message = "地址最长不超过30位")
    private String address;
}
