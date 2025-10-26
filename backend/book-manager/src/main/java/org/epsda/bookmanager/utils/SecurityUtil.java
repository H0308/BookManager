package org.epsda.bookmanager.utils;

import org.epsda.bookmanager.exception.BookManagerException;
import org.epsda.bookmanager.pojo.response.dto.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 18483
 * Date: 2025/10/25
 * Time: 22:12
 *
 * @Author: 憨八嘎
 */
public class SecurityUtil {
    // 防止水平越权
    public static void checkHorizontalOverstepped(Long frontendUserId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
        Long securityUserId = principal.getUserId();
        if (!securityUserId.equals(frontendUserId)) {
            throw new BookManagerException("不允许访问非当前登录用户的资源");
        }
    }

    // 获取当前用户角色ID
    public static Long getRoleIdFromPrinciple() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
        return principal.getRoleId();
    }
}
