package com.document.upload.util;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String sendersMail;



   public void sendEmailToUsers(List<String> recipients, String subject, String htmlContent) throws MessagingException {
       MimeMessage message = javaMailSender.createMimeMessage();
       MimeMessageHelper helper = new MimeMessageHelper(message, true);
      helper.setTo(recipients.toArray(new String[0]));
        helper.setSubject(subject);

        helper.setFrom(sendersMail);




       helper.setText(htmlContent, true);


       javaMailSender.send(message);

    }

}