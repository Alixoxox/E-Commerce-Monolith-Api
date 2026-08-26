package com.e_comerce.service;

import static com.e_comerce.config.RedisAndRabbitConfig.EMAIL_QUEUE;

import com.e_comerce.DTO.UserDto;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
//    @Autowired
//    private JavaMailSender mailSender;

//    @Value("${spring.mail.username}")
//    private String mailUsername;

    @RabbitListener(queues = EMAIL_QUEUE)
    public void consumeMessage(UserDto.supportMsg payload) throws MessagingException {
//        if (payload.getIsHtml()) {
//            sendHtmlEmail(payload);
//        } else {
//       sendSupportMessage(payload);
//    }
    }

    private void sendHtmlEmail(UserDto.supportMsg mailItems) {
//        try {
//            MimeMessage message = mailSender.createMimeMessage();
//            MimeMessageHelper helper = new MimeMessageHelper(message, true);
//            helper.setFrom(mailUsername);
//            helper.setTo(mailItems.getUserEmail());
//            helper.setSubject(mailItems.getSubject());
//            helper.setText(mailItems.getMessage(), true);
//            mailSender.send(message);
//        } catch (MessagingException e) {
//            throw new RuntimeException("Failed to send email", e);
//        }
    }
    private void sendSupportMessage(UserDto.supportMsg msg) {
//        SimpleMailMessage message = new SimpleMailMessage();
//        message.setFrom(mailUsername);       // Gmail forces this anyway, so set it explicitly
//        message.setTo(mailUsername);         // lands in your inbox
//        message.setReplyTo(msg.getUserEmail());       // hitting reply goes to the user
//        message.setSubject("[Support] " + msg.getSubject());
//        message.setText(msg.getMessage());
//        mailSender.send(message);

    }
}