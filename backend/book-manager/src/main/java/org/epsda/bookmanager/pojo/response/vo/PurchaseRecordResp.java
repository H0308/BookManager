package org.epsda.bookmanager.pojo.response.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 18483
 * Date: 2025/10/23
 * Time: 19:04
 *
 * @Author: 憨八嘎
 */
@Data
public class PurchaseRecordResp {
    private String username;
    private String email;
    private String phone;
    private String bookName;
    private Integer purchaseCount;
    private BigDecimal purchasePrice;
    private Integer status;
}
