package org.epsda.bookmanager.pojo.response;

import lombok.Data;
import org.epsda.bookmanager.pojo.Book;

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
public class BookWithAvailableCount {
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
