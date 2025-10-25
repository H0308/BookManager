package org.epsda.bookmanager.pojo.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 18483
 * Date: 2025/10/25
 * Time: 15:19
 *
 * @Author: 憨八嘎
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginReq {
    @NotNull
    @Length(max = 30, message = "邮箱最长不超过30位")
    private String email;
    @NotNull
    @Length(max = 20, message = "密码最长不超过20位")
    private String password;
}
