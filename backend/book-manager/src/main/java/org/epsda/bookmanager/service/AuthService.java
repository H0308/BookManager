package org.epsda.bookmanager.service;

import org.epsda.bookmanager.pojo.request.LoginReq;
import org.epsda.bookmanager.pojo.request.RegisterReq;
import org.epsda.bookmanager.pojo.response.LoginResp;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 18483
 * Date: 2025/10/25
 * Time: 15:21
 *
 * @Author: 憨八嘎
 */
public interface AuthService {
    LoginResp login(LoginReq loginReq);

    Boolean register(RegisterReq registerReq);
}
