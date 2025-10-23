package org.epsda.bookmanager.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.epsda.bookmanager.pojo.Category;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 18483
 * Date: 2025/10/21
 * Time: 10:21
 *
 * @Author: 憨八嘎
 */
@Mapper
public interface CategoryMapper extends BaseMapper<Category> {

    // 根据分类名称获取到分类
    @Select("select * from category where category_name like concat('%', #{categoryName}, '%')")
    List<Category> selectCategoryLikeCategoryName(String categoryName);
}
