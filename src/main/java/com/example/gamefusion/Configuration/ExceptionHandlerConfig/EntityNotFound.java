package com.example.gamefusion.Configuration.ExceptionHandlerConfig;

public class EntityNotFound extends RuntimeException{
    public EntityNotFound(String message) {
        super(message);
    }
}
