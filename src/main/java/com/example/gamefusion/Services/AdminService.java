package com.example.gamefusion.Services;

import com.example.gamefusion.Dto.*;
import com.example.gamefusion.Entity.Brand;
import com.example.gamefusion.Entity.BrandLogo;
import com.example.gamefusion.Entity.OrderMain;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Date;
import java.util.List;
import java.util.Map;

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
    BrandLogo saveBrandLogo(MultipartFile file);
    Integer getProductCountByBrand(Long brandID);
    Map<Long, Integer> getProductCountByBrandPage(List<BrandDto> brandDtoList);

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
    AddressDto getUserAddress(Integer addressId);
    Map<Integer, PaymentDto> getPaymentInfoByOrder(List<OrderMain> contents);

    //? Sales Report
    Map<String,Integer> filterGraphBasedOnDate(String filterBy);
    PaginationInfo filterOrderByStatus(Integer pageNo, Integer pageSize, Integer statusFilter);
    PaginationInfo filterOrderByDate(Integer pageNo, Integer pageSize,String startDate, String endDate);
    PaginationInfo filterOrderByDayAndStatus(Integer pageNo, Integer pageSize, Date date, Integer statusFilter);
    PaginationInfo filterOrderByDateAndStatus(Integer pageNo, Integer pageSize,String startDate, String endDate, Integer statusFilter);

    //? Coupon
    PaginationInfo getAllCoupons(Integer pageNo, Integer pageSize);
    CouponDto getCoupon(Integer id);
    CouponDto saveOrUpdate(CouponDto couponDto);
    PaginationInfo filterByExpiryStatus(Integer pageNo, Integer pageSize);
    Boolean isCouponExist(Integer couponId);
    String getCouponCode();

    void deleteCoupon(Integer couponId);
}
