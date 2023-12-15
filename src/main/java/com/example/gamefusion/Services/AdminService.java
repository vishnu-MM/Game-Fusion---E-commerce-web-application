package com.example.gamefusion.Services;

import java.sql.Date;
import java.util.Map;
import java.util.List;
import com.example.gamefusion.Dto.*;
import com.example.gamefusion.Entity.BrandLogo;
import com.example.gamefusion.Entity.OrderMain;
import org.springframework.web.multipart.MultipartFile;

public interface AdminService {

     //? User management
     void blockUser(Integer id);
     void unBlockUser(Integer id);
     Boolean isUserExists(Integer id);
     Boolean isUserBlocked(Integer id);
     PaginationInfo getAllUsers(Integer pageNo, Integer pageSize);

     //? Category
     List<CategoryDto> getAllCategory();
     void toggleCategoryStatus(Long id);
     CategoryDto getCategoryInfo(Long id);
     Boolean isCategoryNameExist(String name);
     CategoryDto addNewCategory(CategoryDto categoryDto);
     CategoryDto updateCategory(CategoryDto categoryDto);
     PaginationInfo getAllCategory(Integer pageNo, Integer pageSize);

    //?Brands
    List<BrandDto> getAllBrands();
    BrandDto getBrandInfo(Long brandId);
    Boolean isBrandActive(Long brandId);
    void toggleBrandStatus(Long brandId);
    Boolean isBrandExistsById(Long brandId);
    Boolean isBrandExistsByName(String name);
    BrandLogo saveBrandLogo(MultipartFile file);
    BrandDto addOrUpdateBrand(BrandDto brandDto);
    Integer getProductCountByBrand(Long brandID);
    PaginationInfo getAllBrands(Integer pageNo, Integer pageSize);
    Map<Long, Integer> getProductCountByBrandPage(List<BrandDto> brandDtoList);

    //? products
    void toggleStatus(Long id);
    ProductDto getProduct(Long id);
    Long addOrUpdateProduct(ProductDto productDto);
    PaginationInfo getAllProduct(Integer pageNo, Integer pageSize);
    List<String> uploadImage(List<MultipartFile> file, Long productId);

    //? Images
    byte[] getImages(Long imageId);
    void deleteImage(Long imageId);
    List<byte[]> getImageOfSingleProduct(Long productId);

    //? Order Main & Sub
    List<OrderMain> getAllOrders();
    Integer getCancelOrderRequestCount();
    Boolean isOrderExists(Integer orderId);
    OrderMainDto getOrderById(Integer orderId);
    AddressDto getUserAddress(Integer addressId);
    List<OrderSubDto> findOrderSubByMain(Integer orderId);
    OrderMainDto updateOrderMain(OrderMainDto orderMainDto);
    List<OrderHistoryDto> findOrderHistory(Integer orderId);
    PaymentDto getPaymentInfoByOrder(OrderMainDto orderMain);
    OrderHistoryDto saveOrderHistory(OrderMainDto orderMainDto);
    PaginationInfo getAllOrders(Integer pageNo, Integer pageSize);
    Map<Integer, PaymentDto> getPaymentInfoByOrder(List<OrderMain> contents);

    //? Sales Report
    Map<String,Integer> filterGraphBasedOnDate(String filterBy);
    PaginationInfo getPurchaseReport(Integer pageNo, Integer pageSize);
    PaginationInfo filterOrderByStatus(Integer pageNo, Integer pageSize, Integer statusFilter);
    PaginationInfo filterOrderByDate(Integer pageNo, Integer pageSize,String startDate, String endDate);
    PaginationInfo filterOrderByDayAndStatus(Integer pageNo, Integer pageSize, Date date, Integer statusFilter);
    PaginationInfo filterOrderByDateAndStatus(Integer pageNo, Integer pageSize,String startDate, String endDate, Integer statusFilter);

    //? Coupon
    String getCouponCode();
    CouponDto getCoupon(Integer id);
    void deleteCoupon(Integer couponId);
    Boolean isCouponExist(Integer couponId);
    CouponDto saveOrUpdate(CouponDto couponDto);
    PaginationInfo getAllCoupons(Integer pageNo, Integer pageSize);
    PaginationInfo filterByExpiryStatus(Integer pageNo, Integer pageSize);

    //? Category Offer
    OrderMainDto approveCancelRequest(Integer orderId);
    CategoryOfferDto getCategoryOffer(Long categoryId);
    Boolean isCategoryOfferExists(CategoryDto categoryDto);
    PaginationInfo getCancelRequest(Integer pageNo, Integer pageSize);
    CategoryOfferDto saveCategoryOffer(CategoryOfferDto categoryOfferDto);

    //? Search
    List<UserDto> searchUser(String search);
    List<BrandDto> searchBrand(String search);
    List<ProductDto> searchProduct(String search);
    List<CategoryDto> searchCategory(String search);
}
