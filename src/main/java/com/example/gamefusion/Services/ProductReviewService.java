package com.example.gamefusion.Services;

import com.example.gamefusion.Dto.PaginationInfo;
import com.example.gamefusion.Dto.ProductDto;
import com.example.gamefusion.Dto.ProductReviewDto;
import com.example.gamefusion.Dto.UserDto;
import com.example.gamefusion.Entity.ProductReview;

import java.util.List;

public interface ProductReviewService {
    Boolean isExistsById(Long id);
    ProductReviewDto findById(Long id);
    Boolean isExistsByUser(UserDto userDto);
    PaginationInfo findByUser(Integer pageNo, Integer pageSize, UserDto userDto);
    List<ProductReviewDto> findByUser(UserDto userDto);
    Boolean isExistsByProduct(ProductDto productDto);
    ProductReviewDto save(ProductReviewDto productReviewDto);
    PaginationInfo findByProduct(Integer pageNo, Integer pageSize, ProductDto productDto);
    List<ProductReview> findByProduct(ProductDto productDto);
}
