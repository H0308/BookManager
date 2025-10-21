package org.epsda.bookmanager.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.epsda.bookmanager.pojo.BorrowRecord;
import org.epsda.bookmanager.pojo.PurchaseRecord;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 18483
 * Date: 2025/10/21
 * Time: 10:22
 *
 * @Author: 憨八嘎
 */
@Mapper
public interface PurchaseRecordMapper extends BaseMapper<PurchaseRecord> {
    // 根据book_id进行查询出未支付的图书
    @Select("select * from purchase_record where book_id = #{bookId} and status = 0")
    List<BorrowRecord> getPurchaseRecordByBookId(Long bookId);
}
