package com.example.gamefusion.Services;

import com.example.gamefusion.Dto.OrderMainDto;
import com.example.gamefusion.Dto.AddressDto;
import java.util.List;
import java.util.Map;

public interface AddressService {
    void save(AddressDto newAddress);
    void deleteById(Integer addressId);
    Boolean isExistsByUser(Integer user);
    AddressDto findById(Integer addressId);
    Boolean isExistById(Integer addressId);
    List<AddressDto> findByUser(Integer userId);
    List<AddressDto> findByUser(Integer userId, Boolean status);
    Map<Integer,AddressDto> findByOrder(List<OrderMainDto> orderMainList);
}
