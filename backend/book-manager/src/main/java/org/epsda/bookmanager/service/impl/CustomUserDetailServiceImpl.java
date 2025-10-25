package org.epsda.bookmanager.service.impl;

import org.epsda.bookmanager.constants.Constants;
import org.epsda.bookmanager.exception.BookManagerException;
import org.epsda.bookmanager.mapper.RoleMapper;
import org.epsda.bookmanager.mapper.UserMapper;
import org.epsda.bookmanager.pojo.Role;
import org.epsda.bookmanager.pojo.User;
import org.epsda.bookmanager.pojo.response.dto.CustomUserDetails;
import org.epsda.bookmanager.service.CustomUserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 18483
 * Date: 2025/10/25
 * Time: 15:55
 *
 * @Author: 憨八嘎
 */
@Service
public class CustomUserDetailServiceImpl implements CustomUserDetailService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RoleMapper roleMapper;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // 从数据库通过邮箱获取到用户和角色信息
        User user = userMapper.selectByPreciseEmail(email);
        if (user == null) {
            throw new BookManagerException("邮箱错误或者用户未注册");
        }

        Role role = roleMapper.selectById(user.getRoleId());

        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(Constants.SECURITY_ROLE_PREFIX + role.getRole()));

        return new CustomUserDetails(user.getUsername(), user.getPassword(), user.getId(), role.getId(), authorities);
    }
}
