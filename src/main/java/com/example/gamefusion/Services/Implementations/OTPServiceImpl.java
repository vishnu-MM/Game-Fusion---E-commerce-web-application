package com.example.gamefusion.Services.Implementations;

import com.example.gamefusion.Configuration.UtilityClasses.OtpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import com.example.gamefusion.Services.OTPService;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;
import com.example.gamefusion.Dto.UserDto;
import org.slf4j.LoggerFactory;
import java.util.Random;
import org.slf4j.Logger;

@Service
public class OTPServiceImpl implements OTPService {

    private static final long OTP_VALIDITY_DURATION = 5* 60* 1000;
    Logger log = LoggerFactory.getLogger(OTPServiceImpl.class);

    private OtpUtil otpUtil;
    private final JavaMailSender javaMailSender;
    @Value("${spring.mail.username}")
    private String from;

    @Autowired
    public OTPServiceImpl(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Override
    public String generateOTP() {
        Random random = new Random();
        int min = 111111;
        int max = 999999;
        int OneTimePassword = random.nextInt(max - min + 1) + min;
        return Integer.toString(OneTimePassword);
    }

    @Override
    public void sendOTP(UserDto recipient) {
        String username = recipient.getUsername();
        String otp = generateOTP();
        Long timeOfCreation = System.currentTimeMillis();
        otpUtil = new OtpUtil(username,otp,timeOfCreation);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setSubject("OTP Verification - Game Fusion");
        message.setText(
                generateEmailBody(recipient.getFirstName()+" "+recipient.getLastName(),otp )
        );
        message.setTo(username);
        try {
            javaMailSender.send(message);
        }catch (Exception e){
            log.error("Error Occurred"+e.getMessage());
        }
    }

    @Override
    public void sendOTP(String email, String name) {
        String otp = generateOTP();
        Long timeOfCreation = System.currentTimeMillis();
        otpUtil = new OtpUtil(email,otp,timeOfCreation);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setSubject("OTP Verification - Game Fusion");
        message.setText( generateEmailBody(name,otp ) );
        message.setTo(email);

        javaMailSender.send(message);
    }

    @Override
    public String verifyOTP(String recipient, String enteredOtp) {
        if (!recipient.equals(otpUtil.username()))
            return "USER-NOT-FOUNT";
        if (System.currentTimeMillis() - otpUtil.timeOfCreation() > OTP_VALIDITY_DURATION)
            return "TIMEOUT";
        if (enteredOtp == null || otpUtil.otp() == null )
            return "EMPTY";
        if (!enteredOtp.equals(otpUtil.otp()))
            return "INVALID";
        return "SUCCESS";
    }

    private String generateEmailBody(String name, String otp ) {
        return "Hello " + name + ",\n\nYour One-Time Password (OTP) is: " + otp +
                "\n\nThis OTP is valid for a short period. Please use it for authentication as soon as possible.\n\n" +
                "If you didn't request this OTP, please contact our support team.\n\nEnjoy shopping.";
    }
}
