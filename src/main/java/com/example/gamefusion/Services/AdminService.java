package com.example.gamefusion.Services;

import com.example.gamefusion.Dto.*;
import com.example.gamefusion.Entity.BrandLogo;
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
     Boolean isCategoryNameExist(String name);

    //?Brands
    List<BrandDto> getAllBrands();
    PaginationInfo getAllBrands(Integer pageNo, Integer pageSize);
    BrandDto getBrandInfo(Long brandId);
    BrandDto addOrUpdateBrand(BrandDto brandDto);
    Boolean isBrandExistsById(Long brandId);
    Boolean isBrandExistsByName(String name);
    Boolean isBrandActive(Long brandId);
    void toggleBrandStatus(Long brandId);

    //? products
    PaginationInfo getAllProduct(Integer pageNo, Integer pageSize);
    Long addOrUpdateProduct(ProductDto productDto);
    List<String> uploadImage(List<MultipartFile> file, Long productId);
    ProductDto getProduct(Long id);
    void toggleStatus(Long id);

    //? Images
    byte[] getImages(Long imageId);
    List<byte[]> getImageOfSingleProduct(Long productId);
    void deleteImage(Long imageId);

    //? Order Main & Sub
    PaginationInfo getAllOrders(Integer pageNo, Integer pageSize);
    OrderMainDto getOrderById(Integer orderId);
    Boolean isOrderExists(Integer orderId);
    void updateOrderMain(OrderMainDto orderMainDto);
    List<OrderSubDto> findOrderSubByMain(Integer orderId);

    BrandLogo saveBrandLogo(MultipartFile file);
}
