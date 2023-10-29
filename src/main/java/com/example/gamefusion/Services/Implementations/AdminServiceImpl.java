package com.example.gamefusion.Services.Implementations;

import com.example.gamefusion.Dto.CategoryDto;
import com.example.gamefusion.Dto.PaginationInfo;
import com.example.gamefusion.Services.AdminService;
import com.example.gamefusion.Services.CategoryService;
import com.example.gamefusion.Services.UserService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {

    private final UserService userService;
    private final CategoryService categoryService;
    @Autowired
    public AdminServiceImpl(UserService userService, CategoryService categoryService) {
        this.userService = userService;
        this.categoryService = categoryService;
    }

    //* User Ops

    @Override
    public PaginationInfo getAllUsers(Integer pageNo, Integer pageSize) {
        return userService.findAllUsers(pageNo, pageSize);
    }

    @Override
    public Boolean isUserExists(Integer id) {
        return userService.isExistsById(id);
    }

    @Override
    public Boolean isUserBlocked(Integer id) {
        return userService.isBlocked(id);
    }

    @Override
    @Transactional
    public void blockUser(Integer id) {
        userService.block(id);
    }

    @Override
    @Transactional
    public void unBlockUser(Integer id) {
        userService.unBlock(id);
    }


    //* Category Ops

    @Override
    public void addNewCategory(CategoryDto categoryDto) {
        categoryService.save(categoryDto);
    }

    @Override
    public void updateCategory(CategoryDto categoryDto) {
        categoryService.save(categoryDto);
    }

    @Override
    public PaginationInfo getAllCategory(Integer pageNo, Integer pageSize) {
        return categoryService.findAll(pageNo, pageSize);
    }

    @Override
    public List<CategoryDto> getAllCategory() {
        return categoryService.getAll();
    }

    @Override
    public CategoryDto getCategoryInfo(Long id) {
        return categoryService.findById(id);
    }

    @Override
    public void toggleCategoryStatus(Long id) {
        if (categoryService.isCategoryActive(id))
            categoryService.deActivateCategory(id);
        else
            categoryService.activateCategory(id);
    }
}
