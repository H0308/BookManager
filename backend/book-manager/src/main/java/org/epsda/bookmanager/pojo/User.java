package org.epsda.bookmanager.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 18483
 * Date: 2025/10/21
 * Time: 10:12
 *
 * @Author: 憨八嘎
 */
@Data
public class User {
    @TableId(type = IdType.AUTO)
    private Long id;
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
    private Integer borrowRecordCount;
    private Integer purchaseRecordCount;
    @Length(max = 255, message = "图片地址最长不超过255位")
    private String avatar;
    private Long roleId;
    private Integer deleteFlag;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
