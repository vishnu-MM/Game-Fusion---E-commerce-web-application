package com.example.gamefusion.Configuration.ExceptionHandlerConfig;

public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(String message) {
        super(message);
    }
}

