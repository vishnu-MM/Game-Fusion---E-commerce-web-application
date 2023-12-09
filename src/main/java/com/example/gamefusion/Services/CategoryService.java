package com.example.gamefusion.Services;

import com.example.gamefusion.Dto.CategoryDto;
import com.example.gamefusion.Dto.PaginationInfo;
import com.example.gamefusion.Entity.Category;

import java.util.List;

public interface CategoryService {
    List<CategoryDto> getAll();
    List<CategoryDto> getAllAvailable();
    List<String> getAllNames();
    CategoryDto findById(Long id);
    void activateCategory(Long id);
    Boolean isCategoryExist(Long id);
    void deActivateCategory(Long id);
    CategoryDto save(CategoryDto dto);
    Boolean isCategoryActive(Long id);
    Boolean isExistsByName(String name);
    List<CategoryDto> search(String search);
    List<CategoryDto> searchAvailable(String search);
    PaginationInfo findAll(Integer pageNo, Integer pageSize);
}
