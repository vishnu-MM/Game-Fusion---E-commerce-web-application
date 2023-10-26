package com.example.gamefusion.Controller;

import com.example.gamefusion.Dto.UserDto;
import com.example.gamefusion.Services.Implementations.MailServiceImpl;
import com.example.gamefusion.Services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@Controller
public class LoginSignUpController {

    private final UserService userService;
    private MailServiceImpl mailService;

    @Autowired
    public LoginSignUpController(UserService userService, MailServiceImpl mailService) {
        this.userService = userService;
        this.mailService = mailService;
    }

    @GetMapping("/")
    public String getHomePage(Authentication authentication) {
        if (authentication != null) {
            Collection<? extends GrantedAuthority> authorityList = authentication.getAuthorities();
            for (GrantedAuthority authority : authorityList) {
                if (authority.getAuthority().equals("ADMIN")) {
                    return "redirect:/dashboard";
                } else if (authority.getAuthority().equals("USER")) {
                    return "redirect:/home";
                }
            }
        }
        return "User/index";
    }

    @GetMapping("/login-or-registration")
    public String getLoginForm(Authentication authentication, Model model) {
        if (authentication != null) return "redirect:/";
        UserDto newUser = new UserDto();
        model.addAttribute("NewUser", newUser);
        return "User/page-login-register";
    }

    @PostMapping("/user-registration/verify")
    public String saveNewUser(@Valid @ModelAttribute("NewUser") UserDto userDto, BindingResult result) {
        System.out.println("ITs here");
        if (userService.isUserExists(userDto.getUsername())) {
            result.rejectValue("username", String.valueOf(HttpStatus.CONFLICT), "User with this email is already exist");
        }
        if (result.hasErrors()) {
            System.out.println("error");
            return "User/page-login-register";
        }
        System.out.println("no error");
        mailService.sentMail(userDto);
        System.out.println("mail sent and redirecting now");
        return "User/page-otp-verification";
    }

    @GetMapping("/home")
    public String home() {
        return "User/index-4";
    }

    @ResponseBody
    @GetMapping("/is-logged-in")
    public String dummy(Authentication authentication) {
        return authentication.getName() + " " + authentication.getAuthorities() + " ";
    }

    @InitBinder
    private void removeWhiteSpace(WebDataBinder webDataBinder) {
        StringTrimmerEditor ste = new StringTrimmerEditor(true);
        webDataBinder.registerCustomEditor(String.class, ste);
    }
}
