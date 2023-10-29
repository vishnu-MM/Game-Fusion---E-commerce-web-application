package com.example.gamefusion.Controller;

import com.example.gamefusion.Dto.CategoryDto;
import com.example.gamefusion.Services.AdminService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@RequestMapping("/dashboard")
public class AdminController {

    //Logger log = LoggerFactory.getLogger(AdminController.class);
    private final AdminService adminService;

    @Autowired
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("")
    public String home() {

        return "Admin/index";
    }

    //? USER MANAGEMENT

    @GetMapping("/view-users")
    public String getListOfAllUsers(Model model,
                                    @RequestParam(value = "pageNo", defaultValue = "0", required = false) Integer pageNo,
                                    @RequestParam(value = "pageSize", defaultValue = "5", required = false) Integer pageSize) {
        model.addAttribute("UserList", adminService.getAllUsers(pageNo, pageSize));
        return "Admin/page-users-list";
    }

    @PutMapping("/block-unblock-user/{id}")
    public String UnBlockUser(@PathVariable("id") Integer id) {
        if (!adminService.isUserExists(id))
            return "redirect:/dashboard/view-users?error";
        if (!adminService.isUserBlocked(id))
            adminService.unBlockUser(id);
        else if (adminService.isUserBlocked(id))
            adminService.blockUser(id);
        return "redirect:/dashboard/view-users?success";
    }

    @PutMapping("/block-user/{id}")
    public String blockUser(@PathVariable("id") Integer id) {
        if (!adminService.isUserExists(id))
            return "redirect:/dashboard/view-users?error";
        if (adminService.isUserBlocked(id))
            adminService.blockUser(id);
        return "redirect:/dashboard/view-users?success";
    }

    //? CATEGORY MANAGEMENT

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
        if (result.hasErrors()) {
            model.addAttribute("parentList", adminService.getAllCategory());
            model.addAttribute("CategoryDetails", adminService.getCategoryInfo(newCategory.getId()));
            return "Admin/page-add-category";
        }
        adminService.updateCategory(newCategory);
        return "redirect:/dashboard/view-categories?success";
    }

    @PutMapping("/update-status/{id}")
    public String updateCategoryStatus(@PathVariable("id") Long id) {
        adminService.toggleCategoryStatus(id);
        return "redirect:/dashboard/view-categories";
    }

    //? PRODUCT MANAGEMENT

    @GetMapping("/view-products")
    public String getProductsPage() {
        return "Admin/page-products-grid";
    }

    @GetMapping("/add-product")
    public String getAddProductForm() {
        return "";
    }

    @GetMapping("/edit-product")
    public String getEditProductPage() {
        return "";
    }

    @PutMapping("/delete-product")
    public String deleteProduct() {
        return "";
    }

    @PostMapping("/add-product/save")
    public String addNewProduct() {
        return "";
    }

    @PutMapping("/edit-product/update")
    public String editProduct() {
        return "";
    }
}
