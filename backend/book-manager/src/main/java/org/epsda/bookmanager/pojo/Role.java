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
 * Time: 10:11
 *
 * @Author: 憨八嘎
 */
@Data
public class Role {
    @NotNull
    @TableId(type = IdType.INPUT)
    private Integer id;
    @NotNull
    @Length(max = 20, message = "角色描述最长不超过20位")
    private String role;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
