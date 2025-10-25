package org.epsda.bookmanager.service;

import org.epsda.bookmanager.pojo.request.LoginRequest;
import org.epsda.bookmanager.pojo.response.LoginResponse;

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
    LoginResponse login(LoginRequest loginRequest);
}
