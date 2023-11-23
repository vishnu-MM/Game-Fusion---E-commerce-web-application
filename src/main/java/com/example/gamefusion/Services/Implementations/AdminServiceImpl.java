package com.example.gamefusion.Services.Implementations;

import com.example.gamefusion.Configuration.UtilityClasses.EntityDtoConversionUtil;
import com.example.gamefusion.Configuration.UtilityClasses.OrderStatusUtil;
import com.example.gamefusion.Dto.*;
import com.example.gamefusion.Entity.Brand;
import com.example.gamefusion.Entity.BrandLogo;
import com.example.gamefusion.Entity.Product;
import com.example.gamefusion.Services.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.sql.Date;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdminServiceImpl implements AdminService {
    private final UserService userService;
    private final ProductService productService;
    private final CategoryService categoryService;
    private final StorageService storageService;
    private final ImagesService imagesService;
    private final BrandService brandService;
    private final OrderMainService orderMainService;
    private final OrderSubService orderSubService;
    private final AddressService addressService;
    private final EntityDtoConversionUtil conversionUtil;

    @Autowired
    public AdminServiceImpl(
            UserService userService, @Lazy ProductService productService,
            CategoryService categoryService, StorageService storageService,
            ImagesService imagesService, BrandService brandService,
            OrderMainService orderMainService, OrderSubService orderSubService,
            AddressService addressService, EntityDtoConversionUtil conversionUtil) {
        this.userService = userService;
        this.productService = productService;
        this.categoryService = categoryService;
        this.storageService = storageService;
        this.imagesService = imagesService;
        this.brandService = brandService;
        this.orderMainService = orderMainService;
        this.orderSubService = orderSubService;
        this.addressService = addressService;
        this.conversionUtil = conversionUtil;
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
//        ProductDto oldProductDto = productService.getProductById(productDto.getId());
//        productDto.setImageIds(oldProductDto.getImageIds());
        return productService.save(productDto);
    }

    @Override
    public void toggleStatus(Long id) {
        if (!productService.isProductExists(id)){
            throw new EntityNotFoundException("Product not found");
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
        try {
            if (!imagesService.isImageExists(imageId))
                throw new EntityNotFoundException("Image not found");
            if (storageService.deleteImage(imageId)) {
                imagesService.deleteImageById(imageId);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //* ORDER OPS

    @Override
    public PaginationInfo getAllOrders(Integer pageNo, Integer pageSize) {
        return orderMainService.findAll(pageNo,pageSize);
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
        throw new EntityNotFoundException("Order not found");
    }

    @Override
    public AddressDto getUserAddress(Integer addressId) {
        //! Exception Handling
        return addressService.findById(addressId);
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
                LocalDate newEndDate = LocalDate.parse(endDate.getYear()+"-12-31");
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
    public void updateOrderMain(OrderMainDto orderMainDto) {
        orderMainService.save(orderMainDto);
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
        if (!isBrandExistsById(brandId)) throw new EntityNotFoundException("Brand Not Found");
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
    public Integer getProductCountByBrand(Long brandID) {
        if (brandService.existsById(brandID)) throw new EntityNotFoundException("Brand Not found");
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
}