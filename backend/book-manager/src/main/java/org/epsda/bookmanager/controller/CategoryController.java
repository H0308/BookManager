package org.epsda.bookmanager.controller;

import jakarta.validation.constraints.NotNull;
import org.epsda.bookmanager.common.ResultWrapper;
import org.epsda.bookmanager.mapper.CategoryMapper;
import org.epsda.bookmanager.pojo.Book;
import org.epsda.bookmanager.pojo.Category;
import org.epsda.bookmanager.pojo.request.QueryCategoryReq;
import org.epsda.bookmanager.pojo.response.QueryCategoryResp;
import org.epsda.bookmanager.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 18483
 * Date: 2025/10/21
 * Time: 22:41
 *
 * @Author: 憨八嘎
 */
@RequestMapping("/category")
@RestController
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @RequestMapping("/query")
    public ResultWrapper<QueryCategoryResp> queryCategories(@Validated @RequestBody QueryCategoryReq queryCategoryReq) {
        return ResultWrapper.normal(categoryService.queryCategories(queryCategoryReq));
    }

    @RequestMapping("/add")
    public ResultWrapper<Boolean> addCategory(@Validated @RequestBody Category category) {
        return ResultWrapper.normal(categoryService.addCategory(category));
    }

    @RequestMapping("/edit")
    public ResultWrapper<Boolean> editCategory(@Validated @RequestBody Category category) {
        return ResultWrapper.normal(categoryService.editCategory(category));
    }

    @RequestMapping("/get")
    public ResultWrapper<Category> getCategoryById(@NotNull Long categoryId) {
        return ResultWrapper.normal(categoryService.getCategoryById(categoryId));
    }

    @RequestMapping("/delete")
    public ResultWrapper<Boolean> deleteCategory(@NotNull Long categoryId) {
        return ResultWrapper.normal(categoryService.deleteCategory(categoryId));
    }
}
