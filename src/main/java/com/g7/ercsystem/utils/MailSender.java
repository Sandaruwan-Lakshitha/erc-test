package com.g7.ercsystem.utils;

import com.g7.ercsystem.configuration.MailConfiguration;
import com.g7.ercsystem.configuration.ThymeleafConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.Thymeleaf;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Date;

@Service
@Slf4j
public class MailSender {

    @Autowired
    private MailConfiguration configuration ;
    @Autowired
    private ThymeleafConfiguration thymeleafConfiguration;

    @Async
    public void sendVerificationEmail(String name,String email) throws MessagingException, IOException {

        String fromAddress = "harshanadun52@gmail.com";
        String senderName = "ERC University of Ruhuna";
        String subject = "Please verify your subscription account";
        String content = htmlToString("Sandaruwan Lakshitha");
        System.out.println("Init");
        MimeMessage mimeMessage = configuration.getJavaMailSender().createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);

        mimeMessageHelper.setFrom(fromAddress,senderName);
        mimeMessageHelper.setTo(email);
        mimeMessageHelper.setSubject(subject);

        mimeMessageHelper.setText(content,true);
        System.out.println("Start sending");

        configuration.getJavaMailSender().send(mimeMessage);
        System.out.println("Email has been sent");
    }

    public String htmlToString(String recipientName) throws IOException {

        Context ctx = new Context();
        ctx.setVariable("name", recipientName);
        ctx.setVariable("subscriptionDate", new Date());
        ctx.setVariable("hobbies", Arrays.asList("Cinema", "Sports", "Music"));
        return thymeleafConfiguration.templateEngine().process("signup_mail.html",ctx);

    }


}
