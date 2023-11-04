package com.example.gamefusion.Services;

import com.example.gamefusion.Dto.BrandDto;
import com.example.gamefusion.Dto.CategoryDto;
import com.example.gamefusion.Dto.PaginationInfo;
import com.example.gamefusion.Dto.ProductDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AdminService {

     //? User management
     PaginationInfo getAllUsers(Integer pageNo, Integer pageSize);
     Boolean isUserExists(Integer id);
     Boolean isUserBlocked(Integer id);
     void blockUser(Integer id);
     void unBlockUser(Integer id);

     //? Category
     void addNewCategory(CategoryDto categoryDto);
     void updateCategory(CategoryDto categoryDto);
     PaginationInfo getAllCategory(Integer pageNo, Integer pageSize);
     List<CategoryDto> getAllCategory();
     CategoryDto getCategoryInfo(Long id);
     void toggleCategoryStatus(Long id);


    //! products
    PaginationInfo getAllProduct(Integer pageNo, Integer pageSize);
    Long addOrUpdateProduct(ProductDto productDto);
    List<String> uploadImage(List<MultipartFile> file, Long productId);

    //todo: edit
    ProductDto getProduct(Long id);
    void toggleStatus(Long id);

    List<BrandDto> getAllBrands();
    byte[] getImages(Long imageId);


    List<byte[]> getImageOfSingleProduct(Long productId);

    void deleteImage(Long imageId);

    Boolean isCategoryNameExist(String name);
}
