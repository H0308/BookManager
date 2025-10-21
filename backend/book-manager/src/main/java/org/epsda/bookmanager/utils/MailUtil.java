package org.epsda.bookmanager.utils;

import jakarta.mail.internet.MimeMessage;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 18483
 * Date: 2025/10/21
 * Time: 9:01
 *
 * @Author: 憨八嘎
 */
public class MailUtil {

    private JavaMailSender javaMailSender;
    private MailProperties mailProperties;

    public MailUtil(JavaMailSender javaMailSender, MailProperties mailProperties) {
        this.javaMailSender = javaMailSender;
        this.mailProperties = mailProperties;
    }

    public void sendMail(String to, String subject, String html) throws Exception {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false);
        mimeMessageHelper.setFrom(mailProperties.getUsername(), mailProperties.getProperties().get("personal"));
        mimeMessageHelper.setTo(to);
        mimeMessageHelper.setSubject(subject);
        mimeMessageHelper.setText(html, true);

        javaMailSender.send(mimeMessage);
    }
}
