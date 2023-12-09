package com.example.gamefusion.Services;

import java.util.List;
import com.example.gamefusion.Entity.Brand;
import com.example.gamefusion.Dto.ProductDto;
import com.example.gamefusion.Dto.CategoryDto;
import com.example.gamefusion.Dto.PaginationInfo;

public interface ProductService {
    void activateProduct(Long id);
    void deActivateProduct(Long id);
    Long save(ProductDto productDto);
    Boolean isProductExists(Long id);
    Boolean isProductActive(Long id);
    ProductDto getProductById(Long id);
    Integer getCountByBrand(Brand brand);
    List<ProductDto> search(String search);
    void updateQuantity(Long productID, Integer qty);
    PaginationInfo getAllProducts(Integer pageNo, Integer pageSize);
    PaginationInfo getAllActiveProducts(Integer pageNo, Integer pageSize);
    PaginationInfo search(Integer pageNo, Integer pageSize,String search);
    PaginationInfo getAllActiveProductsFromCategory(CategoryDto category, Integer pageNo, Integer pageSize);

}
