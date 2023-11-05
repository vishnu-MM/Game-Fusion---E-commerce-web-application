package com.example.gamefusion.Controller;

import com.example.gamefusion.Dto.BrandDto;
import com.example.gamefusion.Dto.CategoryDto;
import com.example.gamefusion.Dto.PaginationInfo;
import com.example.gamefusion.Dto.ProductDto;
import com.example.gamefusion.Services.BrandService;
import com.example.gamefusion.Services.CategoryService;
import com.example.gamefusion.Services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/user")
public class UserHomeController {
    private final ProductService productService;
    private final CategoryService categoryService;
    private final BrandService brandService;
    @Autowired
    public UserHomeController(ProductService productService, CategoryService categoryService,
                              BrandService brandService) {
        this.productService = productService;
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
        if (categoryService.isCategoryExist(categoryId) && categoryService.isCategoryActive(categoryId)) {
            CategoryDto categoryDto = categoryService.findById(categoryId);
            model.addAttribute(
                    "ProdutDetails",
                    productService.getAllActiveProductsFromCategory(categoryDto, pageNo, pageSize)
            );
        }
        return "User/index-4";
    }
}
