package com.example.gamefusion.Services;

import com.example.gamefusion.Dto.CategoryDto;
import com.example.gamefusion.Dto.CategoryOfferDto;

public interface CategoryOfferService {
    Boolean isExistsById(Integer id);
    CategoryOfferDto findById(Integer id);
    Boolean isExistsByCategory(CategoryDto categoryDto);
    CategoryOfferDto save(CategoryOfferDto categoryOfferDto);
    CategoryOfferDto findByCategory(CategoryDto categoryDto);
}
