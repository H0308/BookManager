package org.epsda.bookmanager.service;

import org.epsda.bookmanager.pojo.User;
import org.epsda.bookmanager.pojo.request.QueryUserReq;
import org.epsda.bookmanager.pojo.response.QueryUserResp;

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
public interface UserService {
    QueryUserResp queryUsers(QueryUserReq queryUserReq);

    Boolean addUser(User user);

    Boolean editUser(User user);

    Boolean deleteUser(Long userId);

    Boolean batchDeleteUser(List<Long> userIds);

    User getUserById(Long userId);
    
    Boolean updateUserAvatar(Long userId, String avatarPath);
}
