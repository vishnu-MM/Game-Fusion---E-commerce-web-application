package com.example.gamefusion.Configuration.ExceptionHandlerConfig;

import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class CustomExceptionHandler {
    @ExceptionHandler(Exception.class)
    public String handleException() {
        return "redirect:/";
    }
    @ExceptionHandler(ChangeSetPersister.NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFoundException(Model model) {
        System.out.println("getting");
        return "redirect:/my-orders";
    }
}