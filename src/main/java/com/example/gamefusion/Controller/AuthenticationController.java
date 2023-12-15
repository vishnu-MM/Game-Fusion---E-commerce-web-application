package com.example.gamefusion.Controller;

import com.example.gamefusion.Dto.UserDto;
import com.example.gamefusion.Dto.WalletDto;
import com.example.gamefusion.Services.OTPService;
import com.example.gamefusion.Services.UserService;
import com.example.gamefusion.Services.WalletService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Collection;
import java.util.UUID;

@Controller
public class AuthenticationController {

    private final OTPService otpService;
    private final UserService userService;
    private final WalletService walletService;
    @Autowired
    public AuthenticationController(OTPService otpService,
                                    UserService userService,
                                    WalletService walletService) {
        this.otpService = otpService;
        this.userService = userService;
        this.walletService = walletService;
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
    public String getLoginForm(Authentication authentication, Model model, HttpServletRequest request,
                               HttpSession session,
                               @RequestParam(name = "referralID", required = false) UUID referralID) {
        if (authentication != null) return "redirect:/";
        request.getSession(true);

        if (referralID != null)
            session.setAttribute("Referral",referralID);

        model.addAttribute("NewUser", new UserDto());
        return "page-login-register";
    }

    @PostMapping("/user-registration/verify")
    public String saveNewUser(@Valid @ModelAttribute("NewUser") UserDto userDto,
                              BindingResult result, HttpSession session ) {
        if (userService.isExistsByUsername(userDto.getUsername()))
            result.rejectValue("username", String.valueOf(HttpStatus.CONFLICT),
                                "User with this email is already exist");

        if (result.hasErrors())
            return "page-login-register";

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
    public String validateOTP(@RequestParam("otp") String otp, Model model,HttpSession session) {

        UserDto userDto = (UserDto) session.getAttribute("UserDetails");
        String msg = otpService.verifyOTP(userDto.getUsername(), otp);

        if (!msg.equals("SUCCESS")) {
            getError(model, msg);
            System.out.println("Literally Error"+msg);
            return "User/page-otp-verification";
        }

        UserDto savedUser = userService.save(userDto);
        session.removeAttribute("UserDetails");

        model.addAttribute("Success",true);
        if (session.getAttribute("Referral") == null)
            return "User/page-otp-verification";

        UUID referralID = (UUID) session.getAttribute("Referral");
        if (!userService.isUserExistsByReferralCode(referralID))
            return "User/page-otp-verification";

        WalletDto walletDto = new WalletDto();
        walletDto.setAmount(51.0);
        walletDto.setUserId(savedUser.getId());
        walletDto.setTransactionType("Credit");
        walletDto.setDateTime(Date.valueOf(LocalDate.now()));
        walletDto.setDescription("Rewarded for completing a referral");
        walletService.save(walletDto);

        UserDto existingUser = userService.findUserByReferralCode(referralID);
        walletDto.setAmount(501.0);
        walletDto.setUserId(existingUser.getId());
        walletService.save(walletDto);

         return "User/page-otp-verification";
    }

    @GetMapping("/forget-password")
    public String showForgetPasswordForm() {
        return "User/page-forget-password";
    }

    @PostMapping("/forget-password/verify")
    public String verifyMailForResetPassword(@RequestParam("username") String username,Model model,Authentication authentication) {
        if (!userService.isExistsByUsername(username)) {
            return "User/page-forget-password";
        }
        otpService.sendOTP(userService.findByUsername(username));
        model.addAttribute("username",username);
        if (authentication != null)
            return "User/page-otp-verification0";
        return "User/page-otp-verification";
    }

    @GetMapping("/resent-otp")
    @ResponseBody
    public ResponseEntity<Void> resentOTP(@RequestParam("username") String username) {
        otpService.sendOTP(userService.findByUsername(username));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/verify-otp")
    public String verifyOTPForResetPassword(@RequestParam("otp") String otp, @RequestParam("username") String username,
                                            Model model,Authentication authentication) {
        String msg = otpService.verifyOTP(username, otp);
        if (msg.equals("SUCCESS")) {
            return "redirect:/update-password/"+username;
        }
        getError(model, msg);
        model.addAttribute("username",username);
        if (authentication != null)
            return "User/page-otp-verification0";
        return "User/page-otp-verification";
    }

    @GetMapping("/update-password/{username}")
    public String getPasswordResetForm(@PathVariable("username") String username, Model model) {
        model.addAttribute("username",username);
        return "User/page-update-password";
    }

    @PutMapping("/reset-password")
    public String updatePassword(@RequestParam("password") String password,
                                 @RequestParam("username") String username) {
        UserDto userDto = userService.findByUsername(username);
        userService.resetPassword(userDto.getId(), password);
        return "redirect:/user/home?success";
    }

    @InitBinder
    private void removeWhiteSpace(WebDataBinder webDataBinder) {
        StringTrimmerEditor ste = new StringTrimmerEditor(true);
        webDataBinder.registerCustomEditor(String.class, ste);
    }

    static void getError(Model model, String msg) {
        String errorMsg = "Some thing went wrong";
        if (msg.equals("INVALID")) errorMsg = "The OTP is Invalid";
        if (msg.equals("EMPTY"))   errorMsg = "OTP should not be Empty";
        if (msg.equals("TIMEOUT")) errorMsg = "OTP expired, Try again";
        if (msg.equals("USER-NOT-FOUNT")) errorMsg = "User not fount";
        model.addAttribute("error", errorMsg);
    }
}