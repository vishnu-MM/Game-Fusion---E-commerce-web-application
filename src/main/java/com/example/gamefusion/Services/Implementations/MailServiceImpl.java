package com.example.gamefusion.Services.Implementations;

import com.example.gamefusion.Dto.UserDto;
import com.example.gamefusion.Services.MailService;
import com.example.gamefusion.Utility.OtpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailServiceImpl implements MailService {

    @Value("${spring.mail.username}")
    private String from;
    private final OtpUtil otpUtil;
    private final JavaMailSender javaMailSender;

    @Autowired
    public MailServiceImpl(JavaMailSender javaMailSender, OtpUtil otpUtil) {
        
        this.javaMailSender = javaMailSender;
        this.otpUtil = otpUtil;
    }

    public void sentMail(UserDto user) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setSubject("OTP Verification - Game Fusion");
        message.setText(
                generateEmailBody(user.getFirstName() + " " + user.getLastName())
        );
        message.setTo(user.getUsername());
        javaMailSender.send(message);
    }

    public Boolean otpAuthentication() {

        return true;
    }

    private String generateEmailBody(String name) {
        return "Hello " + name + ",\n\nYour One-Time Password (OTP) is: " + otpUtil.generateOtp() +
                "\n\nThis OTP is valid for a short period. Please use it for authentication as soon as possible.\n\n" +
                "If you didn't request this OTP, please contact our support team.\n\nEnjoy shopping.";
    }

}
