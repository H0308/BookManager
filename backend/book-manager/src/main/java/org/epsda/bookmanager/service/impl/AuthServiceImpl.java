package org.epsda.bookmanager.service.impl;

import org.epsda.bookmanager.constants.Constants;
import org.epsda.bookmanager.exception.BookManagerException;
import org.epsda.bookmanager.mapper.UserMapper;
import org.epsda.bookmanager.pojo.User;
import org.epsda.bookmanager.pojo.request.LoginReq;
import org.epsda.bookmanager.pojo.request.RegisterReq;
import org.epsda.bookmanager.pojo.response.LoginResp;
import org.epsda.bookmanager.pojo.response.dto.RegisterMail;
import org.epsda.bookmanager.service.AuthService;
import org.epsda.bookmanager.utils.JsonUtil;
import org.epsda.bookmanager.utils.JwtUtil;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public LoginResp login(LoginReq loginReq) {
        String email = loginReq.getEmail();
        String password = loginReq.getPassword();
        // 进行用户存在性校验
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));

        // 通过校验后，生成JWT Token
        User user = userMapper.selectByPreciseEmail(email);
        if (Constants.DELETED_FIELD_FLAG.equals(user.getDeleteFlag())) {
            throw new BookManagerException("当前用户已经处于注销状态，无法登录");
        }

        // 普通用户需要输入验证码
        if (Constants.USER_FLAG.equals(user.getRoleId()) &&
                (loginReq.getInputCaptcha() == null || !StringUtils.hasText(loginReq.getInputCaptcha()))) {
            throw new BookManagerException("普通用户需要输入验证码");
        }

        String token = JwtUtil.generateToken(user.getEmail(), user.getUsername());

        // 返回登录响应
        // 此处可以对这个userId参数使用非对称密钥的公钥进行加密，例如RSA
        // 前端传递该参数给后端，后端使用私钥解密
        return new LoginResp(user.getId(), user.getUsername(), user.getRoleId(), token);
    }

    @Override
    public Boolean register(RegisterReq registerReq) {
        // 判断注册的用户是否存在
        String username = registerReq.getUsername();
        String userIdCard = registerReq.getUserIdCard();
        String email = registerReq.getEmail();
        String phone = registerReq.getPhone();

        if (userMapper.selectByPreciseUsername(username) != null ||
            userMapper.selectByPrecisePhone(phone) != null ||
            userMapper.selectByPreciseUserIdCard(userIdCard) != null ||
            userMapper.selectByPreciseEmail(email) != null) {
            throw new BookManagerException("当前用户已存在无法继续注册");
        }

        User user = new User();
        user.setUsername(username);
        user.setUserIdCard(userIdCard);
        user.setEmail(email);
        user.setPhone(phone);
        user.setPassword(registerReq.getPassword());
        user.setAddress(registerReq.getAddress());

        String userMail = JsonUtil.toJson(new RegisterMail(user.getUsername(), user.getEmail()));
        rabbitTemplate.convertAndSend(Constants.RABBITMQ_USER_EXCHANGE, "", userMail);

        return userMapper.insert(user) == 1;
    }
}
