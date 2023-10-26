package com.example.gamefusion.Services;

import com.example.gamefusion.Dto.UserDto;

public interface UserService {

    void save( UserDto newUser );
    Boolean isUserExists(String username);
}
