package org.epsda.bookmanager.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
* Created with IntelliJ IDEA.
* Description:
* User: 18483
* Date: 2025/10/24
* Time: 21:33
* @Author: 憨八嘎
*/
@Configuration
public class AiConfig {
    @Bean
    public ChatClient chatClient(ChatClient.Builder builder) {
        return builder.build();
    }
}