package com.document.upload.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;
    @Value("${spring.mail.username}")
    private String sendersMail;

   public void sendEmailToUsers(List<String> recipients, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(recipients.toArray(new String[0]));
        message.setSubject(subject);
        message.setText(body);
        message.setFrom(sendersMail);

        javaMailSender.send(message);
    }
}