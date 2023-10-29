package com.example.gamefusion.Services;

import com.example.gamefusion.Dto.CategoryDto;
import com.example.gamefusion.Dto.PaginationInfo;

import java.util.List;

public interface AdminService {

     //? User management
     PaginationInfo getAllUsers(Integer pageNo, Integer pageSize);
     Boolean isUserExists(Integer id);
     Boolean isUserBlocked(Integer id);
     void blockUser(Integer id);
     void unBlockUser(Integer id);

     //! Category
     void addNewCategory(CategoryDto categoryDto);
     void updateCategory(CategoryDto categoryDto);
     PaginationInfo getAllCategory(Integer pageNo, Integer pageSize);
     List<CategoryDto> getAllCategory();
     CategoryDto getCategoryInfo(Long id);
     void toggleCategoryStatus(Long id);

     //! products
     //todo: view
     //todo: add
     //todo: edit
     //todo: soft delete

}
