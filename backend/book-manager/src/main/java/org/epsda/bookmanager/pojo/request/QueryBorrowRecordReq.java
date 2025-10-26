package org.epsda.bookmanager.pojo.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 18483
 * Date: 2025/10/23
 * Time: 9:20
 *
 * @Author: 憨八嘎
 */
@Data
public class QueryBorrowRecordReq {
    @NotNull
    private Long userId; // 由前端必须传递
    private Integer pageNum = 1;
    private Integer pageSize = 10;
    private String username;
    private String email;
    private String phone;
    private String bookName;
    private Integer status;
    private Integer timeDesc; // 需要查询的时间描述，包括借阅日期（代号0）、应还日期（代号1）和实际归还日期（代号2）
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
