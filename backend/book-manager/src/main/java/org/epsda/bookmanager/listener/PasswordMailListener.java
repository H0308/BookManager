package org.epsda.bookmanager.listener;

import com.rabbitmq.client.Channel;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.epsda.bookmanager.constants.Constants;
import org.epsda.bookmanager.pojo.response.dto.PasswordMail;
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
 * Time: 17:32
 *
 * @Author: 憨八嘎
 */
@Slf4j
@Component
public class PasswordMailListener {
    @Autowired
    private MailUtil mailUtil;

    @SneakyThrows
    @RabbitListener(queues = Constants.RABBITMQ_USER_QUEUE)
    public void mailListener(Message message, Channel channel) {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        try {
            String msg = new String(message.getBody(), StandardCharsets.UTF_8);
            PasswordMail passwordMail = JsonUtil.toObject(msg, PasswordMail.class);
            if (passwordMail != null) {
                String email = passwordMail.getEmail();
                String username = passwordMail.getUsername();
                String password = passwordMail.getPassword();
                String html = "<!DOCTYPE html>\n" +
                        "<html lang=\"zh-CN\">\n" +
                        "<head>\n" +
                        "    <meta charset=\"UTF-8\">\n" +
                        "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                        "    <title>密码修改成功通知</title>\n" +
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
                        "            color: #28a745;\n" +
                        "        }\n" +
                        "        .password-box {\n" +
                        "            background-color: #f8f9fa;\n" +
                        "            border: 1px solid #dee2e6;\n" +
                        "            border-radius: 4px;\n" +
                        "            padding: 10px;\n" +
                        "            margin: 10px 0;\n" +
                        "            font-family: monospace;\n" +
                        "            font-size: 14px;\n" +
                        "        }\n" +
                        "        .warning {\n" +
                        "            background-color: #fff3cd;\n" +
                        "            border: 1px solid #ffeaa7;\n" +
                        "            border-radius: 4px;\n" +
                        "            padding: 10px;\n" +
                        "            margin: 15px 0;\n" +
                        "            color: #856404;\n" +
                        "        }\n" +
                        "    </style>\n" +
                        "</head>\n" +
                        "<body>\n" +
                        "    <div class=\"header\">\n" +
                        "        <h1>湖北工业大学图书馆</h1>\n" +
                        "    </div>\n" +
                        "    <div class=\"content\">\n" +
                        "        <h2>亲爱的 <span class=\"highlight\">" + username + "</span>，您好！</h2>\n" +
                        "        <p>您的账户密码已成功修改。以下是您的最新登录信息：</p>\n" +
                        "        <p><strong>用户名：</strong>" + username + "</p>\n" +
                        "        <p><strong>登录邮箱：</strong>" + email + "</p>\n" +
                        "        <p><strong>新密码：</strong></p>\n" +
                        "        <div class=\"password-box\">" + password + "</div>\n" +
                        "        <div class=\"warning\">\n" +
                        "            <strong>⚠️ 安全提醒：</strong>\n" +
                        "            <ul>\n" +
                        "                <li>请妥善保管您的新密码，不要泄露给他人</li>\n" +
                        "                <li>建议您登录后立即修改为更安全的密码</li>\n" +
                        "                <li>如果这不是您本人的操作，请立即联系客服</li>\n" +
                        "            </ul>\n" +
                        "        </div>\n" +
                        "        <p>如果您有任何问题或需要帮助，请随时联系我们的客服团队：400-666-666。</p>\n" +
                        "        <p>祝您使用愉快！</p>\n" +
                        "    </div>\n" +
                        "    <div class=\"footer\">\n" +
                        "        <p>此邮件由湖北工业大学图书馆自动发送，请勿回复。</p>\n" +
                        "        <p>© 2025 湖北工业大学 版权所有。</p>\n" +
                        "    </div>\n" +
                        "</body>\n" +
                        "</html>";
                // 发送邮件
                mailUtil.sendMail(email, "密码修改成功", html);
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
