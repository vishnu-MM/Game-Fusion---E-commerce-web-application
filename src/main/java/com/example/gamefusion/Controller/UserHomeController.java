package com.example.gamefusion.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/home")
public class UserHomeController {
    @GetMapping("")
    public String home() {
        return "User/index-4";
    }
}
