package org.epsda.bookmanager.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 18483
 * Date: 2025/10/21
 * Time: 9:56
 *
 * @Author: 憨八嘎
 */
@Data
@TableName("bill_record")
public class BillRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    @NotNull
    private Long userId;
    @NotNull
    private Long borrowId;
    @NotNull
    private Long purchaseId;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
