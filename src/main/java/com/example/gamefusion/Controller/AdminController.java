package com.example.gamefusion.Controller;

import com.example.gamefusion.Configuration.UtilityClasses.SalesReportExcelExporter;
import com.example.gamefusion.Configuration.UtilityClasses.PageToListUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;
import com.example.gamefusion.Services.AdminService;
import org.springframework.validation.BindingResult;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletResponse;
import com.example.gamefusion.Entity.BrandLogo;
import com.example.gamefusion.Entity.OrderMain;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.ui.Model;
import com.example.gamefusion.Dto.*;
import java.text.SimpleDateFormat;
import jakarta.validation.Valid;
import java.text.DecimalFormat;
import java.text.DateFormat;
import java.sql.Date;
import java.util.*;

@Controller
@RequestMapping("/dashboard")
public class AdminController {

    private final AdminService adminService;

    @Autowired
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    //* DASH BOARD

    @GetMapping("")
    public String home() {
        return "Admin/index";
    }

    @GetMapping("/fetchData/Month")
    @ResponseBody
    public ResponseEntity<Map<String, Integer>> getGraphByDay() {
        Map<String, Integer> response = adminService.filterGraphBasedOnDate("MONTH");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/fetchData/week")
    @ResponseBody
    public ResponseEntity<Map<String, Integer>> getGraphByWeek() {
        Map<String, Integer> response = adminService.filterGraphBasedOnDate("WEEK");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/fetchData/year")
    @ResponseBody
    public ResponseEntity<Map<String, Integer>> getGraphByYear() {
        Map<String, Integer> response = adminService.filterGraphBasedOnDate("YEAR");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    //* USER MANAGEMENT

    @GetMapping("/view-users")
    public String getListOfAllUsers(Model model,
                                    @RequestParam(value = "error", defaultValue = "false", required = false) Boolean error,
                                    @RequestParam(value = "pageNo", defaultValue = "0", required = false) Integer pageNo,
                                    @RequestParam(value = "pageSize", defaultValue = "5", required = false) Integer pageSize) {
        model.addAttribute("Error", error);
        model.addAttribute("UserList", adminService.getAllUsers(pageNo, pageSize));
        return "Admin/page-view-users";
    }

    @PutMapping("/block-unblock-user/{id}")
    public String UnBlockUser(@PathVariable("id") Integer id) {
        if (!adminService.isUserExists(id))
            return "redirect:/dashboard/view-users?error=" + true;
        if (!adminService.isUserBlocked(id))
            adminService.unBlockUser(id);
        else if (adminService.isUserBlocked(id))
            adminService.blockUser(id);
        return "redirect:/dashboard/view-users";
    }

    //* CATEGORY MANAGEMENT

    @GetMapping("/view-categories")
    public String getListOfAllCategory(Model model,
                                       @RequestParam(value = "pageNo", defaultValue = "0", required = false) Integer pageNo,
                                       @RequestParam(value = "pageSize", defaultValue = "5", required = false) Integer pageSize) {
        PaginationInfo paginationInfo = adminService.getAllCategory(pageNo, pageSize);
        model.addAttribute("CategoryList", paginationInfo);
        return "Admin/page-view-categories";
    }

    @GetMapping("/get-category/{parentId}")
    @ResponseBody
    public ResponseEntity<String> fetchParentCategoryName(@PathVariable Long parentId) {
        if (parentId != null) {
            CategoryDto parentCategory = adminService.getCategoryInfo(parentId);
            if (parentCategory != null) {
                return ResponseEntity.ok(parentCategory.getName());
            }
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/add-category")
    public String getAddCategoryForm(Model model, @ModelAttribute("CategoryDetails") CategoryDto categoryDto) {
        model.addAttribute("NewCategory", new CategoryDto());
        model.addAttribute("parentList", adminService.getAllCategory());
        return "Admin/page-add-category";
    }

    @PostMapping("/add-category/save")
    public String addNewCategory(@Valid @ModelAttribute("NewCategory") CategoryDto newCategory, BindingResult result,
                                 @RequestParam(name = "minimumAmount", required = false) Integer minimumAmount,
                                 @RequestParam(name = "discount", required = false) Double discount,
                                 @RequestParam(name = "expiryDate", required = false) String expiryDate) {

        if (adminService.isCategoryNameExist(newCategory.getName()))
            result.rejectValue("name", String.valueOf(HttpStatus.CONFLICT),
                                "Category with the same name exists");

        if (result.hasErrors()) return "Admin/page-add-category";

        CategoryDto categoryDto = adminService.addNewCategory(newCategory);

        if (minimumAmount != null && discount != null && expiryDate != null) {
            CategoryOfferDto categoryOffer = new CategoryOfferDto();
            categoryOffer.setMinimumAmount(minimumAmount);
            categoryOffer.setExpiryDate(Date.valueOf(expiryDate));
            categoryOffer.setCategoryId(categoryDto.getId());
            categoryOffer.setDiscount(
                    Double.valueOf(new DecimalFormat("000.00").format(discount))
            );
            adminService.saveCategoryOffer(categoryOffer);
        }
        return "redirect:/dashboard/view-categories?success";
    }

    @GetMapping("/edit-category/{id}")
    public String getEditCategoryForm(@PathVariable("id") Long id, Model model) {
        model.addAttribute("parentList", adminService.getAllCategory());
        model.addAttribute("OfferDetails", adminService.getCategoryOffer(id));
        model.addAttribute("CategoryDetails", adminService.getCategoryInfo(id));
        return "Admin/page-edit-category";
    }

    @PutMapping("/edit-category/update")
    public String editCategory(@Valid @ModelAttribute("NewCategory") CategoryDto newCategory,
                               BindingResult result, Model model,
                               @RequestParam(name = "offerId", required = false) Long offerId,
                               @RequestParam(name = "minimumAmount", required = false) Integer minimumAmount,
                               @RequestParam(name = "discount", required = false) Double discount,
                               @RequestParam(name = "expiryDate", required = false) String expiryDate) {

        if (result.hasErrors()) {
            model.addAttribute("parentList", adminService.getAllCategory());
            model.addAttribute("CategoryDetails", adminService.getCategoryInfo(newCategory.getId()));
            return "Admin/page-edit-category";
        } else {
            CategoryDto categoryDto = adminService.updateCategory(newCategory);
            if (minimumAmount != null && discount != null && expiryDate != null) {
                CategoryOfferDto categoryOffer = new CategoryOfferDto();
                categoryOffer.setMinimumAmount(minimumAmount);
                categoryOffer.setExpiryDate(Date.valueOf(expiryDate));
                categoryOffer.setCategoryId(categoryDto.getId());
                categoryOffer.setDiscount(
                        Double.valueOf(new DecimalFormat("000.00").format(discount))
                );
                if (offerId != null) categoryOffer.setId(offerId);
                adminService.saveCategoryOffer(categoryOffer);
            }
        }
        return "redirect:/dashboard/view-categories?success";
    }

    @PutMapping("/update-status/{id}")
    public String updateCategoryStatus(@PathVariable("id") Long id) {
        adminService.toggleCategoryStatus(id);
        return "redirect:/dashboard/view-categories";
    }

    @GetMapping("/search-category")
    @ResponseBody
    public ResponseEntity<List<CategoryDto>> searchCategoryController(@RequestParam("search") String search) {
        return new ResponseEntity<>(adminService.searchCategory(search), HttpStatus.OK);
    }

    //* PRODUCT MANAGEMENT

    @GetMapping("/view-products")
    public String getProductsPage(Model model,
                                  @RequestParam(value = "pageNo", defaultValue = "0", required = false) Integer pageNo,
                                  @RequestParam(value = "pageSize", defaultValue = "10", required = false) Integer pageSize) {
        model.addAttribute("ProductPage", adminService.getAllProduct(pageNo, pageSize));
        return "Admin/page-view-products";
    }

    @GetMapping("/add-product")
    public String getAddProductForm(Model model) {
        productCommonAttributes(model);
        model.addAttribute("NewProduct", new ProductDto());
        return "Admin/page-add-product";
    }

    @PostMapping("/add-product/save")
    public String addNewProduct(@Valid @ModelAttribute("NewProduct") ProductDto productDto,
                                BindingResult result, Model model) {
        if (result.hasErrors()) {
            productCommonAttributes(model);
            model.addAttribute("NewProduct", new ProductDto());
            return "Admin/page-add-product";
        }
        Long productId = adminService.addOrUpdateProduct(productDto);
        return "redirect:/dashboard/upload-images/" + productId;
    }

    @GetMapping("/upload-images/{productId}")
    public String getAddImageForm(@PathVariable Long productId, Model model) {
        model.addAttribute("Product", adminService.getProduct(productId));
        return "Admin/page-add-images";
    }

    @PostMapping("/upload-images/save")
    public String addProductImages(@RequestParam("imageFiles") List<MultipartFile> file,
                                   @RequestParam("productId") Long productId) {
        adminService.uploadImage(file, productId);
        return "redirect:/dashboard/view-products";
    }

    @PutMapping("/delete-product")
    public String deleteProduct(@RequestParam("id") Long id) {
        adminService.toggleStatus(id);
        return "redirect:/dashboard/view-products";
    }

    @GetMapping("/edit-product")
    public String getEditProductPage(@RequestParam("id") Long id, Model model) {
        productCommonAttributes(model);
        model.addAttribute("Product", adminService.getProduct(id));
        return "Admin/page-edit-product";
    }

    @GetMapping("/update-images/{productId}")
    public String getEditImageForm(@PathVariable Long productId, Model model) {
        model.addAttribute("Product", adminService.getProduct(productId));
        return "Admin/page-edit-images";
    }

    @PutMapping("/edit-product/update")
    public String editProduct(@Valid @ModelAttribute("Product") ProductDto productDto,
                              BindingResult result, Model model) {
        if (result.hasErrors()) {
            productCommonAttributes(model);
            model.addAttribute("Product", productDto);
            return "Admin/page-edit-product";
        }
        Long productId = adminService.addOrUpdateProduct(productDto);
        return "redirect:/dashboard/update-images/" + productId;
    }

    @PutMapping("/save-and-exit")
    public String editAndExit(@Valid @ModelAttribute("Product") ProductDto productDto,
                              BindingResult result, Model model) {
        if (result.hasErrors()) {
            productCommonAttributes(model);
            model.addAttribute("Product", productDto);
            return "Admin/page-edit-product";
        }
        adminService.addOrUpdateProduct(productDto);
        return "redirect:/dashboard/view-products";
    }

    @PostMapping("/update-images/save")
    public String editProductImages(@RequestParam("imageFiles") List<MultipartFile> file,
                                    @RequestParam("productId") Long productId) {
        adminService.uploadImage(file, productId);
        return "redirect:/dashboard/update-images/" + productId;
    }

    @PostMapping(value = "/update-images/save/ajax", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public Boolean editProductImagesAjax(@RequestParam("imageFiles") List<MultipartFile> files,
                                         @RequestParam("productId") Long productId) {
        adminService.uploadImage(files, productId);
        return true;
    }

    @DeleteMapping("/delete-image")
    public String deleteImage(@RequestParam("imageId") Long imageId,
                              @RequestParam("productId") Long productId) {
        adminService.deleteImage(imageId);
        return "redirect:/dashboard/update-images/" + productId;
    }

    private void productCommonAttributes(Model model) {
        model.addAttribute("CategoryList", adminService.getAllCategory());
        model.addAttribute("brandList", adminService.getAllBrands());
    }

    @GetMapping("/search-product")
    @ResponseBody
    public ResponseEntity<List<ProductDto>> searchProductController(@RequestParam("search") String search) {
        return new ResponseEntity<>(adminService.searchProduct(search), HttpStatus.OK);
    }

    //* BRAND MANAGEMENT

    @GetMapping("/brands")
    public String viewAllBrands(Model model,
                                @RequestParam(value = "pageNo", defaultValue = "0", required = false) Integer pageNo,
                                @RequestParam(value = "pageSize", defaultValue = "12", required = false) Integer pageSize) {
        PageToListUtil<BrandDto> conversionUtil = new PageToListUtil<>();
        PaginationInfo paginationInfo = adminService.getAllBrands(pageNo, pageSize);
        Map<Long, Integer> brandCount = adminService.getProductCountByBrandPage(conversionUtil.convert(paginationInfo));

        model.addAttribute("BrandPage", paginationInfo);
        model.addAttribute("BrandCount", brandCount);
        return "Admin/page-view-brands";
    }

    @GetMapping("/add-new-brand")
    public String addNewBrand(Model model) {
        model.addAttribute("NewBrand", new BrandDto());
        return "Admin/page-add-brand";
    }

    @PostMapping("/add-new-brand/save")
    public String addNewBrandSave(@Valid @ModelAttribute("NewBrand") BrandDto brandDto,
                                  BindingResult result, MultipartFile file, Model model) {
        if(adminService.isBrandExistsByName(brandDto.getName())) {
            result.rejectValue("name", String.valueOf(HttpStatus.CONFLICT),
                    "Category with the same name exists");
        }
        if (result.hasErrors()) {
            model.addAttribute("NewBrand", new BrandDto());
            return "Admin/page-add-brand";
        }
        if (file != null) {
            BrandLogo brandLogo = adminService.saveBrandLogo(file);
            brandDto.setLogo(brandLogo.getId());
        }
        adminService.addOrUpdateBrand(brandDto);
        return "redirect:/dashboard/brands";
    }

    @GetMapping("/edit-brand/{brandId}")
    public String editBrand(@PathVariable("brandId") Long brandId, Model model) {
        if (adminService.isBrandExistsById(brandId)) {
            model.addAttribute("BrandDetails", adminService.getBrandInfo(brandId));
            return "Admin/page-edit-brand";
        }
        return "redirect:/dashboard/brands";
    }

    @PutMapping("/edit-brand/update")
    public String editBrandUpdate(@Valid @ModelAttribute("BrandDetails") BrandDto brandDto,
                                  BindingResult result, MultipartFile file, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("NewBrand", brandDto);
            return "Admin/page-edit-brand";
        }
        if (file != null) {
            BrandLogo brandLogo = adminService.saveBrandLogo(file);
            brandDto.setLogo(brandLogo.getId());
        }
        adminService.addOrUpdateBrand(brandDto);
        return "redirect:/dashboard/brands";
    }

    @PutMapping("/update-brand-status/{BrandID}")
    public String updateBrandStatus(@PathVariable("BrandID") Long brandId) {
        if (adminService.isBrandExistsById(brandId))
            adminService.toggleBrandStatus(brandId);
        return "redirect:/dashboard/brands";
    }

    //* ORDER MANAGEMENT

    @GetMapping("/view-orders")
    public String viewAllOrders(Model model,
                                @RequestParam(value = "pageNo", defaultValue = "0", required = false) Integer pageNo,
                                @RequestParam(value = "pageSize", defaultValue = "5", required = false) Integer pageSize) {

        PageToListUtil<OrderMain> conversionUtil = new PageToListUtil<>();
        PaginationInfo paginationInfo = adminService.getAllOrders(pageNo, pageSize);
        Map<Integer, PaymentDto> paymentInfo = adminService.getPaymentInfoByOrder(conversionUtil.convert(paginationInfo));

        model.addAttribute("OrderMainList", paginationInfo);
        model.addAttribute("paymentInfo", paymentInfo);
        return "Admin/page-view-orders";
    }

    @GetMapping("/order-details")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> viewOrderDetail(@RequestParam("orderID") Integer orderId) {
        if (adminService.isOrderExists(orderId)) {
            List<OrderSubDto> orderSub = adminService.findOrderSubByMain(orderId);
            List<ProductDto> orderProducts = new ArrayList<>();
            for (OrderSubDto osd : orderSub) {
                orderProducts.add(adminService.getProduct(osd.getProductId()));
            }
            Map<String, Object> response = new HashMap<>();
            response.put("DETAILS", orderSub);
            response.put("PRODUCTS", orderProducts);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/order-address")
    @ResponseBody
    public ResponseEntity<AddressDto> getOrderAddress(@RequestParam("orderID") Integer orderId) {
        if (adminService.isOrderExists(orderId)) {
            Integer addressId = adminService.getOrderById(orderId).getAddressId();
            AddressDto addressDto = adminService.getUserAddress(addressId);
            return new ResponseEntity<>(addressDto, HttpStatus.OK);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/update-order-status")
    public String updateOrderStatus(@RequestParam("OrderID") Integer orderId,
                                    @RequestParam("status") String status) {
        if (adminService.isOrderExists(orderId)) {
            OrderMainDto orderMainDto = adminService.getOrderById(orderId);
            orderMainDto.setStatus(status);
            adminService.updateOrderMain(orderMainDto);
            adminService.saveOrderHistory(orderMainDto);
        }
        return "redirect:/dashboard/view-orders";
    }

    @GetMapping("/cancel-order-request/count")
    @ResponseBody
    public Integer getCancelOrderRequestCount() {
        return adminService.getCancelOrderRequestCount();
    }

    @GetMapping("/cancel-order-request")
    public String getCancelOrderRequest(Model model,
                                        @RequestParam(value = "pageNo", defaultValue = "0", required = false) Integer pageNo,
                                        @RequestParam(value = "pageSize", defaultValue = "5", required = false) Integer pageSize) {

        PageToListUtil<OrderMain> conversionUtil = new PageToListUtil<>();
        PaginationInfo paginationInfo = adminService.getCancelRequest(pageNo, pageSize);
        Map<Integer, PaymentDto> paymentInfo = adminService.getPaymentInfoByOrder(conversionUtil.convert(paginationInfo));

        model.addAttribute("OrderMainList", paginationInfo);
        model.addAttribute("paymentInfo", paymentInfo);
        return "Admin/page-view-orders-cancel-request";
    }

    @GetMapping("/order-return-request")
    public String getOrderReturnRequest(Model model,
                                        @RequestParam(value = "pageNo", defaultValue = "0", required = false) Integer pageNo,
                                        @RequestParam(value = "pageSize", defaultValue = "5", required = false) Integer pageSize) {

        PageToListUtil<OrderMain> conversionUtil = new PageToListUtil<>();
        PaginationInfo paginationInfo = adminService.getReturnRequest(pageNo, pageSize);
        Map<Integer, PaymentDto> paymentInfo = adminService.getPaymentInfoByOrder(conversionUtil.convert(paginationInfo));

        model.addAttribute("OrderMainList", paginationInfo);
        model.addAttribute("paymentInfo", paymentInfo);
        return "Admin/page-view-orders-cancel-request";
    }

    @GetMapping("/order-details/{orderId}")
    public String getOrderDetails(@PathVariable Integer orderId, Model model) {
        if (!adminService.isOrderExists(orderId))
            return "redirect:/dashboard";

        OrderMainDto orderMainDto = adminService.getOrderById(orderId);
        List<OrderSubDto> orderSub = adminService.findOrderSubByMain(orderId);
        AddressDto addressDto = adminService.getUserAddress(orderMainDto.getAddressId());
        PaymentDto paymentDto = adminService.getPaymentInfoByOrder(orderMainDto);

        Map<Integer, ProductDto> orderProducts = new HashMap<>();
        for (OrderSubDto osd : orderSub)
            orderProducts.put(osd.getId(), adminService.getProduct(osd.getProductId()));

        model.addAttribute("OrderHistory", adminService.findOrderHistory(orderId));
        model.addAttribute("OrderMainDto", orderMainDto);
        model.addAttribute("Products", orderProducts);
        model.addAttribute("PaymentInfo", paymentDto);
        model.addAttribute("OrderSubDto", orderSub);
        model.addAttribute("Address", addressDto);

        return "Admin/page-view-orders-details";
    }

    @GetMapping("/approve-cancel")
    public String approveCancelRequest(@RequestParam("OrderId") Integer orderId) {
        if (!adminService.isOrderExists(orderId))
            return "redirect:/dashboard/cancel-order-request";

        OrderMainDto orderMainDto = adminService.approveCancelRequest(orderId);
        adminService.saveOrderHistory(orderMainDto);
        return "redirect:/dashboard/cancel-order-request";
    }

    @GetMapping("/reject-cancel")
    public String rejectCancelRequest(@RequestParam("OrderId") Integer orderId) {
        if (!adminService.isOrderExists(orderId))
            return "redirect:/dashboard/cancel-order-request";

        OrderMainDto orderMainDto = adminService.rejectCancelRequest(orderId);
        adminService.saveOrderHistory(orderMainDto);
        return "redirect:/dashboard/cancel-order-request";
    }

    //* REPORT

    @GetMapping("/view-sales-report")
    public String viewSalesReport(Model model,
                                  @RequestParam(value = "pageNo", defaultValue = "0", required = false) Integer pageNo,
                                  @RequestParam(value = "pageSize", defaultValue = "5", required = false) Integer pageSize,
                                  @RequestParam(value = "startDate", required = false) String startDate,
                                  @RequestParam(value = "endDate", required = false) String endDate,
                                  @RequestParam(value = "StatusFilter", defaultValue = "0", required = false) Integer statusFilter) {

        PaginationInfo paginationInfo;

        if (isValidDate(startDate) && isValidDate(endDate) && statusFilter == 0) {
            paginationInfo = adminService.getAllOrders(pageNo, pageSize);
        } else if (statusFilter == 0) {
            paginationInfo = adminService.filterOrderByDate(pageNo, pageSize, startDate, endDate);
        } else {
            paginationInfo = adminService.filterOrderByDateAndStatus(pageNo, pageSize, startDate, endDate, statusFilter);
        }
        model.addAttribute("StartDate", startDate);
        model.addAttribute("EndDate", endDate);
        model.addAttribute("StatusFilter", statusFilter);
        model.addAttribute("OrderMainList", paginationInfo);
        return "Admin/page-view-sales_report";
    }

    @GetMapping("/purchase-report")
    public String purchaseReport(@RequestParam(value = "pageNo", defaultValue = "0", required = false) Integer pageNo,
                                 @RequestParam(value = "pageSize", defaultValue = "5", required = false) Integer pageSize,
                                  Model model){
        model.addAttribute("Report",adminService.getPurchaseReport(pageNo,pageSize));
        return "Admin/page-view-purchase_report";
    }

    private static Boolean isValidDate(String dateString) {
        return dateString == null || Objects.equals(dateString, "") || dateString.trim().isEmpty();
    }

    @GetMapping("/sales-report/download")
    public void exportToExcel(HttpServletResponse response) {
        response.setContentType("application/octet-stream");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateTime = dateFormatter.format(new java.util.Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=users_" + currentDateTime + ".xlsx";
        response.setHeader(headerKey, headerValue);

        List<OrderMain> orderMainList = adminService.getAllOrders();
        SalesReportExcelExporter excelExporter = new SalesReportExcelExporter(orderMainList);
        excelExporter.export(response);
    }

    //* COUPON MANAGEMENT

    @GetMapping("/view-coupons")
    public String viewCoupons(Model model,
                              @RequestParam(value = "pageNo", defaultValue = "0", required = false) Integer pageNo,
                              @RequestParam(value = "pageSize", defaultValue = "5", required = false) Integer pageSize) {
        model.addAttribute("CouponList", adminService.getAllCoupons(pageNo, pageSize));
        return "Admin/page-view-coupons";
    }

    @GetMapping("/coupon-details/{couponID}")
    @ResponseBody
    public ResponseEntity<CouponDto> getCouponDetails(@PathVariable("couponID") Integer couponId) {
        if (!adminService.isCouponExist(couponId))
            return ResponseEntity.notFound().build();
        return new ResponseEntity<>(adminService.getCoupon(couponId), HttpStatus.OK);
    }

    @GetMapping("/generate-coupon-code")
    @ResponseBody
    public ResponseEntity<String> getCouponCode() {
        return new ResponseEntity<>(adminService.getCouponCode(), HttpStatus.OK);
    }

    @GetMapping("/add-coupon")
    public String addNewCoupon(Model model) {
        model.addAttribute("NewCoupon", new CouponDto());
        return "Admin/page-add-coupon";
    }

    @PostMapping("/add-coupon/save")
    public String saveNewCoupon(@Valid @ModelAttribute("NewCoupon") CouponDto couponDto, BindingResult result,
                                Model model) {
        if (result.hasErrors()) {
            model.addAttribute("NewCoupon", new CouponDto());
            return "Admin/page-add-coupon";
        }
        adminService.saveOrUpdate(couponDto);
        return "redirect:/dashboard/view-coupons";
    }

    @GetMapping("/edit-coupon/{couponId}")
    public String getEditForm(@PathVariable("couponId") Integer couponId, Model model) {
        if (adminService.isCouponExist(couponId)) {
            model.addAttribute("Coupon", adminService.getCoupon(couponId));
            return "Admin/page-edit-coupon";
        }
        return "redirect:/dashboard/view-coupons";
    }

    @PutMapping("/edit-coupon/update")
    public String updateCoupon(@Valid @ModelAttribute("Coupon") CouponDto couponDto, BindingResult result,
                               Model model) {
        if (result.hasErrors()) {
            model.addAttribute("Coupon", couponDto);
            return "Admin/page-add-coupon";
        }
        adminService.saveOrUpdate(couponDto);
        return "redirect:/dashboard/view-coupons";
    }

    @DeleteMapping("/delete-coupon")
    public String deleteCoupon(@RequestParam("couponID") Integer couponId) {
        if (adminService.isCouponExist(couponId)) {
            adminService.deleteCoupon(couponId);
        }
        return "redirect:/dashboard/view-coupons";
    }
}