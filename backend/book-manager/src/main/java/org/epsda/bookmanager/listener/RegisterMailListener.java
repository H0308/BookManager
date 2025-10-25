package org.epsda.bookmanager.listener;

import com.rabbitmq.client.Channel;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.epsda.bookmanager.constants.Constants;
import org.epsda.bookmanager.pojo.response.dto.RegisterMail;
import org.epsda.bookmanager.utils.JsonUtil;
import org.epsda.bookmanager.utils.MailUtil;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 18483
 * Date: 2025/10/25
 * Time: 17:05
 *
 * @Author: 憨八嘎
 */
@Slf4j
@Component
public class RegisterMailListener {
    @Autowired
    private MailUtil mailUtil;

    @SneakyThrows
    @RabbitListener(queues = Constants.RABBITMQ_USER_QUEUE)
    public void mailListener(Message message, Channel channel) {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        try {
            String msg = new String(message.getBody(), StandardCharsets.UTF_8);
            RegisterMail userMail = JsonUtil.toObject(msg, RegisterMail.class);
            if (userMail != null) {
                String email = userMail.getEmail();
                String username = userMail.getUsername();
                String html = "<!DOCTYPE html>\n" +
                        "<html lang=\"zh-CN\">\n" +
                        "<head>\n" +
                        "    <meta charset=\"UTF-8\">\n" +
                        "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                        "    <title>欢迎来到湖北工业大学图书馆</title>\n" +
                        "    <style>\n" +
                        "        body {\n" +
                        "            font-family: Arial, sans-serif;\n" +
                        "            line-height: 1.6;\n" +
                        "            color: #333;\n" +
                        "            max-width: 600px;\n" +
                        "            margin: 0 auto;\n" +
                        "            padding: 20px;\n" +
                        "        }\n" +
                        "        .header {\n" +
                        "            text-align: center;\n" +
                        "            padding: 20px 0;\n" +
                        "            background-color: #f8f9fa;\n" +
                        "            border-radius: 5px;\n" +
                        "        }\n" +
                        "        .content {\n" +
                        "            padding: 20px;\n" +
                        "            border: 1px solid #ddd;\n" +
                        "            border-radius: 5px;\n" +
                        "            margin-top: 20px;\n" +
                        "        }\n" +
                        "        .footer {\n" +
                        "            text-align: center;\n" +
                        "            margin-top: 20px;\n" +
                        "            font-size: 12px;\n" +
                        "            color: #777;\n" +
                        "        }\n" +
                        "        .highlight {\n" +
                        "            font-weight: bold;\n" +
                        "            color: #007bff;\n" +
                        "        }\n"+
                        "    </style>\n" +
                        "</head>\n" +
                        "<body>\n" +
                        "    <div class=\"header\">\n" +
                        "        <h1>湖北工业大学图书馆</h1>\n" +
                        "    </div>\n" +
                        "    <div class=\"content\">\n" +
                        "        <h2>亲爱的 <span class=\"highlight\">" + username + "</span>，您好！</h2>\n" +
                        "        <p>感谢您成为湖北工业大学图书馆的一员。您的账户已经创建成功，以下是您的登录信息：</p>\n" +
                        "        <p><strong>用户名：</strong>" + username + "</p>\n" +
                        "        <p><strong>登录邮箱：</strong>" + email + "</p>\n" +
                        "        <p>请使用上述邮箱地址登录您的账户。如果您有任何问题或需要帮助，请随时联系我们的客服团队：400-666-666。</p>\n" +
                        "        <p>祝您使用愉快！</p>\n" +
                        "    </div>\n" +
                        "    <div class=\"footer\">\n" +
                        "        <p>此邮件由湖北工业大学图书馆自动发送，请勿回复。</p>\n" +
                        "        <p>© 2025 湖北工业大学 版权所有。</p>\n" +
                        "    </div>\n" +
                        "</body>\n" +
                        "</html>";
                // 发送邮件
                mailUtil.sendMail(email, "注册成功", html);
            }

            // 第二个参数表示是否需要确认多次，true表示需要
            channel.basicAck(deliveryTag, true);
        } catch (Exception e) {
            // 第三个参数表示是否需要重发
            channel.basicNack(deliveryTag, true, true);
            log.error("邮件信息监听异常，异常内容：{}", e.getMessage());
        }
    }
}
