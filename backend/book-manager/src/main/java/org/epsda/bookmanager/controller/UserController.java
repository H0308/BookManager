package org.epsda.bookmanager.controller;

import jakarta.validation.constraints.NotNull;
import org.epsda.bookmanager.common.ResultWrapper;
import org.epsda.bookmanager.pojo.User;
import org.epsda.bookmanager.pojo.request.QueryUserReq;
import org.epsda.bookmanager.pojo.response.QueryUserResp;
import org.epsda.bookmanager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 18483
 * Date: 2025/10/22
 * Time: 18:23
 *
 * @Author: 憨八嘎
 */
@RequestMapping("/user")
@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping("/query")
    public ResultWrapper<QueryUserResp> queryUsers(@Validated @RequestBody QueryUserReq queryUserReq) {
        return ResultWrapper.normal(userService.queryUsers(queryUserReq));
    }

    @RequestMapping("/add")
    public ResultWrapper<Boolean> addUser(@Validated @RequestBody User user) {
        return ResultWrapper.normal(userService.addUser(user));
    }

    @RequestMapping("/get")
    public ResultWrapper<User> getUserById(@NotNull Long userId) {
        return ResultWrapper.normal(userService.getUserById(userId));
    }

    @RequestMapping("/edit")
    public ResultWrapper<Boolean> editUser(@Validated @RequestBody User user) {
        return ResultWrapper.normal(userService.editUser(user));
    }

    @RequestMapping("/delete")
    public ResultWrapper<Boolean> deleteUser(@NotNull Long userId) {
        return ResultWrapper.normal(userService.deleteUser(userId));
    }

    @RequestMapping("/batchDelete")
    public ResultWrapper<Boolean> batchDeleteUser(@RequestParam List<Long> userIds) {
        return ResultWrapper.normal(userService.batchDeleteUser(userIds));
    }
}
