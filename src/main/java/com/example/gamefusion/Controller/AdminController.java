package com.example.gamefusion.Controller;

import com.example.gamefusion.Dto.*;
import com.example.gamefusion.Entity.BrandLogo;
import com.example.gamefusion.Services.AdminService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
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
    public ResponseEntity<Map<String,Integer>> getGraphByDay(){
        Map<String, Integer> response = adminService.filterGraphBasedOnDate("MONTH");
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @GetMapping("/fetchData/week")
    @ResponseBody
    public ResponseEntity<Map<String,Integer>> getGraphByWeek(){
        Map<String, Integer> response = adminService.filterGraphBasedOnDate("WEEK");
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @GetMapping("/fetchData/year")
    @ResponseBody
    public ResponseEntity<Map<String,Integer>> getGraphByYear(){
        Map<String, Integer> response = adminService.filterGraphBasedOnDate("YEAR");
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    //* USER MANAGEMENT

    @GetMapping("/view-users")
    public String getListOfAllUsers(Model model,
                                    @RequestParam(value = "error", defaultValue = "false", required = false) Boolean error,
                                    @RequestParam(value = "pageNo", defaultValue = "0", required = false) Integer pageNo,
                                    @RequestParam(value = "pageSize", defaultValue = "5", required = false) Integer pageSize) {
        model.addAttribute("Error",error);
        model.addAttribute("UserList", adminService.getAllUsers(pageNo, pageSize));
        return "Admin/page-users-list";
    }

    @PutMapping("/block-unblock-user/{id}")
    public String UnBlockUser(@PathVariable("id") Integer id) {
        if (!adminService.isUserExists(id))
            return "redirect:/dashboard/view-users?error="+true;
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
        return "Admin/page-categories";
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
    public String addNewCategory(@Valid @ModelAttribute("NewCategory") CategoryDto newCategory, BindingResult result) {
        if(adminService.isCategoryNameExist(newCategory.getName())){
            result.rejectValue("name", String.valueOf(HttpStatus.CONFLICT),"Category with the same name exists");
        }
        if (result.hasErrors()) {
            return "Admin/page-add-category";
        }
        adminService.addNewCategory(newCategory);
        return "redirect:/dashboard/view-categories?success";
    }

    @GetMapping("/edit-category/{id}")
    public String getEditCategoryForm(@PathVariable("id") Long id, Model model) {
        model.addAttribute("parentList", adminService.getAllCategory());
        model.addAttribute("CategoryDetails", adminService.getCategoryInfo(id));
        return "Admin/page-edit-category";
    }

    @PutMapping("/edit-category/update")
    public String editCategory(@Valid @ModelAttribute("NewCategory") CategoryDto newCategory,
                               BindingResult result, Model model) {
        boolean isSuccess;
        if (result.hasErrors()) {
            model.addAttribute("parentList", adminService.getAllCategory());
            model.addAttribute("CategoryDetails", adminService.getCategoryInfo(newCategory.getId()));
            isSuccess = false;
        } else {
            adminService.updateCategory(newCategory);
            isSuccess = true;
        }
        model.addAttribute("success",isSuccess);
        return "Admin/page-edit-category";
    }

    @PutMapping("/update-status/{id}")
    public String updateCategoryStatus(@PathVariable("id") Long id) {
        adminService.toggleCategoryStatus(id);
        return "redirect:/dashboard/view-categories";
    }

    //* PRODUCT MANAGEMENT

    @GetMapping("/view-products")
    public String getProductsPage(Model model,
                                  @RequestParam(value = "pageNo", defaultValue = "0", required = false) Integer pageNo,
                                  @RequestParam(value = "pageSize", defaultValue = "10", required = false) Integer pageSize) {
        model.addAttribute("ProductPage", adminService.getAllProduct(pageNo,pageSize));
        return "Admin/page-products-grid";
    }

    @GetMapping("/add-product")
    public String getAddProductForm(Model model) {
        productCommonAttributes(model);
        model.addAttribute("NewProduct",new ProductDto());
        return "Admin/page-add-product";
    }

    @PostMapping("/add-product/save")
    public String addNewProduct(@Valid @ModelAttribute("NewProduct") ProductDto productDto,
                                BindingResult result, Model model) {
        if (result.hasErrors()) {
            productCommonAttributes(model);
            model.addAttribute("NewProduct",new ProductDto());
            return "Admin/page-add-product";
        }
        Long productId = adminService.addOrUpdateProduct(productDto);
        return "redirect:/dashboard/upload-images/" + productId;
    }

    @GetMapping("/upload-images/{productId}")
    public String getAddImageForm(@PathVariable Long productId, Model model) {
        model.addAttribute("productId",productId);
        return "Admin/page-add-images";
    }

    @PostMapping("/upload-images/save")
    public String addProductImages(@RequestParam("imageFiles") List<MultipartFile> file,
                                   @RequestParam("productId") Long productId ) {
        adminService.uploadImage(file,productId);
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
        model.addAttribute("Product",adminService.getProduct(productId));
        return "Admin/page-edit-images";
    }

    @PutMapping("/edit-product/update")
    public String editProduct(@Valid @ModelAttribute("Product") ProductDto productDto,
                              BindingResult result, Model model) {
        if (result.hasErrors()) {
            productCommonAttributes(model);
            model.addAttribute("Product",productDto);
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
            model.addAttribute("Product",productDto);
            return "Admin/page-edit-product";
        }
        adminService.addOrUpdateProduct(productDto);
        return "redirect:/dashboard/view-products";
    }

    @PostMapping("/update-images/save")
    public String editProductImages(@RequestParam("imageFiles") List<MultipartFile> file,
                                   @RequestParam("productId") Long productId ) {
        adminService.uploadImage(file,productId);
        return "redirect:/dashboard/update-images/" + productId;
    }

    @DeleteMapping("/delete-image")
    public String deleteImage(@RequestParam("imageId") Long imageId,
                              @RequestParam("productId") Long productId ) {
        adminService.deleteImage(imageId);
        return "redirect:/dashboard/update-images/"+productId;
    }

    private void productCommonAttributes(Model model) {
        model.addAttribute("CategoryList", adminService.getAllCategory());
        model.addAttribute("brandList", adminService.getAllBrands());
    }

    //* BRAND MANAGEMENT

    @GetMapping("/brands")
    public String viewAllBrands(Model model,
                                @RequestParam(value = "pageNo", defaultValue = "0", required = false) Integer pageNo,
                                @RequestParam(value = "pageSize", defaultValue = "10", required = false) Integer pageSize) {
        PaginationInfo paginationInfo = adminService.getAllBrands(pageNo, pageSize);
        Map<Long,Integer> brandCount = adminService.getProductCountByBrandPage((List<BrandDto>) paginationInfo.getContents());

        model.addAttribute("BrandPage",paginationInfo);
        model.addAttribute("BrandCount",brandCount);
        return "Admin/page-brands";
    }

//    @GetMapping("/brand/{BrandID}")
//    public String viewBrand(@PathVariable("BrandID") String brandId) {
//        return "";
//    }

    @GetMapping("/add-new-brand")
    public String addNewBrand(Model model) {
        model.addAttribute("NewBrand",new BrandDto());
        return "Admin/page-add-brand";
    }
    
    @PostMapping("/add-new-brand/save")
    public String addNewBrandSave(@ModelAttribute("NewBrand") BrandDto brandDto,BindingResult result,
                                  MultipartFile file,Model model) {
        if (result.hasErrors()) {
            model.addAttribute("NewBrand",new BrandDto());
            return "Admin/page-add-brand";
        }
        if (file != null) {
            BrandLogo brandLogo = adminService.saveBrandLogo(file);
            brandDto.setLogo(brandLogo.getId());
        }
        adminService.addOrUpdateBrand(brandDto);
        return "redirect:/dashboard/brands";
    }

//    @GetMapping("/edit-brand/{BrandID}")
//    public String editBrand(@PathVariable("BrandID") String brandId) {
//        return "";
//    }
//
//    @PutMapping("/edit-brand/update")
//    public String editBrandUpdate() {
//        return "";
//    }

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
        PaginationInfo paginationInfo = adminService.getAllOrders(pageNo, pageSize);
        model.addAttribute("OrderMainList", paginationInfo);
        return "Admin/page-orders-1";
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
            Map<String,Object> response = new HashMap<>();
            response.put("DETAILS",orderSub);
            response.put("PRODUCTS",orderProducts);
            return new ResponseEntity<>(response,HttpStatus.OK);
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
            return new ResponseEntity<>(addressDto,HttpStatus.OK);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/update-order-status")
    public String updateOrderStatus(@RequestParam("OrderID") Integer orderId,
                                    @RequestParam("status") String status){
        if (adminService.isOrderExists(orderId) ) {
            OrderMainDto orderMainDto = adminService.getOrderById(orderId);
            orderMainDto.setStatus(status);
            adminService.updateOrderMain(orderMainDto);
        }
        return "redirect:/dashboard/view-orders";
    }

    //* SALES REPORT

    @GetMapping("/view-sales-report")
    public String viewSalesReport(Model model,
                                @RequestParam(value = "pageNo", defaultValue = "0", required = false) Integer pageNo,
                                @RequestParam(value = "pageSize", defaultValue = "5", required = false) Integer pageSize,
                                @RequestParam(value = "startDate", required = false) String startDate,
                                @RequestParam(value = "endDate", required = false) String endDate,
                                @RequestParam(value = "StatusFilter", defaultValue = "0", required = false) Integer statusFilter) {

            PaginationInfo paginationInfo;

            if ( isValidDate(startDate) && isValidDate(endDate) && statusFilter == 0) {
                paginationInfo = adminService.getAllOrders(pageNo, pageSize);
            }
            else if (statusFilter == 0 ) {
                paginationInfo = adminService.filterOrderByDate(pageNo, pageSize, startDate, endDate);
            }
            else {
                paginationInfo = adminService.filterOrderByDateAndStatus(pageNo,pageSize,startDate,endDate,statusFilter);
            }
            model.addAttribute("StartDate", startDate);
            model.addAttribute("EndDate", endDate);
            model.addAttribute("StatusFilter", statusFilter);
            model.addAttribute("OrderMainList", paginationInfo);
            return "Admin/page-sales-report";
        }

        private static Boolean isValidDate(String dateString) {
            return dateString == null || Objects.equals(dateString, "") || dateString.trim().isEmpty();
        }
}
