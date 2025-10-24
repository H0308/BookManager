package org.epsda.bookmanager.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.epsda.bookmanager.pojo.BillRecord;

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
public interface BillRecordMapper extends BaseMapper<BillRecord> {

    @Select("select * from bill_record where user_id = #{userId} and status != 1")
    BillRecord selectBillRecordByUserId(Long userId);

    @Select("select * from bill_record where borrow_id = #{borrowId}")
    BillRecord selectBillRecordByBorrowId(Long borrowId);

    @Select("select * from bill_record where purchase_id = #{purchaseId}")
    BillRecord selectBillRecordByPurchaseId(Long purchaseId);
}
