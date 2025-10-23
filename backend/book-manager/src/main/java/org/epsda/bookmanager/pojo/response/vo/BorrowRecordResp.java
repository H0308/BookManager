package org.epsda.bookmanager.pojo.response.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 18483
 * Date: 2025/10/23
 * Time: 9:21
 *
 * @Author: 憨八嘎
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BorrowRecordResp {
    private String username;
    private String email;
    private String phone;
    private String bookName;
    private String isbn;
    private LocalDateTime borrowTime;
    private LocalDateTime preReturnTime;
    private LocalDateTime realReturnTime;
    private Integer status;
    private BigDecimal fine;
}
