package com.example.gamefusion.Services;

import com.example.gamefusion.Dto.CartDto;
import com.example.gamefusion.Dto.PaginationInfo;
import com.example.gamefusion.Dto.ProductDto;
import com.example.gamefusion.Dto.UserDto;
import com.example.gamefusion.Entity.Cart;

import java.util.List;
import java.util.Map;

public interface CartService {
    void deleteById(Integer id);
    void addToCart(CartDto cart);
    void deleteByUser(UserDto user);
    Boolean isExistsById(Integer id);
    CartDto findById(Integer cartId);
    List<Cart> findByUser(UserDto user);

    Map<Long, Map<String,Double>> getOfferCount(List<CartDto> cartListDto);

    Integer totalAmount(List<CartDto> cartList);
    List<CartDto> getCartByUser(UserDto userDto);
    List<CartDto> findAvailableProductsByUser(UserDto user);
    Boolean isExistsByProduct(UserDto user, ProductDto product);
    CartDto findByUserAndProduct(UserDto user, ProductDto product);
    PaginationInfo findByUser(UserDto user,Integer pageNo, Integer pageSize);
}
