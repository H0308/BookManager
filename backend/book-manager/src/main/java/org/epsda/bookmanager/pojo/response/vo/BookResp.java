package org.epsda.bookmanager.pojo.response.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 18483
 * Date: 2025/10/21
 * Time: 12:37
 *
 * @Author: 憨八嘎
 */
@Data
public class BookResp {
    private Long id;
    private String bookName;
    private String isbn;
    private String author;
    private String publisher;
    private String category;
    private Integer totalCount;
    private Integer availableCount;
    private BigDecimal price;
    private Integer status;
}
