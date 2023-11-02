package com.example.gamefusion.Services;

import com.example.gamefusion.Dto.PaginationInfo;
import com.example.gamefusion.Dto.UserDto;
import com.example.gamefusion.Entity.User;

public interface UserService {

    void save( UserDto newUser );

    PaginationInfo findAllUsers(Integer pageNo, Integer pageSize);

    UserDto findByUsername(String receiver);

    Boolean isExistsByUsername(String username);
    Boolean isExistsById(Integer id);
    Boolean isBlocked(Integer id);

    void block(Integer id);
    void unBlock(Integer id);
}

