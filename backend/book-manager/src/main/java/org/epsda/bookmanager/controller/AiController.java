package org.epsda.bookmanager.controller;

import org.epsda.bookmanager.exception.BookManagerException;
import org.epsda.bookmanager.pojo.request.AiReq;
import org.epsda.bookmanager.service.UserService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 18483
 * Date: 2025/10/24
 * Time: 21:25
 *
 * @Author: 憨八嘎
 */
@RequestMapping("/ai")
@RestController
public class AiController {

    @Autowired
    private ChatClient chatClient;
    @Autowired
    private UserService userService;

    @RequestMapping(value = "/chat", produces = "text/html;charset=utf-8")
    public Flux<String> chatClientStream(@Validated @RequestBody AiReq aiReq) {
        String username = userService.getUserById(aiReq.getUserId()).getUsername();
        String defaultMessage = "你是图书管理系统的人工智能助手小书，打招呼时需要带上姓名：" + username;
        return chatClient.prompt()
                .system(defaultMessage)
                .user(aiReq.getMessage()).stream().content();
    }
}
