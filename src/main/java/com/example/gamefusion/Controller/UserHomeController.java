package com.example.gamefusion.Controller;

import com.example.gamefusion.Dto.*;
import com.example.gamefusion.Services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/user")
public class UserHomeController {
    private final UserService userService;
    private final ProductService productService;
    private final CategoryService categoryService;
    private final ImagesService imagesService;
    private final BrandService brandService;
    @Autowired
    public UserHomeController(UserService userService, ProductService productService,
                              CategoryService categoryService, ImagesService imagesService,
                              BrandService brandService) {
        this.userService = userService;
        this.productService = productService;
        this.categoryService = categoryService;
        this.imagesService = imagesService;
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
        List<ImagesDto> images = imagesService.findImageByProduct(productDto.getId());

        model.addAttribute("CategoryDetails", categoryDto);
        model.addAttribute("BrandDetails", brandDto);
        model.addAttribute("ProductDetails",productDto);
        return "User/shop-product";
    }
}
