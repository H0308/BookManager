package org.epsda.bookmanager;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Encoders;
import org.epsda.bookmanager.utils.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.crypto.SecretKey;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 18483
 * Date: 2025/10/25
 * Time: 16:15
 *
 * @Author: 憨八嘎
 */
@SpringBootTest
public class JwtTest {
    @Test
    void test() {
        String email = "qiuyiba@qq.com";
        String username = "求一把";
        String token = JwtUtil.generateToken(email, username);
        String tokenUsername = JwtUtil.extractUsername(token);
        String tokenEmail = JwtUtil.extractEmail(token);
        System.out.println(username);
        System.out.println(tokenEmail);
    }

    @Test
    void generateSignature() {
        // 第 23 行：生成 Base64 编码的密钥
        String SECRET_KEY = Encoders.BASE64.encode(Jwts.SIG.HS256.key().build().getEncoded());
        System.out.println(SECRET_KEY);
    }
}
