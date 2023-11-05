package com.example.gamefusion.Controller;

import com.example.gamefusion.Dto.UserDto;
import com.example.gamefusion.Services.OTPService;
import com.example.gamefusion.Services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
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
    private final OTPService otpService;

    @Autowired
    public LoginSignUpController(UserService userService, OTPService otpService) {
        this.userService = userService;
        this.otpService = otpService;
    }

    @GetMapping("/")
    public String getHomePage(Authentication authentication) {
        if (authentication != null) {
            Collection<? extends GrantedAuthority> authorityList = authentication.getAuthorities();
            for (GrantedAuthority authority : authorityList) {
                if (authority.getAuthority().equals("ADMIN")) {
                    return "redirect:/dashboard";
                } else if (authority.getAuthority().equals("USER")) {
                    return "redirect:/user/home";
                }
            }
        }
        return "User/index";
    }

    @GetMapping("/login-or-registration")
    public String getLoginForm(Authentication authentication, Model model, HttpServletRequest request) {
        if (authentication != null) return "redirect:/";
        request.getSession(true);
        model.addAttribute("NewUser", new UserDto());
        return "User/page-login-register";
    }

    @PostMapping("/user-registration/verify")
    public String saveNewUser( @Valid @ModelAttribute("NewUser") UserDto userDto,BindingResult result, HttpSession session ) {
        if (userService.isExistsByUsername(userDto.getUsername())) {
            result.rejectValue("username", String.valueOf(HttpStatus.CONFLICT),"User with this email is already exist");
        }
        if (result.hasErrors()) {
            return "User/page-login-register";
        }
        session.setAttribute("UserDetails",userDto);
        return "redirect:/sent-otp";
    }

    @GetMapping("/sent-otp")
    public String sentOTPtoUser(HttpSession session) {
        UserDto receiver = (UserDto) session.getAttribute("UserDetails");
        otpService.sendOTP(receiver);
        return "redirect:/otp-validation";
    }

    @GetMapping("/otp-validation")
    public String getOtpForm() {
        return "User/page-otp-verification";
    }

    @PostMapping("/otp-validation/verify")
    public String validateOTP(@RequestParam("otp") String otp, Model model,
                              HttpSession session) {
        UserDto userDto = (UserDto) session.getAttribute("UserDetails");
        String msg = otpService.verifyOTP(userDto.getUsername(), otp);
        if (msg.equals("SUCCESS")) {
            userService.save(userDto);
            session.removeAttribute("UserDetails");
            model.addAttribute("Success",true);
            return "User/page-otp-verification";
        }
        String errorMsg = "Some thing went wrong";
        if (msg.equals("INVALID")) errorMsg = "The OTP is Invalid";
        if (msg.equals("EMPTY"))   errorMsg = "OTP should not be Empty";
        if (msg.equals("TIMEOUT")) errorMsg = "OTP expired, Try again";
        if (msg.equals("USER-NOT-FOUNT")) errorMsg = "User not fount";

        model.addAttribute("error", errorMsg);
        return "User/page-otp-verification";
    }

    @InitBinder
    private void removeWhiteSpace(WebDataBinder webDataBinder) {
        StringTrimmerEditor ste = new StringTrimmerEditor(true);
        webDataBinder.registerCustomEditor(String.class, ste);
    }
}
