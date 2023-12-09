package com.example.gamefusion.Services.Implementations;

import com.example.gamefusion.Configuration.ExceptionHandlerConfig.EntityNotFound;
import com.example.gamefusion.Configuration.UtilityClasses.EntityDtoConversionUtil;
import com.example.gamefusion.Configuration.UtilityClasses.OrderStatusUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.context.annotation.Lazy;
import java.time.format.DateTimeParseException;
import org.springframework.stereotype.Service;
import com.example.gamefusion.Services.*;
import jakarta.transaction.Transactional;
import com.example.gamefusion.Entity.*;
import com.example.gamefusion.Dto.*;
import java.time.DateTimeException;
import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Objects;
import java.util.List;
import java.sql.Date;
import java.util.Map;

@Service
public class AdminServiceImpl implements AdminService {
    private final UserService userService;
    private final BrandService brandService;
    private final ImagesService imagesService;
    private final CouponService couponService;
    private final StorageService storageService;
    private final ProductService productService;
    private final AddressService addressService;
    private final PaymentService paymentService;
    private final CategoryService categoryService;
    private final OrderSubService orderSubService;
    private final OrderMainService orderMainService;
    private final EntityDtoConversionUtil conversionUtil;
    private final OrderHistoryService orderHistoryService;
    private final CategoryOfferService categoryOfferService;

    @Autowired
    public AdminServiceImpl(OrderHistoryService orderHistoryService,
                            CategoryOfferService categoryOfferService,
                            ImagesService imagesService, BrandService brandService,
                            UserService userService, @Lazy ProductService productService,
                            CategoryService categoryService, StorageService storageService,
                            CouponService couponService, OrderMainService orderMainService,
                            OrderSubService orderSubService, AddressService addressService,
                            PaymentService paymentService, EntityDtoConversionUtil conversionUtil) {
        this.userService = userService;
        this.brandService = brandService;
        this.imagesService = imagesService;
        this.couponService = couponService;
        this.productService = productService;
        this.storageService = storageService;
        this.addressService = addressService;
        this.paymentService = paymentService;
        this.conversionUtil = conversionUtil;
        this.categoryService = categoryService;
        this.orderSubService = orderSubService;
        this.orderMainService = orderMainService;
        this.orderHistoryService = orderHistoryService;
        this.categoryOfferService = categoryOfferService;
    }

    //* USER OPS

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


    //* CATEGORY OPS

    @Override
    public CategoryDto addNewCategory(CategoryDto categoryDto) {
        return categoryService.save(categoryDto);
    }

