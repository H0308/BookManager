package org.epsda.bookmanager.controller;

import jakarta.validation.constraints.NotNull;
import org.epsda.bookmanager.common.ResultWrapper;
import org.epsda.bookmanager.pojo.Book;
import org.epsda.bookmanager.pojo.request.QueryBookReq;
import org.epsda.bookmanager.pojo.response.QueryBookResp;
import org.epsda.bookmanager.pojo.response.dto.CustomUserDetails;
import org.epsda.bookmanager.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    @PreAuthorize("hasAnyRole('管理员', '普通用户')")
    public ResultWrapper<QueryBookResp> queryBooks(@Validated @RequestBody QueryBookReq queryBookReq) {
        return ResultWrapper.normal(bookService.queryBooks(queryBookReq));
    }

    // 通过id获取图书
    @RequestMapping("/get")
    @PreAuthorize("hasAnyRole('管理员', '普通用户')")
    public ResultWrapper<Book> getBookById(@NotNull Long bookId) {
        return ResultWrapper.normal(bookService.getBookById(bookId));
    }

    // 新增图书
    // 只有管理员可以新增图书
    @RequestMapping("/add")
    @PreAuthorize("hasRole('管理员')")
    public ResultWrapper<Boolean> addBook(@Validated @RequestBody Book book) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
        System.out.println("当前操作人权限为：" + principal.getRoleId());
        System.out.println(principal.getAuthorities());
        return ResultWrapper.normal(bookService.addBook(book));
    }

    // 编辑图书
    // 只有管理员可以新增图书
    @RequestMapping("/edit")
    @PreAuthorize("hasRole('管理员')")
    public ResultWrapper<Boolean> editBook(@Validated @RequestBody Book book) {
        return ResultWrapper.normal(bookService.editBook(book));
    }

    // 删除图书
    // 只有管理员可以新增图书
    @RequestMapping("/delete")
    @PreAuthorize("hasRole('管理员')")
    public ResultWrapper<Boolean> deleteBook(@NotNull Long bookId) {
        return ResultWrapper.normal(bookService.deleteBook(bookId));
    }

    // 批量删除
    // 只有管理员可以新增图书
    @RequestMapping("/batchDelete")
    @PreAuthorize("hasRole('管理员')")
    public ResultWrapper<Boolean> batchDeleteBook(@RequestParam List<Long> bookIds) {
        return ResultWrapper.normal(bookService.batchDeleteBook(bookIds));
    }

    // 获取书籍有效个数
    // 普通用户和管理员都可以访问
    @RequestMapping("/getAvailableCount")
    @PreAuthorize("hasAnyRole('管理员', '普通用户')")
    public ResultWrapper<Integer> getAvailableCount(@Validated @RequestBody Book book) {
        return ResultWrapper.normal(bookService.getAvailableCount(book));
    }
}
