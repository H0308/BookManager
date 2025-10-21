package org.epsda.bookmanager.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 18483
 * Date: 2025/10/21
 * Time: 9:50
 *
 * @Author: 憨八嘎
 */
@Data
@TableName("book")
public class Book {
    @TableId(type = IdType.AUTO)
    private Long id;
    @NotNull
    @Length(message = "书名最长不超过20位", max = 20)
    private String bookName;
    @NotNull
    @Length(message = "ISBN编号最长不超过50位", max = 50)
    private String isbn;
    @NotNull
    @Length(message = "作者最长不超过20位", max = 20)
    private String author;
    @NotNull
    @Length(message = "出版社最长不超过50位", max = 50)
    private String publisher;
    @NotNull
    private Integer categoryId;
    @NotNull
    private Integer totalCount;
    @NotNull
    private BigDecimal price;
    private Integer status;
    private Integer deleteFlag;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
