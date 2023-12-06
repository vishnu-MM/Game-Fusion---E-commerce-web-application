package com.example.gamefusion.Services;

import com.example.gamefusion.Dto.UserDto;

public interface OTPService {
    String generateOTP();
    String verifyOTP(String recipient, String otp);
    void sendOTP(UserDto recipient);
    void sendOTP(String email, String name);
}
