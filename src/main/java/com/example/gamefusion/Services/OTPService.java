package com.example.gamefusion.Services;

import com.example.gamefusion.Dto.UserDto;

public interface OTPService {

    String generateOTP();

    boolean verifyOTP(String recipient, String otp);

    void sendOTP(UserDto recipient);
}
