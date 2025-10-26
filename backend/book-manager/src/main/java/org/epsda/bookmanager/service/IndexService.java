package org.epsda.bookmanager.service;

import jakarta.validation.constraints.NotNull;
import org.epsda.bookmanager.pojo.response.AdminIndexDataResp;
import org.epsda.bookmanager.pojo.response.UserIndexDataResp;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 18483
 * Date: 2025/10/26
 * Time: 15:28
 *
 * @Author: 憨八嘎
 */
public interface IndexService {
    AdminIndexDataResp queryAdminHeaderData();

    UserIndexDataResp queryUserHeaderData(@NotNull Long userId);
}
