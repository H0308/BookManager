package org.epsda.bookmanager.service;

import jakarta.validation.constraints.NotNull;
import org.epsda.bookmanager.pojo.Book;
import org.epsda.bookmanager.pojo.request.QueryBookReq;
import org.epsda.bookmanager.pojo.response.QueryBookResp;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 18483
 * Date: 2025/10/21
 * Time: 10:29
 *
 * @Author: 憨八嘎
 */
public interface BookService {
    // 根据指定条件查询
    QueryBookResp queryBooks(QueryBookReq queryBookReq);

    Boolean addBook(Book book);

    Boolean editBook(Book book);

    Book getBookById(Long id);

    Boolean deleteBook(@NotNull Long id);

    Boolean batchDeleteBook(List<Long> bookIds);
}
