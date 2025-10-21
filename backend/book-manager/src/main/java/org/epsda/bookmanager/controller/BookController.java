package org.epsda.bookmanager.controller;

import jakarta.validation.constraints.NotNull;
import org.epsda.bookmanager.common.ResultWrapper;
import org.epsda.bookmanager.pojo.Book;
import org.epsda.bookmanager.pojo.request.QueryBookReq;
import org.epsda.bookmanager.pojo.response.QueryBookResp;
import org.epsda.bookmanager.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 18483
 * Date: 2025/10/21
 * Time: 10:27
 *
 * @Author: 憨八嘎
 */
@RequestMapping("/book")
@RestController
public class BookController {

    @Autowired
    private BookService bookService;

    // 获取图书
    @RequestMapping("/query")
    public ResultWrapper<QueryBookResp> queryBooks(@Validated @RequestBody QueryBookReq queryBookReq) {
        return ResultWrapper.normal(bookService.queryBooks(queryBookReq));
    }

    // 通过id获取图书
    @RequestMapping("/get")
    public ResultWrapper<Book> getBookById(@NotNull Long bookId) {
        return ResultWrapper.normal(bookService.getBookById(bookId));
    }

    // 新增图书
    @RequestMapping("/add")
    public ResultWrapper<Boolean> addBook(@Validated @RequestBody Book book) {
        return ResultWrapper.normal(bookService.addBook(book));
    }

    // 编辑图书
    @RequestMapping("/edit")
    public ResultWrapper<Boolean> editBook(@Validated @RequestBody Book book) {
        return ResultWrapper.normal(bookService.editBook(book));
    }

    // 删除图书
    @RequestMapping("/delete")
    public ResultWrapper<Boolean> deleteBook(@NotNull Long bookId) {
        return ResultWrapper.normal(bookService.deleteBook(bookId));
    }

    // 批量删除
    @RequestMapping("/batchDelete")
    public ResultWrapper<Boolean> batchDeleteBook(@RequestParam List<Long> bookIds) {
        return ResultWrapper.normal(bookService.batchDeleteBook(bookIds));
    }

}
