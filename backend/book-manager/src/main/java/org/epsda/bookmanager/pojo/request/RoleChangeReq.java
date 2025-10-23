package org.epsda.bookmanager.pojo.request;

import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 18483
 * Date: 2025/10/24
 * Time: 0:59
 *
 * @Author: 憨八嘎
 */
@Data
public class RoleChangeReq {
    private Long userId;
    private Long roleId;
}
