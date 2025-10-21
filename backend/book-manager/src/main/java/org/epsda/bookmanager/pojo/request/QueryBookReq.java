package org.epsda.bookmanager.pojo.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 18483
 * Date: 2025/10/21
 * Time: 10:25
 *
 * @Author: 憨八嘎
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QueryBookReq {
    private Integer pageNum;
    // 默认一页内容为10条
    private Integer pageSize = 10;
    private String bookName;
    private String isbn;
    private String author;
    private String publisher;
    private String categoryName;
}
