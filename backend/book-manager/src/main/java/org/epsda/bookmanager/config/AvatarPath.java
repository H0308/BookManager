package org.epsda.bookmanager.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 18483
 * Date: 2025/10/26
 * Time: 15:11
 *
 * @Author: 憨八嘎
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "avatar.upload")
public class AvatarPath {
    private String path;
}
