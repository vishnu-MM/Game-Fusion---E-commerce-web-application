package com.example.gamefusion.Configuration.ExceptionHandlerConfig;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(UserBlockedException.class)
    public String handleUserBlockException(UserBlockedException ex, Model model) {
        model.addAttribute("blocked", "You are blocked from accessing this web site");
        return "login";
    }
}

