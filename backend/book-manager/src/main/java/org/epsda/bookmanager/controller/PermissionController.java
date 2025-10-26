package org.epsda.bookmanager.controller;

import org.epsda.bookmanager.common.ResultWrapper;
import org.epsda.bookmanager.pojo.request.QueryRoleReq;
import org.epsda.bookmanager.pojo.request.RoleChangeReq;
import org.epsda.bookmanager.pojo.response.QueryRoleResp;
import org.epsda.bookmanager.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 18483
 * Date: 2025/10/23
 * Time: 23:15
 *
 * @Author: 憨八嘎
 */
@RequestMapping("/permission")
@RestController
@PreAuthorize("hasRole('管理员')")
public class PermissionController {

    @Autowired
    private PermissionService permissionService;

    @RequestMapping("/query")
    public ResultWrapper<QueryRoleResp> queryRoles(@Validated @RequestBody QueryRoleReq queryRoleReq) {
        return ResultWrapper.normal(permissionService.queryRoles(queryRoleReq));
    }

    @RequestMapping("/change")
    public ResultWrapper<Boolean> changeRole(@Validated @RequestBody RoleChangeReq roleChangeReq) {
        return ResultWrapper.normal(permissionService.changeRole(roleChangeReq));
    }
}
