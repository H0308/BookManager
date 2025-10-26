package org.epsda.bookmanager.controller;

import jakarta.validation.constraints.NotNull;
import org.epsda.bookmanager.common.ResultWrapper;
import org.epsda.bookmanager.pojo.response.AdminIndexDataResp;
import org.epsda.bookmanager.pojo.response.UserIndexDataResp;
import org.epsda.bookmanager.service.IndexService;
import org.epsda.bookmanager.utils.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 18483
 * Date: 2025/10/26
 * Time: 15:24
 *
 * @Author: 憨八嘎
 */
@RequestMapping("/index")
@RestController
public class IndexController {

    @Autowired
    private IndexService indexService;

    @RequestMapping("/adminHeader")
    @PreAuthorize("hasRole('管理员')")
    public ResultWrapper<AdminIndexDataResp> queryAdminHeaderData() {
        return ResultWrapper.normal(indexService.queryAdminHeaderData());
    }

    @RequestMapping("/userHeader")
    @PreAuthorize("hasRole('普通用户')")
    public ResultWrapper<UserIndexDataResp> queryUserHeaderData(@NotNull Long userId) {
        // 水平越权校验
        SecurityUtil.checkHorizontalOverstepped(userId);

        return ResultWrapper.normal(indexService.queryUserHeaderData(userId));
    }
}
