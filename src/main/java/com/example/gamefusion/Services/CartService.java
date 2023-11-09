package com.example.gamefusion.Services;

import com.example.gamefusion.Dto.CartDto;
import com.example.gamefusion.Dto.PaginationInfo;
import com.example.gamefusion.Dto.ProductDto;
import com.example.gamefusion.Dto.UserDto;
import com.example.gamefusion.Entity.Cart;
import com.example.gamefusion.Entity.Product;
import com.example.gamefusion.Entity.User;

import java.util.List;

public interface CartService {
    void addToCart(CartDto cart);
    PaginationInfo findByUser(UserDto user,Integer pageNo, Integer pageSize);
    List<Cart> findByUser(UserDto user);
    Integer totalAmount(List<Cart> cartList);
    CartDto findByUserAndProduct(User user, Product product);
    Boolean isExistsByProduct(UserDto user, ProductDto product);
    Boolean isExistsById(Integer id);
    void deleteById(Integer id);
    void deleteByUser(UserDto user);
    CartDto findById(Integer cartId);
}
