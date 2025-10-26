package org.epsda.bookmanager.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.SneakyThrows;
import org.epsda.bookmanager.common.ResultWrapper;
import org.epsda.bookmanager.config.AvatarPath;
import org.epsda.bookmanager.exception.BookManagerException;
import org.epsda.bookmanager.pojo.User;
import org.epsda.bookmanager.pojo.response.dto.CustomUserDetails;
import org.epsda.bookmanager.service.UserService;
import org.epsda.bookmanager.utils.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

/**
* Created with IntelliJ IDEA.
* Description:
* User: 18483
* Date: 2025/10/26
* Time: 13:05
* @Author: 憨八嘎
*/
@RequestMapping("/info")
@RestController
@PreAuthorize("hasAnyRole('管理员', '普通用户')")
public class InfoController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private AvatarPath avatarPath;

    // 获取用户个人信息
    @RequestMapping("/get")
    public ResultWrapper<User> getUserInfoByUserId(@NotNull Long userId) {
        // 当前接口不论是管理员还是普通用户都不允许越权
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
        if (!principal.getUserId().equals(userId)) {
            throw new BookManagerException("不允许获取其他用户的个人信息");
        }

        return ResultWrapper.normal(userService.getUserById(userId));
    }

    // 编辑用户个人信息
    @RequestMapping("/edit")
    public ResultWrapper<Boolean> editUserInfo(@NotNull User user) {
        // 当前接口不论是管理员还是普通用户都不允许越权
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
        if (!principal.getUserId().equals(user.getId())) {
            throw new BookManagerException("不允许获取其他用户的个人信息");
        }

        return ResultWrapper.normal(userService.editUser(user));
    }

    // 注销接口
    // 普通用户特有
    // 管理员不允许注销账户
    @RequestMapping("/destroy")
    @PreAuthorize("hasRole('普通用户')")
    public ResultWrapper<Boolean> destroyAccount(@NotNull Long userId) {
        // 当前接口不论是管理员还是普通用户都不允许越权
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
        if (!principal.getUserId().equals(userId)) {
            throw new BookManagerException("不允许获取其他用户的个人信息");
        }

        return ResultWrapper.normal(userService.deleteUser(userId));
    }

    // 上传头像
    @SneakyThrows
    @RequestMapping("/uploadAvatar")
    public ResultWrapper<String> uploadAvatar(@RequestParam("file") MultipartFile file, @NotNull Long userId) {
        // 权限校验：确保只能上传自己的头像
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
        if (!principal.getUserId().equals(userId)) {
            throw new BookManagerException("不允许上传其他用户的头像");
        }

        // 文件验证
        if (file.isEmpty()) {
            throw new BookManagerException("上传的文件不能为空");
        }

        // 验证文件类型
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new BookManagerException("文件名不能为空");
        }
        
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
        List<String> allowedExtensions = Arrays.asList(".jpg", ".jpeg", ".png", ".gif");
        if (!allowedExtensions.contains(fileExtension)) {
            throw new BookManagerException("只支持 jpg、jpeg、png、gif 格式的图片");
        }

        // 生成唯一文件名
        String fileName = userId + "_" + System.currentTimeMillis() + fileExtension;
        
        // 构建文件保存路径
        Path uploadPath = Paths.get(avatarPath.getPath());
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        
        Path filePath = uploadPath.resolve(fileName);
        
        // 获取用户当前头像并删除旧文件
        User currentUser = userService.getUserById(userId);
        if (currentUser.getAvatar() != null && !currentUser.getAvatar().isEmpty()) {
            Path oldFilePath = Paths.get(avatarPath.getPath(), currentUser.getAvatar());
            if (Files.exists(oldFilePath)) {
                Files.delete(oldFilePath);
            }
        }
        
        // 保存文件
        file.transferTo(filePath.toFile());
        
        // 更新数据库
        Boolean updateUserAvatar = userService.updateUserAvatar(userId, fileName);
        if (!updateUserAvatar) {
            throw new BookManagerException("保存头像失败");
        }

        return ResultWrapper.normal(fileName);
    }

    // 获取头像
    @RequestMapping("/getAvatar")
    @SneakyThrows
    public void getAvatar(@NotNull Long userId, HttpServletResponse response) {
        // 权限校验：确保只能获取自己的头像
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
        if (!principal.getUserId().equals(userId)) {
            throw new BookManagerException("不允许获取其他用户的头像");
        }

        // 获取用户头像路径
        User user = userService.getUserById(userId);
        String avatarFileName = user.getAvatar();
        
        File avatarFile;
        if (avatarFileName == null || avatarFileName.isEmpty()) {
            // 使用默认头像
            avatarFile = new File(avatarPath.getPath(), "default_avatar.png");
        } else {
            avatarFile = new File(avatarPath.getPath(), avatarFileName);
            if (!avatarFile.exists()) {
                // 如果用户头像文件不存在，使用默认头像
                avatarFile = new File(avatarPath.getPath(), "default_avatar.png");
            }
        }
        
        // 设置响应头
        String contentType = getContentType(avatarFile.getName());
        response.setContentType(contentType);
        response.setHeader("Cache-Control", "max-age=3600"); // 缓存1小时
        
        // 输出文件流
        try (FileInputStream fis = new FileInputStream(avatarFile);
             OutputStream os = response.getOutputStream()) {
            
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.flush();
        }
    }
    
    private String getContentType(String fileName) {
        String extension = fileName.substring(fileName.lastIndexOf(".")).toLowerCase();
        return switch (extension) {
            case ".jpg", ".jpeg" -> "image/jpeg";
            case ".png" -> "image/png";
            case ".gif" -> "image/gif";
            default -> "application/octet-stream";
        };
    }
}