    @Override
    public CategoryDto updateCategory(CategoryDto categoryDto) {
        return categoryService.save(categoryDto);
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

    @Override
    public Boolean isCategoryNameExist(String name) {
        return categoryService.isExistsByName(name);
    }

    //* PRODUCT OPS

    public PaginationInfo getAllProduct(Integer pageNo, Integer pageSize) {
        return productService.getAllProducts(pageNo,pageSize);
    }

    @Override
    public ProductDto getProduct(Long id) {
        return productService.getProductById(id);
    }

    @Override
    public Long addOrUpdateProduct(ProductDto productDto) {
/*      ProductDto oldProductDto = productService.getProductById(productDto.getId());
        productDto.setImageIds(oldProductDto.getImageIds());*/
        return productService.save(productDto);
    }

    @Override
    public void toggleStatus(Long id) {
        if (!productService.isProductExists(id)){
            throw new EntityNotFound("Product not found");
        }
        if (productService.isProductActive(id)) {
            productService.deActivateProduct(id);
        }
        else {
            productService.activateProduct(id);
        }
    }

    //* IMAGES OPS

    @Override
    public byte[] getImages(Long imageId) {
        try{
            return storageService.downloadImageFromFileSystem(imageId);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<String> uploadImage(List<MultipartFile> file, Long id) {
        Product product =conversionUtil.dtoToEntity( productService.getProductById(id));
        return storageService.uploadImagesToFileSystem(file,product);
    }

    @Override
    public List<byte[]> getImageOfSingleProduct(Long productId) {
        return imagesService.getImageOfSingleProduct(productId);
    }

    @Override
    public void deleteImage(Long imageId) {
        if (!imagesService.isImageExists(imageId))
            throw new EntityNotFound("Image not found");
        try {
            storageService.deleteImage(imageId);
            imagesService.deleteImageById(imageId);
        } catch (IOException e) { throw new RuntimeException(e); }
    }

    //* ORDER OPS

    @Override
    public PaginationInfo getAllOrders(Integer pageNo, Integer pageSize) {
        return orderMainService.findAll(pageNo,pageSize);
    }

    @Override
    public List<OrderMain> getAllOrders() {
        return orderMainService.findAll();
    }

    @Override
    public OrderMainDto getOrderById(Integer orderId) {
        return orderMainService.findOrderById(orderId);
    }

    @Override
    public Boolean isOrderExists(Integer orderId) {
        return orderMainService.isExistsByID(orderId);
    }

    @Override
    public List<OrderSubDto> findOrderSubByMain(Integer orderId) {
        OrderMainDto orderMain = orderMainService.findOrderById(orderId);
        if ( orderMain!=null )
            return orderSubService.findOrderByOrder(orderMain);
        throw new EntityNotFound("Order not found");
    }

    @Override
    public AddressDto getUserAddress(Integer addressId) {
        //! Exception Handling
        return addressService.findById(addressId);
    }

    @Override
    public List<OrderHistoryDto> findOrderHistory(Integer orderId) {
        return orderHistoryService.findByOrderId(orderId);
    }

    @Override
    public Map<String, Integer> filterGraphBasedOnDate(String filterBy) {
        LocalDate endDate = LocalDate.now();
        switch (filterBy) {
            case "MONTH" -> {
                LocalDate startDate = endDate.minusDays(endDate.getDayOfMonth() - 1);
                if (startDate.isAfter(endDate))
                    throw new DateTimeException("Invalid Start and Ending date");
                return orderMainService.getOrderCountByMonth(startDate, endDate);
            }
            case "YEAR" -> {
                LocalDate startDate = LocalDate.parse(endDate.getYear()+"-01-01");
                if (startDate.isAfter(endDate))
                    throw new DateTimeException("Invalid Start and Ending date");
                return orderMainService.getOrderCountByYear(startDate, endDate);
            }
            case "WEEK" -> {
                LocalDate startDate = endDate.minusDays( endDate.getDayOfWeek().getValue());
                if (startDate.isAfter(endDate))
                    throw new DateTimeException("Invalid Start and Ending date");
                return orderMainService.getOrderCountByWeek(startDate, endDate);

            }
            default -> throw new RuntimeException();
        }
    }

    @Override
    public OrderMainDto updateOrderMain(OrderMainDto orderMainDto) {
        return orderMainService.save(orderMainDto);
    }

    //* BRAND OPS

    @Override
    public List<BrandDto> getAllBrands() {
        return brandService.getAll();
    }

    @Override
    public PaginationInfo getAllBrands(Integer pageNo, Integer pageSize) {
        return brandService.getAll(pageNo, pageSize);
    }

    @Override
    public BrandDto getBrandInfo(Long brandId) {
        return brandService.findById(brandId);
    }

    @Override
    public BrandDto addOrUpdateBrand(BrandDto brandDto) {
        return brandService.saveOrUpdate(brandDto);
    }

    @Override
    public Boolean isBrandExistsById(Long brandId) {
        return brandService.existsById(brandId);
    }

    @Override
    public Boolean isBrandExistsByName(String name) {
        return brandService.existsByName(name);
    }

    @Override
    public Boolean isBrandActive(Long brandId) {
        return brandService.isActive(brandId);
    }

    public void toggleBrandStatus(Long brandId) {
        if (!isBrandExistsById(brandId)) throw new EntityNotFound("Brand Not Found");
        if (isBrandActive(brandId)) brandService.updateStatusInActive(brandId);
        else brandService.updateStatusActive(brandId);
    }

    @Override
    public BrandLogo saveBrandLogo(MultipartFile file) {
        try {
            return storageService.uploadImage(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public PaginationInfo filterOrderByDate(Integer pageNo, Integer pageSize, String startDateStr, String endDateStr) {
        Date startDate = convertStringToDate(startDateStr);
        Date endDate = convertStringToDate(endDateStr);
        if (startDate.compareTo(endDate) > 0 ){
            return orderMainService.findAll(pageNo, pageSize);
        }
        if (startDate.equals(endDate)){
            return orderMainService.findAllFilterByDay(pageNo,pageSize,endDate);
        }
        return orderMainService.findAllBetweenDay(pageNo,pageSize,startDate,endDate);
    }

    @Override
    public PaginationInfo filterOrderByDayAndStatus(Integer pageNo, Integer pageSize, Date date, Integer statusFilter) {
        String status = OrderStatusUtil.getOrderStatusByValue(statusFilter);
        return orderMainService.findByDayAndStatus(pageNo, pageSize, date, status);
    }

    @Override
    public PaginationInfo filterOrderByStatus(Integer pageNo, Integer pageSize, Integer statusFilter) {
        String status = OrderStatusUtil.getOrderStatusByValue(statusFilter);
        return orderMainService.findOrderByStatus(status, pageNo, pageSize);
    }

    @Override
    public PaginationInfo filterOrderByDateAndStatus(Integer pageNo, Integer pageSize, String startDateStr,
                                                     String endDateStr, Integer statusFilter) {
        if ((startDateStr == null || startDateStr.equals("null")) && (endDateStr == null || endDateStr.equals("null"))) {
            return filterOrderByStatus(pageNo, pageSize, statusFilter);
        }
        String status = OrderStatusUtil.getOrderStatusByValue(statusFilter);
        Date startDate = convertStringToDate(startDateStr);
        Date endDate = convertStringToDate(endDateStr);

        if (startDate.equals(endDate)){
            return orderMainService.findByDayAndStatus(pageNo,pageSize,endDate,status);
        }
        return orderMainService.findByDatesBetweenAndStatus(pageNo,pageSize,startDate,endDate,status);
    }

    @Override
    public Map<Integer, PaymentDto> getPaymentInfoByOrder(List<OrderMain> contents) {
        Map<Integer, PaymentDto> paymentDtoMap = new HashMap<>();
        for (OrderMain OrderMain: contents) {
            paymentDtoMap.put(
                    OrderMain.getId(),
                    paymentService.findByOrderMain(conversionUtil.entityToDto(OrderMain))
            );
        }
        return paymentDtoMap;
    }

    @Override
    public OrderHistoryDto saveOrderHistory(OrderMainDto orderMainDto) {
        return orderHistoryService.save(orderMainDto);
    }

    @Override
    public PaymentDto getPaymentInfoByOrder(OrderMainDto orderMain) {
        return paymentService.findByOrderMain(orderMain);
    }

    @Override
    public Integer getCancelOrderRequestCount() {
        Integer count =  orderMainService.countCancelRequest();
        return (count == null) ? 0 : count;
    }

    @Override
    public Integer getProductCountByBrand(Long brandID) {
        if (brandService.existsById(brandID)) throw new EntityNotFound("Brand Not found");
        Brand brand = conversionUtil.dtoToEntity(brandService.findById(brandID));
        return productService.getCountByBrand(brand);
    }

    @Override
    public Map<Long, Integer> getProductCountByBrandPage(List<BrandDto> brandDtoList) {
        Map<Long, Integer> brandProductCount = new HashMap<>();
        for ( BrandDto brandDto : brandDtoList ) {

            brandProductCount.put(brandDto.getId(),
                    productService.getCountByBrand(conversionUtil.dtoToEntity(brandDto)));
        }
        return brandProductCount;
    }

    private Date convertStringToDate(String DateStr) {
        Date date;
        if (DateStr == null || DateStr.equals("null")) {
            date = Date.valueOf(LocalDate.now());
        } else {
            try {
                date = Date.valueOf(LocalDate.parse(DateStr));
            } catch (DateTimeParseException e) {
                date = Date.valueOf(LocalDate.now());
            }
        }
        return date;
    }

    @Override
    public PaginationInfo getAllCoupons(Integer pageNo, Integer pageSize) {
        return couponService.findAll(pageNo,pageSize);
    }

    @Override
    public CouponDto getCoupon(Integer couponId) {
        return couponService.findById(couponId);
    }

    @Override
    public CouponDto saveOrUpdate(CouponDto couponDto) {
        return couponService.save(couponDto);
    }

    @Override
    public PaginationInfo filterByExpiryStatus(Integer pageNo, Integer pageSize) {
        return null;
    }

    @Override
    public Boolean isCouponExist(Integer couponId) {
        return couponService.isExistById(couponId);
    }

    @Override
    public String getCouponCode() {
        return couponService.generateCoupon();
    }

    @Override
    public void deleteCoupon(Integer couponId) {
        couponService.delete(couponId);
    }

    @Override
    public Boolean isCategoryOfferExists(CategoryDto categoryDto) {
        return categoryOfferService.isExistsByCategory(categoryDto);
    }

    @Override
    public CategoryOfferDto saveCategoryOffer(CategoryOfferDto categoryOfferDto) {
        return categoryOfferService.save(categoryOfferDto);
    }

    @Override
    public CategoryOfferDto getCategoryOffer(Long categoryId) {
        CategoryDto categoryDto = categoryService.findById(categoryId);
        if (!categoryOfferService.isExistsByCategory(categoryDto)) return null;
        return categoryOfferService.findByCategory(categoryDto);
    }

    @Override
    public PaginationInfo getCancelRequest(Integer pageNo, Integer pageSize) {
        return orderMainService.findOrderByStatus(String.valueOf(OrderStatusUtil.REQUEST_CANCEL),pageNo,pageSize);
    }

    @Override
    public OrderMainDto approveCancelRequest(Integer orderId) {
       String status = orderMainService.findOrderById(orderId).getStatus();
       if (Objects.equals(status, String.valueOf(OrderStatusUtil.REQUEST_CANCEL)) ||
           Objects.equals(status, String.valueOf(OrderStatusUtil.REQUEST_REPLACE)) ) {
           return orderMainService.approveCancelRequest(orderId);
       }
       throw new RuntimeException("Invalid  Status to approve");
    }

    @Override
    public List<UserDto> searchUser(String search) {
        return null;
    }

    @Override
    public List<CategoryDto> searchCategory(String search) {
        return categoryService.search(search);
    }

    @Override
    public List<BrandDto> searchBrand(String search) {
        return null;
    }

    @Override
    public List<ProductDto> searchProduct(String search) {
        return productService.search(search);
    }
}