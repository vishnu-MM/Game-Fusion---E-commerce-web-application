package com.example.gamefusion.Services;

import com.example.gamefusion.Dto.PaginationInfo;
import com.example.gamefusion.Dto.UserDto;

import java.util.UUID;

public interface UserService {
    UserDto save(UserDto newUser );
    PaginationInfo findAllUsers(Integer pageNo, Integer pageSize);

    void update(UserDto userDto);

    UserDto findByUsername(String receiver);
    UserDto findById(Integer id);
    Boolean isExistsByUsername(String username);
    Boolean isExistsById(Integer id);
    Boolean isBlocked(Integer id);
    void block(Integer id);
    void unBlock(Integer id);
    void resetPassword(Integer id, String password);

    Boolean isUserExistsByReferralCode(UUID referral);

    UserDto findUserByReferralCode(UUID referral);
}

