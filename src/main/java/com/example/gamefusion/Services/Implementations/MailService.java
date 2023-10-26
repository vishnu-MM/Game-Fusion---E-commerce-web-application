package com.example.gamefusion.Services.Implementations;

import com.example.gamefusion.Utility.MailStructure;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {
    private JavaMailSender javaMailSender;
    @Autowired
    public MailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }
    @Value("${spring.mail.username}")
    private String from;
    public void sentMail(String mail, MailStructure mailStructure ) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setSubject(mailStructure.getSubject());
        message.setText(mailStructure.getMessage()
        );
        message.setTo(mail);
        System.out.println(from);
        javaMailSender.send(message);
    }
}
