package org.epsda.bookmanager.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.epsda.bookmanager.constants.Constants;
import org.epsda.bookmanager.exception.BookManagerException;
import org.epsda.bookmanager.service.CustomUserDetailService;
import org.epsda.bookmanager.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 18483
 * Date: 2025/10/25
 * Time: 15:29
 *
 * @Author: 憨八嘎
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private CustomUserDetailService customUserDetailService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // 基于JWT进行安全校验
        // 从请求中拿到请求头
        String header = request.getHeader(Constants.TOKEN_HEADER);
        String token = null;
        // 判断提取到的JWT是否包含Bearer头部
        if (header != null && header.startsWith(Constants.TOKEN_START_FLAG)) {
            token = header.substring(7);
        }

        // 从Token中获取邮箱
        String email = null;
        if (token != null) {
            email = JwtUtil.extractEmail(token);
        }

        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // 从数据库获取到用户信息用于信息比对
            UserDetails userDetails = customUserDetailService.loadUserByUsername(email);
            if (!JwtUtil.validateToken(token, userDetails.getUsername())) {
                throw new BookManagerException("Token信息和当前用户信息不匹配，校验失败");
            }

            // 存储用户信息
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

            // 设置认证详情
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            // 设置安全上下文
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }

        // 继续后续的过滤器
        filterChain.doFilter(request, response);
    }
}
