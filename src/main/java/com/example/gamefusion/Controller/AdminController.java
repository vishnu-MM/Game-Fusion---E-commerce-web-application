package com.example.gamefusion.Controller;

import com.example.gamefusion.Dto.CategoryDto;
import com.example.gamefusion.Dto.ProductDto;
import com.example.gamefusion.Services.AdminService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@RequestMapping("/dashboard")
public class AdminController {

    Logger log = LoggerFactory.getLogger(AdminController.class);
    private final AdminService adminService;

    @Autowired
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("")
    public String home() {
        return "Admin/index";
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

        model.addAttribute("CategoryList", adminService.getAllCategory(pageNo, pageSize));
        return "Admin/page-categories";
    }

    @GetMapping("/add-category")
    public String getAddCategoryForm(Model model, @ModelAttribute("CategoryDetails") CategoryDto categoryDto) {
        model.addAttribute("NewCategory", new CategoryDto());
        model.addAttribute("parentList", adminService.getAllCategory());
        return "Admin/page-add-category";
    }

    @PostMapping("/add-category/save")
    public String addNewCategory(@Valid @ModelAttribute("NewCategory") CategoryDto newCategory, BindingResult result) {
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
            System.out.println(result);
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
            System.out.println(result);
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
        log.info(adminService.uploadImage(file,productId).toString());
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
}
