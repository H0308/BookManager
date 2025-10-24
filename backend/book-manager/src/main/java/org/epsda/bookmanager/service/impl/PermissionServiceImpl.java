package org.epsda.bookmanager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.epsda.bookmanager.constants.Constants;
import org.epsda.bookmanager.exception.BookManagerException;
import org.epsda.bookmanager.mapper.RoleMapper;
import org.epsda.bookmanager.mapper.UserMapper;
import org.epsda.bookmanager.pojo.Role;
import org.epsda.bookmanager.pojo.User;
import org.epsda.bookmanager.pojo.request.QueryRoleReq;
import org.epsda.bookmanager.pojo.request.RoleChangeReq;
import org.epsda.bookmanager.pojo.response.QueryRoleResp;
import org.epsda.bookmanager.pojo.response.vo.RoleResp;
import org.epsda.bookmanager.service.PermissionService;
import org.epsda.bookmanager.utils.BeanUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 18483
 * Date: 2025/10/23
 * Time: 23:16
 *
 * @Author: 憨八嘎
 */
@Service
public class PermissionServiceImpl implements PermissionService {

    @Value("${admin.adminName}")
    private String adminName;

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RoleMapper roleMapper;

    @Override
    public QueryRoleResp queryRoles(QueryRoleReq queryRoleReq) {
        Integer pageNum = queryRoleReq.getPageNum();
        Integer pageSize = queryRoleReq.getPageSize();

        Page<User> page = new Page<>(pageNum, pageSize);
        List<RoleResp> roleResps = new ArrayList<>();

        List<Long> roleIds = new ArrayList<>();
        roleIds.add(0L);
        String role = queryRoleReq.getRole();
        if (StringUtils.hasText(role)) {
            LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
            wrapper.like(Role::getRole, role);
            List<Role> roles = roleMapper.selectList(wrapper);
            roleIds = roles.stream().map(Role::getId).toList();
        }
        Page<User> pages = userMapper.selectPage(page,
                new LambdaQueryWrapper<User>().in(StringUtils.hasText(role), User::getRoleId, roleIds)
                        .ne(User::getUsername, adminName)
                        .ne(User::getDeleteFlag, Constants.USER_UNAVAILABLE_FLAG));
        List<User> records = pages.getRecords();
        for (var record : records) {
            String username = record.getUsername();
            String email = record.getEmail();
            String phone = record.getPhone();
            Long roleId = record.getRoleId();
            Role recordRole = roleMapper.selectById(roleId);
            RoleResp roleResp = BeanUtil.generateRoleResp(username, email, phone, recordRole.getRole());
            roleResps.add(roleResp);
        }

        return new QueryRoleResp(pages.getCurrent(), pages.getPages(), pages.getTotal(), roleResps);
    }

    @Override
    public Boolean changeRole(RoleChangeReq roleChangeReq) {
        Long userId = roleChangeReq.getUserId();
        User user = userMapper.selectById(userId);
        // 先判断当前用户是否已经被标记为注销
        if (Constants.USER_UNAVAILABLE_FLAG.equals(user.getDeleteFlag())) {
            throw new BookManagerException("当前用户已经被标记为注销，无法修改权限");
        }

        // 直接修改状态
        user.setRoleId(roleChangeReq.getRoleId());
        return userMapper.update(user, new LambdaQueryWrapper<User>().eq(User::getId, userId)) == 1;
    }
}
