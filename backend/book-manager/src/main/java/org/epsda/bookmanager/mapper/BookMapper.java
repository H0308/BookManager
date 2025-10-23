package org.epsda.bookmanager.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.epsda.bookmanager.pojo.Book;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 18483
 * Date: 2025/10/21
 * Time: 10:20
 *
 * @Author: 憨八嘎
 */
@Mapper
public interface BookMapper extends BaseMapper<Book> {
    // 根据图书名称获取到图书信息
    @Select("select * from book where book_name like concat('%', #{bookName}, '%')")
    List<Book> selectByBookName(String bookName);
}
