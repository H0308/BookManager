package org.epsda.bookmanager.controller;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.epsda.bookmanager.common.ResultWrapper;
import org.epsda.bookmanager.constants.Constants;
import org.epsda.bookmanager.config.Captcha;
import org.epsda.bookmanager.pojo.request.LoginReq;
import org.epsda.bookmanager.pojo.request.RegisterReq;
import org.epsda.bookmanager.pojo.response.LoginResp;
import org.epsda.bookmanager.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Date;

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

    // 注入验证码配置类
    @Autowired
    private Captcha captcha;

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

    // 获取验证码
    @GetMapping("/getCaptcha")
    public void getCaptcha(HttpServletResponse response, HttpSession session){
        //定义图形验证码的长和宽
        LineCaptcha lineCaptcha = CaptchaUtil.createLineCaptcha(captcha.getWidth(), captcha.getHeight());
        try(ServletOutputStream outputStream = response.getOutputStream()) {
            lineCaptcha.write(outputStream);
            String code = lineCaptcha.getCode();
            // 打印验证码
            System.out.println(code);
            // 将验证码的值存入到Session中用于校验
            session.setAttribute(captcha.getSession().getKey_name(), code);
            session.setAttribute(captcha.getSession().getDate_name(), new Date());
            // 设置返回类型
            response.setContentType("image/png");
            // 防止缓存
            response.setHeader("Pragma", "No-cache");
            // 设置编码
            response.setCharacterEncoding("utf-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 校验验证码
    @PostMapping("/check")
    public boolean checkCaptcha(HttpSession session, String inputCaptcha) {
        System.out.println("前端传递的值为：" + inputCaptcha);
        String validCaptcha  = (String) session.getAttribute(captcha.getSession().getKey_name());
        Date validDate = (Date) session.getAttribute(captcha.getSession().getDate_name());
        // 输入的验证码为空或者获取验证码时间与当前校验时间差值大于等于一分钟时直接返回false
        if(!StringUtils.hasLength(inputCaptcha) ||
                (System.currentTimeMillis() - validDate.getTime() >= Constants.VALID_CAPTCHA_TIME)) {
            return false;
        }

        return validCaptcha.equalsIgnoreCase(inputCaptcha);
    }
}
