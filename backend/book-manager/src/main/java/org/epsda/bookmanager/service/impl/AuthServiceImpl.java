package org.epsda.bookmanager.service.impl;

import org.epsda.bookmanager.mapper.UserMapper;
import org.epsda.bookmanager.pojo.User;
import org.epsda.bookmanager.pojo.request.LoginRequest;
import org.epsda.bookmanager.pojo.response.LoginResponse;
import org.epsda.bookmanager.service.AuthService;
import org.epsda.bookmanager.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 18483
 * Date: 2025/10/25
 * Time: 15:21
 *
 * @Author: 憨八嘎
 */
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserMapper userMapper;

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        String email = loginRequest.getEmail();
        String password = loginRequest.getPassword();
        // 进行用户存在性校验
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));

        // 通过校验后，生成JWT Token
        User user = userMapper.selectByPreciseEmail(email);
        String token = JwtUtil.generateToken(user.getEmail(), user.getUsername());

        // 返回登录响应
        return new LoginResponse(user.getUsername(), user.getRoleId(), token);
    }
}
