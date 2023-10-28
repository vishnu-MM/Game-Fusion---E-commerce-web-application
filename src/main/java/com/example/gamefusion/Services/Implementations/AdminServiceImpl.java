package com.example.gamefusion.Services.Implementations;

import com.example.gamefusion.Entity.User;
import com.example.gamefusion.Repository.UserRepository;
import com.example.gamefusion.Services.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    @Autowired
    public AdminServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findUsersByRole("USER");
    }
}
