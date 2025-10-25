package org.epsda.bookmanager.pojo.response.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 18483
 * Date: 2025/10/24
 * Time: 15:44
 *
 * @Author: 憨八嘎
 */
@Data
@ColumnWidth(20)
public class BillRecordExcel {
    @ExcelProperty("账单ID")
    private Long id;
    @ExcelProperty("用户ID")
    private Long userId;
    @ExcelProperty("用户名")
    private String username;
    @ExcelProperty("用户电话")
    private String phone;
    @ExcelProperty("用户邮箱")
    private String email;
    @ExcelProperty("借阅ID")
    private Long borrowId;
    @ExcelProperty("借阅图书名称")
    private String borrowBookName;
    @ExcelProperty("借阅图书ISBN编号")
    private String borrowBookIsbn;
    @ExcelProperty("购买图书ID")
    private Long purchaseId;
    @ExcelProperty("购买图书名称")
    private String purchaseBookName;
    @ExcelProperty("购买图书ISBN编号")
    private String purchaseBookIsbn;
    @ExcelProperty("罚金金额")
    private BigDecimal fine;
    @ExcelProperty("购买金额")
    private BigDecimal purchasePrice;
    @ExcelProperty("总金额")
    private BigDecimal totalBill;
    @ExcelProperty("账单状态")
    private String status;
    @ExcelProperty("账单创建时间")
    private LocalDateTime createTime;
    @ExcelProperty("账单更新时间")
    private LocalDateTime updateTime;
}
