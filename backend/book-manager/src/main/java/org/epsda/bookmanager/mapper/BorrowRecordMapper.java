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
    // 根据book_id进行查询，只考虑借阅中的情况，逾期只是未缴费，但是不属于没有还书
    @Select("select * from borrow_record where book_id = #{bookId} and status = 0 and delete_flag = 0")
    List<BorrowRecord> getBorrowRecordByBookId(Long bookId);
    // 根据user_id进行查询
    @Select("select * from borrow_record where user_id = #{userId} and status != 1 and delete_flag = 0")
    List<BorrowRecord> getBorrowRecordByUserId(Long userId);
}
