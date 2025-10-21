package org.epsda.bookmanager.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 18483
 * Date: 2025/10/21
 * Time: 10:08
 *
 * @Author: 憨八嘎
 */
@Data
@TableName("purchase_record")
public class PurchaseRecord {
    @TableId(type = IdType.AUTO)
    private Integer id;
    @NotNull
    private Integer userId;
    @NotNull
    private Integer bookId;
    @NotNull
    private Integer purchaseCount;
    @NotNull
    private BigDecimal purchasePrice;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
