package com.example.gamefusion.Controller;

import com.example.gamefusion.Services.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/dashboard")
public class AdminController {

    private final AdminService adminService;
    @Autowired
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("")
    public String home() {
        return "Admin/index";
    }

    @GetMapping("/view-users")
    public String getListOfAllUsers(Model model) {
        model.addAttribute("UserList",adminService.getAllUsers());
        return "Admin/page-users-list";
    }


}
