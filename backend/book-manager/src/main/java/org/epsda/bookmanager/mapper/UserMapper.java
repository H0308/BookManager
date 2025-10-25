package org.epsda.bookmanager.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.epsda.bookmanager.pojo.User;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 18483
 * Date: 2025/10/21
 * Time: 10:22
 *
 * @Author: 憨八嘎
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
    // 根据用户名获取到用户信息
    @Select("select * from user where username like concat('%', #{username}, '%')")
    List<User> selectByUsername(String username);
    // 根据电子邮箱获取到用户信息
    @Select("select * from user where email like concat('%', #{email}, '%')")
    List<User> selectByEmail(String email);
    // 根据联系电话获取到用户信息
    @Select("select * from user where email like concat('%', #{phone}, '%')")
    List<User> selectByPhone(String phone);
    @Select("select * from user where email = #{email}")
    User selectByPreciseEmail(String email);
    @Select("select * from user where username = #{username}")
    User selectByPreciseUsername(String username);
    @Select("select * from user where phone = #{phone}")
    User selectByPrecisePhone(String phone);
    @Select("select * from user where user_id_card = #{userIdCard}")
    User selectByPreciseUserIdCard(String userIdCard);
}
