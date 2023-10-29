package com.example.gamefusion.Services;

import com.example.gamefusion.Dto.CategoryDto;
import com.example.gamefusion.Dto.PaginationInfo;

import java.util.List;

public interface CategoryService {
    void save(CategoryDto dto);
    CategoryDto findById(Long id);
    PaginationInfo findAll(Integer pageNo, Integer pageSize);
    List<CategoryDto> getAll();
    List<String> getAllNames();
    void activateCategory(Long id);
    void deActivateCategory(Long id);
    Boolean isCategoryExist(Long id);
    Boolean isCategoryActive(Long id);

}
