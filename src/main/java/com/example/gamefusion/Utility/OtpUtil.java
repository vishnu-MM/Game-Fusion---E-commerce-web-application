package com.example.gamefusion.Utility;

import org.springframework.stereotype.Component;
import java.util.Random;

@Component
public class OtpUtil {

    public String generateOtp() {
        Random random = new Random();
        int min = 111111;
        int max = 999999;
        int OneTimePassword = random.nextInt(max - min + 1) + min;
        return Integer.toString(OneTimePassword);
    }
}

