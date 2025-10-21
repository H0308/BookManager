package org.epsda.bookmanager.pojo.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.epsda.bookmanager.pojo.Book;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 18483
 * Date: 2025/10/21
 * Time: 10:28
 *
 * @Author: 憨八嘎
 */
@Data
@AllArgsConstructor
public class QueryBookResp {
    private Long currentPage;
    private Long totalPages;
    private Long totalCount;
    private List<BookWithAvailableCount> currentBooks;
}
