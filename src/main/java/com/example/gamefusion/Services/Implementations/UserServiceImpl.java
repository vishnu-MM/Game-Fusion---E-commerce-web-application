package com.example.gamefusion.Services.Implementations;

import com.example.gamefusion.Dto.UserDto;
import com.example.gamefusion.Entity.User;
import com.example.gamefusion.Repository.UserRepository;
import com.example.gamefusion.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void save(UserDto newUser) {
        User user = new User();
        user.setFirstName(newUser.getFirstName());
        user.setLastName(newUser.getLastName());
        user.setPhone(newUser.getPhone());
        user.setUsername(newUser.getUsername());
        user.setRole("USER");
        user.setIsActive(true);
        user.setPassword(
                passwordEncoder.encode(newUser.getPassword())
        );
        userRepository.save(user);
    }

    @Override
    public Boolean isUserExists(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public void activateAccount(String receiver) {
        if (isUserExists(receiver)) {
            userRepository.unBlockUser(receiver);
        }
        else {
            throw new UsernameNotFoundException("User not exist.");
        }
    }
}
