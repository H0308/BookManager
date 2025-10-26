package org.epsda.bookmanager.pojo.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

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
public class QueryPurchaseRecordReq {
    @NotNull
    private Long userId; // 由前端必须传递
    private Integer pageNum = 1;
    private Integer pageSize = 10;
    private String username;
    private String email;
    private String phone;
    private String bookName;
    private Integer status;
}
