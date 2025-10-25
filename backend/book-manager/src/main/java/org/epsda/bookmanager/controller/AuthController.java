package org.epsda.bookmanager.controller;

import org.epsda.bookmanager.common.ResultWrapper;
import org.epsda.bookmanager.pojo.request.LoginReq;
import org.epsda.bookmanager.pojo.request.RegisterReq;
import org.epsda.bookmanager.pojo.response.LoginResp;
import org.epsda.bookmanager.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 18483
 * Date: 2025/10/25
 * Time: 15:19
 *
 * @Author: 憨八嘎
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @RequestMapping("/login")
    public ResultWrapper<LoginResp> login(@Validated @RequestBody LoginReq loginReq) {
        return ResultWrapper.normal(authService.login(loginReq));
    }

    @RequestMapping("/register")
    public ResultWrapper<Boolean> register(@Validated @RequestBody RegisterReq registerReq) {
        return ResultWrapper.normal(authService.register(registerReq));
    }
}
