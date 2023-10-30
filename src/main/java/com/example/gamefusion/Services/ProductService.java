package com.example.gamefusion.Services;

import com.example.gamefusion.Dto.ProductDto;
import com.example.gamefusion.Entity.Product;

public interface ProductService {
    Long save(ProductDto productDto);
    ProductDto getProductById(Long id);

    ProductDto entityToDto(Product product);
    Product dtoToEntity(ProductDto dto);
}
