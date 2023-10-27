package com.example.gamefusion.Configuration.ExceptionHandlerConfig;

import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class UserBlockedException extends UsernameNotFoundException {
    public UserBlockedException(String message) {
        super(message);
    }
}
