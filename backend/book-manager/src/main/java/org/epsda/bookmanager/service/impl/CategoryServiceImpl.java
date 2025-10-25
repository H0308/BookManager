package org.epsda.bookmanager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.epsda.bookmanager.constants.Constants;
import org.epsda.bookmanager.exception.BookManagerException;
import org.epsda.bookmanager.mapper.CategoryMapper;
import org.epsda.bookmanager.pojo.Category;
import org.epsda.bookmanager.pojo.request.QueryCategoryReq;
import org.epsda.bookmanager.pojo.response.QueryCategoryResp;
import org.epsda.bookmanager.pojo.response.vo.CategoryResp;
import org.epsda.bookmanager.service.CategoryService;
import org.epsda.bookmanager.utils.BeanUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 18483
 * Date: 2025/10/21
 * Time: 23:44
 *
 * @Author: 憨八嘎
 */
@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public QueryCategoryResp queryCategories(QueryCategoryReq queryCategoryReq) {
        Integer pageNum = queryCategoryReq.getPageNum();
        Integer pageSize = queryCategoryReq.getPageSize();

        Page<Category> categoryPage = new Page<>(pageNum, pageSize);

        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(queryCategoryReq.getCategoryName()), Category::getCategoryName, queryCategoryReq.getCategoryName())
                .eq(Category::getDeleteFlag, Constants.NOT_DELETE_FIELD_FLAG);

        Page<Category> pages = categoryMapper.selectPage(categoryPage, wrapper);
        List<Category> records = pages.getRecords();
        List<CategoryResp> allCategories = records.stream().map((category)-> (CategoryResp) BeanUtil.convert(category)).toList();

        return new QueryCategoryResp(pages.getCurrent(), pages.getPages(), pages.getTotal(), allCategories);
    }

    @Override
    public Boolean addCategory(Category category) {
        // 是否存在过指定的书籍
        Category existedNotDeleted = categoryMapper.selectOne(new LambdaQueryWrapper<Category>().eq(Category::getCategoryName, category.getCategoryName()).eq(Category::getDeleteFlag, Constants.NOT_DELETE_FIELD_FLAG));
        if (existedNotDeleted != null) {
            throw new BookManagerException("已经存在指定的分类，插入失败");
        }

        // 需要判断新增的分类是否存在过但是处于删除状态
        Category deleted = categoryMapper.selectOne(new LambdaQueryWrapper<Category>().eq(Category::getCategoryName, category.getCategoryName()).eq(Category::getDeleteFlag, Constants.DELETED_FIELD_FLAG));
        if (deleted != null) {
            Category newCategory = new Category();
            newCategory.setDeleteFlag(Constants.NOT_DELETE_FIELD_FLAG);
            return categoryMapper.update(newCategory, new LambdaQueryWrapper<Category>().eq(Category::getCategoryName, category.getCategoryName())) == 1;
        }

        // 此时直接插入即可
        return categoryMapper.insert(category) == 1;
    }

    @Override
    public Boolean editCategory(Category category) {
        if (!StringUtils.hasText(category.getCategoryName())) {
            throw new BookManagerException("图书分类名称为空，修改失败");
        }

        return categoryMapper.update(category, new LambdaQueryWrapper<Category>().eq(Category::getId, category.getId()).eq(Category::getDeleteFlag, Constants.NOT_DELETE_FIELD_FLAG)) == 1;
    }

    @Override
    public Category getCategoryById(Long categoryId) {
        return categoryMapper.selectById(categoryId);
    }

    @Override
    public Boolean deleteCategory(Long categoryId) {
        // 先检查当前分类下的图书量是否为0
        Category category = categoryMapper.selectById(categoryId);
        if (category.getCategoryCount() != 0) {
            throw new BookManagerException("当前分类下存在图书，无法删除");
        }

        // 检查该分类是否已经被标记为删除
        if (Constants.DELETED_FIELD_FLAG.equals(category.getDeleteFlag())) {
            throw new BookManagerException("该分类已经删除，删除失败");
        }

        category.setDeleteFlag(Constants.DELETED_FIELD_FLAG);
        return categoryMapper.update(category, new LambdaQueryWrapper<Category>().eq(Category::getId, categoryId)) == 1;
    }

    @Override
    public Boolean batchDeleteCategories(List<Long> categoryIds) {
        var count = 0;
        for (var categoryId : categoryIds) {
            Boolean ret = deleteCategory(categoryId);
            if (ret) {
                count++;
            }
        }

        return count == categoryIds.size();
    }
}
