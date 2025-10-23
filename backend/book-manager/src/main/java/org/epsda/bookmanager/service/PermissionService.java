package org.epsda.bookmanager.service;

import jakarta.validation.constraints.NotNull;
import org.epsda.bookmanager.pojo.request.QueryRoleReq;
import org.epsda.bookmanager.pojo.request.RoleChangeReq;
import org.epsda.bookmanager.pojo.response.QueryRoleResp;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 18483
 * Date: 2025/10/23
 * Time: 23:16
 *
 * @Author: 憨八嘎
 */
public interface PermissionService {
    QueryRoleResp queryRoles(QueryRoleReq queryRoleReq);

    Boolean changeRole(RoleChangeReq roleChangeReq);
}
