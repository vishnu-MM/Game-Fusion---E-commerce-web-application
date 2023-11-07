package com.example.gamefusion.Services;

import com.example.gamefusion.Dto.AddressDto;
import com.example.gamefusion.Dto.UserDto;

import java.util.List;

public interface AddressService {
    List<AddressDto> findByUser(Integer userId);
    Boolean isExistsByUser(Integer user);
    Boolean isExistById(Integer addressId);

    void save(AddressDto newAddress);

    AddressDto findById(Integer addressId);

    void deleteById(Integer addressId);
}
