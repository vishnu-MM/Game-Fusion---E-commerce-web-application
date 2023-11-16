package com.example.gamefusion.Services;

import com.example.gamefusion.Dto.CategoryDto;
import com.example.gamefusion.Dto.PaginationInfo;
import com.example.gamefusion.Dto.ProductDto;
import com.example.gamefusion.Dto.UserDto;
import com.example.gamefusion.Entity.Category;
import com.example.gamefusion.Entity.Product;

public interface ProductService {
    Long save(ProductDto productDto);
    PaginationInfo getAllProducts(Integer pageNo, Integer pageSize);
    PaginationInfo getAllActiveProducts(Integer pageNo, Integer pageSize);
    PaginationInfo getAllActiveProductsFromCategory(CategoryDto category, Integer pageNo, Integer pageSize);
    ProductDto getProductById(Long id);
    Boolean isProductActive(Long id);
    Boolean isProductExists(Long id);
    void activateProduct(Long id);
    void deActivateProduct(Long id);

    void updateQuantity(Long productID, Integer qty);
}
