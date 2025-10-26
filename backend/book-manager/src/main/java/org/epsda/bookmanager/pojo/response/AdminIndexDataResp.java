package org.epsda.bookmanager.pojo.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 18483
 * Date: 2025/10/26
 * Time: 15:26
 *
 * @Author: 憨八嘎
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminIndexDataResp {
    private Long readerCount;
    private Long bookCount;
    private Long borrowCount;
    private Long purchaseCount;
}
