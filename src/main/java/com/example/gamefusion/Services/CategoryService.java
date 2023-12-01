package com.example.gamefusion.Services;

import com.example.gamefusion.Dto.CategoryDto;
import com.example.gamefusion.Dto.PaginationInfo;
import com.example.gamefusion.Entity.Category;

import java.util.List;

public interface CategoryService {

    CategoryDto save(CategoryDto dto);
    void activateCategory(Long id);
    void deActivateCategory(Long id);
    CategoryDto findById(Long id);
    PaginationInfo findAll(Integer pageNo, Integer pageSize);
    Boolean isExistsByName(String name);
    List<CategoryDto> getAll();
    List<String> getAllNames();
    Boolean isCategoryExist(Long id);
    Boolean isCategoryActive(Long id);
}
