package com.example.gamefusion.Controller;

import com.example.gamefusion.Configuration.UtilityClasses.PageToListUtil;
import com.example.gamefusion.Dto.*;
import com.example.gamefusion.Services.*;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.sql.Date;
import java.util.List;
import java.util.Objects;

@Controller
@RequestMapping("/user")
public class UserHomeController {
    private final OTPService otpService;
    private final UserService userService;
    private final BrandService brandService;
    private final WalletService walletService;
    private final AddressService addressService;
    private final ProductService productService;
    private final CategoryService categoryService;
    private final ProductReviewService productReviewService;
    @Value("${referral_url}")
    private String REFERRAL_URL;
    @Autowired
    public UserHomeController(OTPService otpService, ProductService productService, UserService userService,
                              CategoryService categoryService, BrandService brandService,
                              AddressService addressService, WalletService walletService,
                              ProductReviewService productReviewService) {
        this.otpService = otpService;
        this.userService = userService;
        this.brandService = brandService;
        this.walletService = walletService;
        this.addressService = addressService;
        this.productService = productService;
        this.categoryService = categoryService;
        this.productReviewService = productReviewService;
    }


    @GetMapping("/home")
    public String home(Model model,
                       @RequestParam(value = "pageNo", defaultValue = "0", required = false) Integer pageNo,
                       @RequestParam(value = "pageSize", defaultValue = "5", required = false) Integer pageSize,
                       Principal principal) {
        PaginationInfo info = productService.getAllActiveProducts(pageNo,pageSize);
        model.addAttribute("ProductPage",info);
        model.addAttribute("CategoryList",categoryService.getAll());
        return "User/index-home";
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
        String username = principal.getName();
        UserDto user = userService.findByUsername(username);
        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/edit-profile")
    public String showEditProfileForm(Principal principal, Model model){
        String username = principal.getName();
        if( username != null  && userService.isExistsByUsername(username)) {
            model.addAttribute("UserDetails", userService.findByUsername(username));
        }
        return "User/page-edit-profile";
    }

    @PutMapping("/edit-profile/update")
    public String updateProfile(@Valid @ModelAttribute("UserDetails") UserDto userDto,
                                BindingResult result, Model model,Authentication authentication) {
        String oldUsername = userService.findById(userDto.getId()).getUsername();
        String newUsername = userDto.getUsername();
        if (userService.isExistsByUsername(newUsername) && !(oldUsername.equals(newUsername))) {
            result.rejectValue("username", String.valueOf(HttpStatus.CONFLICT),"User with this email is already exist");
        }
        if (result.hasErrors()) {
            model.addAttribute("UserDetails",userDto);
            return "User/page-edit-profile";
        }
//        User user = userService.update(userDto);
//
//        authentication = new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword(), authentication.getAuthorities());
//        SecurityContextHolder.getContext().setAuthentication(authentication);
        return "redirect:/user/home";
    }

    @GetMapping("/username-exists")
    @ResponseBody
    public Boolean isUsernameExists( @RequestParam("Email") String email,Principal principal ) {
        return userService.isExistsByUsername(email) && (!Objects.equals(principal.getName(), email));
    }

    @GetMapping("/sent-otp")
    @ResponseBody
    public ResponseEntity<Void> sentOTPtoUser(@RequestParam("Email") String email, @RequestParam("Name") String name) {
        otpService.sendOTP(email, name);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/otp-validation/verify")
    public ResponseEntity<String> validateOTP(@RequestParam("OTP") String otp,
                                              @RequestParam("Email") String email) {
        String msg = otpService.verifyOTP(email, otp);
        if (msg.equals("SUCCESS")) return new ResponseEntity<>(msg,HttpStatus.OK);
        return new ResponseEntity<>(msg,HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/my-address")
    public String showUserAddress(Principal principal, Model model) {
        UserDto user = userService.findByUsername(principal.getName());
        List<AddressDto> addressDtoList = addressService.findByUser(user.getId(), true);
        model.addAttribute("AddressList", addressDtoList);
        model.addAttribute("User", user);
        return "User/page-user-address";
    }

    @GetMapping("/add-address")
    public String showNewAddressForm(Model model, Principal principal) {
        UserDto user = userService.findByUsername(principal.getName());
        model.addAttribute("NewAddress",new AddressDto());
        model.addAttribute("User",user);
        return "User/page-add-address";
    }

    @PostMapping("/add-address/save")
    public String saveNewAddress(@ModelAttribute("NewAddress") @Valid AddressDto newAddress, BindingResult result,
                                 Principal principal, Model model) {
        if (result.hasErrors()) {
            UserDto user = userService.findByUsername(principal.getName());
            model.addAttribute("User",user);
            return "User/page-add-address";
        }
        UserDto user = userService.findByUsername(principal.getName());
        newAddress.setUserId(user.getId());
        addressService.save(newAddress);
        return "redirect:/user/my-address";
    }


    @DeleteMapping("/delete-address")
    public String deleteAddress(@RequestParam("addressId") Integer addressId) {
        if (!addressService.isExistById(addressId))
            return "redirect:/user/my-address?failed";
        addressService.deleteById(addressId);
        return "redirect:/user/my-address?success";
    }

    @GetMapping("/edit-address/{addressId}")
    public String showEditAddressForm(@PathVariable("addressId") Integer addressId,
                                      Principal principal, Model model) {
        AddressDto addressDto = addressService.findById(addressId);
        UserDto user = userService.findByUsername(principal.getName());
        model.addAttribute("Address",addressDto);
        model.addAttribute("User",user);
        return  "User/page-edit-address";
    }

    @PutMapping("/edit-address/update")
    public String updateAddress( @Valid @ModelAttribute("Address") AddressDto newAddress,
                                BindingResult result, Principal principal, Model model ) {
        if (result.hasErrors()) {
            model.addAttribute("Address",newAddress);
            model.addAttribute("User",userService.findByUsername(principal.getName()));
            return "User/page-edit-address";
        }
        addressService.save(newAddress);
        return "redirect:/user/my-address";
    }

    @GetMapping("/my-wallet")
    public String getMyWallet(Model model, Principal principal) {
        double balance = 0;
        UserDto userDto = userService.findByUsername(principal.getName());
        List<WalletDto> walletDtoList = new ArrayList<>();
        if(walletService.existByUser(userDto.getId())) {
            walletDtoList =  walletService.findByUser(userDto);
            balance = walletService.getWalletBalance(userDto);
        }
        model.addAttribute("Balance",balance);
        model.addAttribute("TransactionHistory",walletDtoList);
        return "User/page-my-wallet";
    }

    @GetMapping("/referral-link")
    @ResponseBody
    public String getReferralLink(Principal principal) {
        UserDto userDto = userService.findByUsername(principal.getName());
        return REFERRAL_URL+"?referralID="+userDto.getReferralCode();
    }

    // Products & Reviews

    @GetMapping("/view-product/{id}")
    public String getSingleProduct(@PathVariable("id") Long id, Model model) {

        ProductDto productDto = productService.getProductById(id);
        CategoryDto categoryDto = categoryService.findById(productDto.getCategoryId());
        BrandDto brandDto = brandService.findById(productDto.getBrandId());

        model.addAttribute("BrandDetails", brandDto);
        model.addAttribute("ProductDetails",productDto);
        model.addAttribute("CategoryDetails", categoryDto);
        model.addAttribute("Review",productReviewService.findByProduct(productDto));
        return "User/page-shop-product";
    }

    @PostMapping("/add-product-review")
    @ResponseBody
    public ResponseEntity<ProductReviewDto> addProductReview(Principal principal, @RequestParam("ProductId") Long productId,
                                                             @RequestParam("Review") String review, @RequestParam("Rating") Double rating ) {
        UserDto userDto = userService.findByUsername(principal.getName());

        if (!productService.isProductExists(productId) || !productService.isProductActive(productId))
            return ResponseEntity.badRequest().build();

        DecimalFormat format = new DecimalFormat("0.0");
        ProductReviewDto productReview = new ProductReviewDto();
        productReview.setReview(review);
        productReview.setProductId(productId);
        productReview.setUserId(userDto.getId());
        productReview.setDate(Date.valueOf(LocalDate.now()));
        productReview.setRating(Double.valueOf(format.format(rating)));
        productReview = productReviewService.save(productReview);
        return new ResponseEntity<>(productReview, HttpStatus.OK);
    }

    @GetMapping("/category-list")
    @ResponseBody
    public ResponseEntity<List<CategoryDto>> getAllCategories(@RequestParam(name = "search", required = false) String search) {
        List<CategoryDto> categoryDtoList;
        if (search == null) categoryDtoList = categoryService.getAllAvailable();
        else categoryDtoList = categoryService.searchAvailable(search);
        return new ResponseEntity<>(categoryDtoList,HttpStatus.OK);

    }
}