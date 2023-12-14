package com.example.gamefusion.Services;

import java.util.List;

import com.example.gamefusion.Dto.BrandDto;
import com.example.gamefusion.Entity.Brand;
import com.example.gamefusion.Dto.ProductDto;
import com.example.gamefusion.Dto.CategoryDto;
import com.example.gamefusion.Dto.PaginationInfo;
import com.example.gamefusion.Entity.Category;

public interface ProductService {
    void activateProduct(Long id);
    void deActivateProduct(Long id);
    Long save(ProductDto productDto);
    Boolean isProductExists(Long id);
    Boolean isProductActive(Long id);
    ProductDto getProductById(Long id);
    Integer getCountByBrand(Brand brand);
    List<ProductDto> search(String search);
    Integer getCountByBrand(BrandDto brand);
    Integer getCountByCategory(Category category);
    Integer getCountByCategory(CategoryDto category);
    void updateQuantity(Long productID, Integer qty);
    PaginationInfo getAllProducts(Integer pageNo, Integer pageSize);
    Integer getCountByPriceFilter(Integer minPrice, Integer maxPrice);
    PaginationInfo getAllActiveProducts(Integer pageNo, Integer pageSize);
    PaginationInfo search(Integer pageNo, Integer pageSize,String search);
    Integer getCountByBrandPriceFilter(BrandDto brand, int minPrice, int maxPrice);
    Integer getCountByCategoryPriceFilter(CategoryDto category, int minPrice, int maxPrice);
    PaginationInfo getAllActiveProductsFromBrand(BrandDto brandDto, Integer pageNo, Integer pageSize);
    PaginationInfo filterByPrice(Integer minPrice, Integer maxPrice, Integer pageNo, Integer pageSize);
    PaginationInfo getAllActiveProductsFromCategory(CategoryDto category, Integer pageNo, Integer pageSize);
    PaginationInfo filterByPriceAndBrand(BrandDto brand, int minPrice, int maxPrice, Integer pageNo, Integer pageSize);
    PaginationInfo filterByPriceAndCategory(CategoryDto category, int minPrice, int maxPrice, Integer pageNo, Integer pageSize);
}
