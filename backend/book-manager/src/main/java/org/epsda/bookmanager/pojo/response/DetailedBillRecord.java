package org.epsda.bookmanager.pojo.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 18483
 * Date: 2025/10/24
 * Time: 11:20
 *
 * @Author: 憨八嘎
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DetailedBillRecord {
    private BigDecimal fine;
    private BigDecimal purchasePrice;
    private BigDecimal totalBill;
}
