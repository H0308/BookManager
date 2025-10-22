package org.epsda.bookmanager.service;

import jakarta.validation.constraints.NotNull;
import org.epsda.bookmanager.common.ResultWrapper;
import org.epsda.bookmanager.pojo.Book;
import org.epsda.bookmanager.pojo.Category;
import org.epsda.bookmanager.pojo.request.QueryCategoryReq;
import org.epsda.bookmanager.pojo.response.QueryCategoryResp;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 18483
 * Date: 2025/10/21
 * Time: 23:42
 *
 * @Author: 憨八嘎
 */
public interface CategoryService {
    QueryCategoryResp queryCategories(QueryCategoryReq queryCategoryReq);

    Boolean addCategory(Category category);

    Boolean editCategory(Category category);

    Category getCategoryById(Long categoryId);

    Boolean deleteCategory(@NotNull Long categoryId);

    Boolean batchDeleteCategories(List<Long> categoryIds);
}
