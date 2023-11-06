package com.example.gamefusion.Controller;

import com.example.gamefusion.Dto.*;
import com.example.gamefusion.Services.BrandService;
import com.example.gamefusion.Services.CategoryService;
import com.example.gamefusion.Services.ProductService;
import com.example.gamefusion.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;

@Controller
@RequestMapping("/user")
public class UserHomeController {
    private final ProductService productService;
    private final UserService userService;
    private final CategoryService categoryService;
    private final BrandService brandService;
    @Autowired
    public UserHomeController(ProductService productService, UserService userService,
                              CategoryService categoryService, BrandService brandService) {
        this.productService = productService;
        this.userService = userService;
        this.categoryService = categoryService;
        this.brandService = brandService;
    }


    @GetMapping("/home")
    public String home(Model model,
                       @RequestParam(value = "pageNo", defaultValue = "0", required = false) Integer pageNo,
                       @RequestParam(value = "pageSize", defaultValue = "5", required = false) Integer pageSize) {
        PaginationInfo info = productService.getAllActiveProducts(pageNo,pageSize);
        model.addAttribute("ProductPage",info);
        model.addAttribute("CategoryList",categoryService.getAll());
        return "User/index-4";
    }

    @GetMapping("/view-product/{id}")
    public String getSingleProduct(@PathVariable("id") Long id, Model model) {

        ProductDto productDto = productService.getProductById(id);
        CategoryDto categoryDto = categoryService.findById(productDto.getCategoryId());
        BrandDto brandDto = brandService.findById(productDto.getBrandId());
        model.addAttribute("CategoryDetails", categoryDto);
        model.addAttribute("BrandDetails", brandDto);
        model.addAttribute("ProductDetails",productDto);
        return "User/shop-product";
    }

    @GetMapping("/category-filter/{id}")
    public String getProductOfaSingleCategory(@PathVariable("id") Long categoryId, Model model,
                                              @RequestParam(value = "pageNo", defaultValue = "0", required = false ) Integer pageNo,
                                              @RequestParam(value = "pageSize", defaultValue = "5", required = false) Integer pageSize ) {
        PaginationInfo paginationInfo = null;
        if (categoryService.isCategoryExist(categoryId) && categoryService.isCategoryActive(categoryId)) {
            CategoryDto categoryDto = categoryService.findById(categoryId);
            paginationInfo = productService.getAllActiveProductsFromCategory(categoryDto, pageNo, pageSize);
            model.addAttribute("CategoryList",categoryService.getAll());
            model.addAttribute("ID",categoryId);
        }
        model.addAttribute( "ProductDetails", paginationInfo);
        return "User/page-category-products";
    }

    @GetMapping("/my-profile")
    public ResponseEntity<UserDto> showUserProfile(Principal principal) {
        UserDto user = userService.findByUsername(principal.getName());
        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/edit-profile/{id}")
    public String showEditProfileForm(@PathVariable("id") Integer id, Model model){
        if(userService.isExistsById(id)) {
            model.addAttribute("UserDetails", userService.findById(id));
        }
        return "User/";
    }

    @GetMapping("/my-orders/{id}")
    public String showUserOrders(@PathVariable("id") String id) {
        return  "";
    }

    @PutMapping("/cancel-order/{id}")
    public String cancelOrders(@PathVariable("id") String id) {
        return  "";
    }

    @GetMapping("/my-address/{id}")
    public String showUserAddress(@PathVariable("id") String id) {
        return "";
    }

    @GetMapping("/add-address")
    public String showNewAddressForm() {
        return "";
    }

    @PostMapping("/add-address/save")
    public String saveNewAddress() {
        return "";
    }

    @GetMapping("/edit-address/{addressId}")
    public String showEditAddressForm(@PathVariable("addressId") String addressId) {

        return  "";
    }

    @PutMapping("/edit-address/update")
    public String updateAddress() {
        return "";
    }

    @GetMapping("/forget-password/{id}")
    public String showForgetPasswordForm(@PathVariable("id") String id) {
        return "";
    }
}
