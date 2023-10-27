package com.example.gamefusion.Services.Implementations;

import com.example.gamefusion.Dto.UserDto;
import com.example.gamefusion.Repository.UserRepository;
import com.example.gamefusion.Services.OTPService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class OTPServiceImpl implements OTPService {

    Logger log = LoggerFactory.getLogger(OTPServiceImpl.class);
    Map<String,String> otpStore = new HashMap<>();
    private final JavaMailSender javaMailSender;
    private final UserRepository userRepository;
    @Value("${spring.mail.username}")
    private String from;

    @Autowired
    public OTPServiceImpl(JavaMailSender javaMailSender, UserRepository userRepository) {
        this.javaMailSender = javaMailSender;
        this.userRepository = userRepository;
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
    public boolean verifyOTP(String recipient, String enteredOtp) {
        String expectedOtp = otpStore.get(recipient);
        if ( (enteredOtp != null && expectedOtp != null ) && expectedOtp.equals(enteredOtp) ) {
            log.info("OTP Verified.");
            return true;
        }
        otpStore.remove(recipient);
        return false;
    }

    @Override
    public void sendOTP(UserDto recipient) {
        String otp = generateOTP();
        otpStore.put( recipient.getUsername(), otp );


        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setSubject("OTP Verification - Game Fusion");
        message.setText(
                generateEmailBody(recipient.getFirstName()+" "+recipient.getLastName(),otp )
        );
        message.setTo(recipient.getUsername());
        try {
            javaMailSender.send(message);
        }catch (Exception e){
            log.error(e.getMessage());
            log.warn("Error Occurred");
        }
        log.info("send Email Successfully");
    }

    private String generateEmailBody(String name, String otp ) {
        return "Hello " + name + ",\n\nYour One-Time Password (OTP) is: " + otp +
                "\n\nThis OTP is valid for a short period. Please use it for authentication as soon as possible.\n\n" +
                "If you didn't request this OTP, please contact our support team.\n\nEnjoy shopping.";
    }
}
