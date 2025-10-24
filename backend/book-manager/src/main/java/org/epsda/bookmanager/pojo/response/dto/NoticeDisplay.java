package org.epsda.bookmanager.pojo.response.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.epsda.bookmanager.constants.Constants;

import java.time.LocalDateTime;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 18483
 * Date: 2025/10/24
 * Time: 20:14
 *
 * @Author: 憨八嘎
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NoticeDisplay {
    private String username;
    private String title;
    private String content;
    private Integer type;
    private Integer status;
    private LocalDateTime updateTime;
}
