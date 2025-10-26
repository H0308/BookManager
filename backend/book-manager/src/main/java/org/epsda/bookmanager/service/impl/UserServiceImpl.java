package org.epsda.bookmanager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.epsda.bookmanager.constants.Constants;
import org.epsda.bookmanager.exception.BookManagerException;
import org.epsda.bookmanager.mapper.PurchaseRecordMapper;
import org.epsda.bookmanager.mapper.UserMapper;
import org.epsda.bookmanager.pojo.PurchaseRecord;
import org.epsda.bookmanager.pojo.User;
import org.epsda.bookmanager.pojo.request.QueryUserReq;
import org.epsda.bookmanager.pojo.response.QueryUserResp;
import org.epsda.bookmanager.pojo.response.dto.PasswordMail;
import org.epsda.bookmanager.pojo.response.vo.UserResp;
import org.epsda.bookmanager.service.UserService;
import org.epsda.bookmanager.utils.BeanUtil;
import org.epsda.bookmanager.utils.JsonUtil;
import org.epsda.bookmanager.utils.SecurityUtil;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 18483
 * Date: 2025/10/22
 * Time: 18:24
 *
 * @Author: 憨八嘎
 */
@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Value("${admin.admin-name}")
    private String adminName;

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private PurchaseRecordMapper purchaseRecordMapper;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public QueryUserResp queryUsers(QueryUserReq queryUserReq) {
        if (!Constants.ADMIN_FLAG.equals(SecurityUtil.getRoleIdFromPrinciple())) {
            throw new BookManagerException("当前登录用户没有权限查询用户");
        }

        Integer pageNum = queryUserReq.getPageNum();
        Integer pageSize = queryUserReq.getPageSize();

        Page<User> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        String username = queryUserReq.getUsername();
        String userIdCard = queryUserReq.getUserIdCard();
        String phone = queryUserReq.getPhone();
        String address = queryUserReq.getAddress();
        wrapper.like(StringUtils.hasText(username), User::getUsername, username)
                .like(StringUtils.hasText(userIdCard), User::getUserIdCard, userIdCard)
                .like(StringUtils.hasText(phone), User::getPhone, phone)
                .like(StringUtils.hasText(address), User::getAddress, address)
                .eq(User::getDeleteFlag, Constants.NOT_DELETE_FIELD_FLAG)
                .ne(User::getUsername, adminName); // 不显示管理员

        Page<User> userPage = userMapper.selectPage(page, wrapper);
        List<User> users = userPage.getRecords();
        List<UserResp> userResp = users.stream().map((user) -> (UserResp) BeanUtil.convert(user)).toList();

        return new QueryUserResp(userPage.getCurrent(), userPage.getPages(), userPage.getTotal(), userResp);
    }

    @Override
    public Boolean addUser(User user) {
        if (!Constants.ADMIN_FLAG.equals(SecurityUtil.getRoleIdFromPrinciple())) {
            throw new BookManagerException("当前登录用户没有权限新增用户");
        }
        // 根据身份证号判断插入的用户是否已经存在过
        LambdaQueryWrapper<User> idCardWrapper = new LambdaQueryWrapper<User>().eq(User::getUserIdCard, user.getUserIdCard()).eq(User::getDeleteFlag, Constants.NOT_DELETE_FIELD_FLAG);
        User existed = userMapper.selectOne(idCardWrapper);
        if (existed != null) {
            throw new BookManagerException("新增的用户已经存在，无法添加");
        }
        LambdaQueryWrapper<User> emailWrapper = new LambdaQueryWrapper<User>().eq(User::getEmail, user.getEmail());
        existed = userMapper.selectOne(emailWrapper);
        if (existed != null) {
            throw new BookManagerException("此邮箱已经注册，请更换邮箱");
        }
        LambdaQueryWrapper<User> phoneWrapper = new LambdaQueryWrapper<User>().eq(User::getPhone, user.getPhone());
        existed = userMapper.selectOne(phoneWrapper);
        if (existed != null) {
            throw new BookManagerException("此电话已经注册，请更换电话");
        }

        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<User>().eq(User::getUserIdCard, user.getUserIdCard()).eq(User::getDeleteFlag, Constants.DELETED_FIELD_FLAG);
        User deleted = userMapper.selectOne(wrapper);
        if (deleted != null) {
            deleted.setDeleteFlag(Constants.NOT_DELETE_FIELD_FLAG);
            return userMapper.update(deleted, wrapper) == 1;
        }

        // 排除上面两种情况后，当前用户即为不存在的用户
        return userMapper.insert(user) == 1;
    }

    @Override
    public User getUserById(Long userId) {
        return userMapper.selectById(userId);
    }

    @Override
    public Boolean editUser(User user) {
        // 如果传递的密码等于原密码，说明没有进行密码修改
        User oldUser = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getId, user.getId())
                .eq(User::getDeleteFlag, Constants.NOT_DELETE_FIELD_FLAG));
        if (oldUser == null) {
            throw new BookManagerException("待修改的用户不存在或已被删除，无法修改");
        }
        // 如果发生加密，此处需要修改匹配方式
        if (!oldUser.getPassword().equals(user.getPassword())) {
            // 需要处理密码邮件发送
            String passwordMail = JsonUtil.toJson(new PasswordMail(user.getUsername(), user.getEmail(), user.getPassword()));
            rabbitTemplate.convertAndSend(Constants.RABBITMQ_USER_EXCHANGE, "", passwordMail);
            // 需要删除该登录状态，让用户重新登录
            log.info("用户修改了密码，新密码为：{}", user.getPassword());
        }

        return userMapper.update(user, new LambdaQueryWrapper<User>().eq(User::getId, user.getId())) == 1;
    }

    @Override
    public Boolean deleteUser(Long userId) {
        // 检查当前用户是否有借阅（包括逾期）和待支付的订单
        User user = userMapper.selectById(userId);
        if (user.getBorrowRecordCount() != 0) {
            throw new BookManagerException("当前用户存在借阅或逾期书籍，无法删除");
        }
        List<PurchaseRecord> purchaseRecordByUserId = purchaseRecordMapper.getPurchaseRecordByUserId(userId);
        if (!purchaseRecordByUserId.isEmpty()) {
            throw new BookManagerException("当前用户存在未支付的书籍，无法删除");
        }

        if (Constants.DELETED_FIELD_FLAG.equals(user.getDeleteFlag())) {
            throw new BookManagerException("当前用户已经删除，无法删除");
        }

        // 虚拟删除
        user.setDeleteFlag(Constants.DELETED_FIELD_FLAG);
        return userMapper.updateById(user) == 1;
    }

    @Override
    public Boolean batchDeleteUser(List<Long> userIds) {
        var count = 0;
        for (var userId : userIds) {
            Boolean ret = deleteUser(userId);
            if (ret) {
                count++;
            }
        }

        return count == userIds.size();
    }
    
    @Override
    public Boolean updateUserAvatar(Long userId, String avatarPath) {
        SecurityUtil.checkHorizontalOverstepped(userId);
        User user = new User();
        user.setId(userId);
        user.setAvatar(avatarPath);
        return userMapper.updateById(user) == 1;
    }
}
