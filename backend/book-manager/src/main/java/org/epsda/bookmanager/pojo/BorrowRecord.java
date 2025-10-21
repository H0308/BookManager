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
 * Time: 9:58
 *
 * @Author: 憨八嘎
 */
@Data
@TableName("borrow_record")
public class BorrowRecord {
    @TableId(type = IdType.AUTO)
    private Integer id;
    @NotNull
    private Integer userId;
    @NotNull
    private Integer bookId;
    private LocalDateTime borrowTime;
    @NotNull
    private LocalDateTime preReturnTime;
    private LocalDateTime realReturnTime;
    private Integer status;
    private BigDecimal fine;
    private Integer deleteFlag;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
