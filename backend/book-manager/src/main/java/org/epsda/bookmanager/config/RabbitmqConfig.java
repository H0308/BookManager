package org.epsda.bookmanager.config;

import org.epsda.bookmanager.constants.Constants;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 18483
 * Date: 2025/10/25
 * Time: 17:01
 *
 * @Author: 憨八嘎
 */
@Configuration
public class RabbitmqConfig {
    @Bean
    public Queue userQueue() {
        return QueueBuilder.durable(Constants.RABBITMQ_USER_QUEUE).build();
    }

    @Bean
    public FanoutExchange userExchange() {
        return ExchangeBuilder.fanoutExchange(Constants.RABBITMQ_USER_EXCHANGE).durable(true).build();
    }

    @Bean
    public Binding userBinding(Queue userQueue, FanoutExchange userExchange) {
        return BindingBuilder.bind(userQueue).to(userExchange);
    }
}
