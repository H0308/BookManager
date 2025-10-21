package org.epsda.bookmanager.utils;

import lombok.extern.slf4j.Slf4j;
import org.epsda.bookmanager.constants.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 18483
 * Date: 2025/10/21
 * Time: 8:57
 *
 * @Author: 憨八嘎
 */
// 封装Redis的基本操作
@Slf4j
@Component
public class RedisUtil {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    // hasKey
    public boolean hasKey(String key) {
        return key != null && stringRedisTemplate.hasKey(key);
    }

    // get
    public String get(String key) {
        try {
            return key == null ? null : (hasKey(key) ? stringRedisTemplate.opsForValue().get(key) : null);
        } catch (Exception e) {
            log.error("Redis获取值异常，key为：{}", key);
            return null;
        }
    }

    // set no timeout
    public boolean set(String key, String value) {
        try {
            if (key == null || value == null) {
                return false;
            }

            stringRedisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            log.error("Redis设置值异常，key为：{}，value为：{}", key, key);
            return false;
        }
    }

    // set with timeout, seconds
    public boolean set(String key, String value, long timeout) {
        try {
            if (key == null || value == null) {
                return false;
            }

            stringRedisTemplate.opsForValue().set(key, value, timeout, TimeUnit.SECONDS);
            return true;
        } catch (Exception e) {
            log.error("Redis设置值异常，key为：{}，value为：{}", key, value);
            return false;
        }
    }

    // buildKey
    public String buildKey(String prefix, String ...args) {
        if (prefix == null) {
            prefix = Constants.REDIS_DEFAULT_PREFIX;
        }

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(prefix);
        stringBuilder.append(Constants.REDIS_NAMESPACE_SEP);
        if (args != null) {
            for (int i = 0; i < args.length; i++) {
                stringBuilder.append(args[i]);
                if (i != args.length - 1) {
                    stringBuilder.append(Constants.REDIS_NAMESPACE_SEP);
                }
            }
        }

        return stringBuilder.toString();
    }
}
