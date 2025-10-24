package org.epsda.bookmanager.pojo.response.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 18483
 * Date: 2025/10/24
 * Time: 9:29
 *
 * @Author: 憨八嘎
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BillRecordResp {
    private String username;
    private String email;
    private String phone;
    private BigDecimal bills;
    private Integer status;
}
