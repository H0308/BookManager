package org.epsda.bookmanager.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 18483
 * Date: 2025/10/25
 * Time: 11:30
 *
 * @Author: 憨八嘎
 */
public class JwtUtil {

    // 生成密钥
    private final static String secretKeySignature = "uh7Ib5KBKRwQCLal4ziR1UmsVJ07FirkpEJl10JFu+c=";

    // 过期时间
    private static final long EXPIRATION_TIME = 24 * 60 * 60 * 1000;

    private static SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKeySignature));
    }

    public static String generateToken(String email, String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", email);
        claims.put("username", username);

        return Jwts.builder()
                .claims(claims)
                .subject(email)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSigningKey())
                .compact();
    }

    public static String extractEmail(String token) {
        return (String) extractClaims(token).get("email");
    }

    public static String extractUsername(String token) {
        return (String) extractClaims(token).get("username");
    }

    public static Date extractExpiration(String token) {
        return extractClaims(token).getExpiration();
    }

    private static Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public static boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public static boolean validateToken(String token, String username) {
        try {
            final String tokenUsername = extractUsername(token);
            return (tokenUsername.equals(username) && !isTokenExpired(token));
        } catch (Exception e) {
            return false;
        }
    }
}
