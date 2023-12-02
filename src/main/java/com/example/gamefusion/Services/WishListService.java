package com.example.gamefusion.Services;

import com.example.gamefusion.Dto.ProductDto;
import com.example.gamefusion.Dto.UserDto;
import com.example.gamefusion.Dto.WishListDto;

import java.util.List;

public interface WishListService {
    void delete(WishListDto wishListDto);
    Boolean isExitsById(Integer id);
    WishListDto findById(Integer id);
    void deleteByUser(UserDto userDto);
    void deleteById(Integer wishlistId);
    Boolean isExistsByUser(UserDto userDto);
    WishListDto save(WishListDto wishListDto);
    List<WishListDto> findByUser(UserDto userDto);
    void deleteByProduct(UserDto userDto, ProductDto productDto);
    Boolean isExistsByProduct(UserDto userDto, ProductDto productDto);
}
