package org.epsda.bookmanager.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.epsda.bookmanager.pojo.BorrowRecord;

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
public interface BorrowRecordMapper extends BaseMapper<BorrowRecord> {
    // 根据book_id进行查询
    @Select("select * from borrow_record where book_id = #{bookId} and status != 1")
    List<BorrowRecord> getBorrowRecordByBookId(Long bookId);
}
