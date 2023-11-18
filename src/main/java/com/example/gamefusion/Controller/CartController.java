package com.example.gamefusion.Controller;

import com.example.gamefusion.Configuration.UtilityClasses.EntityDtoConversionUtil;
import com.example.gamefusion.Configuration.UtilityClasses.OrderStatusUtil;
import com.example.gamefusion.Configuration.UtilityClasses.PaymentMethodUtil;
import com.example.gamefusion.Dto.*;
import com.example.gamefusion.Entity.Cart;
import com.example.gamefusion.Entity.OrderSub;
import com.example.gamefusion.Services.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

@Controller
public class CartController {
    private final UserService userService;
    private final CartService cartService;
    private final ProductService productService;
    private final AddressService addressService;
    private final OrderMainService orderMainService;
    private final OrderSubService orderSubService;
    private final EntityDtoConversionUtil conversionUtil;
    @Autowired
    public CartController(UserService userService, CartService cartService,
                          ProductService productService, AddressService addressService,
                          OrderMainService orderMainService, OrderSubService orderSubService,
                          EntityDtoConversionUtil conversionUtil) {
        this.userService = userService;
        this.cartService = cartService;
        this.productService = productService;
        this.addressService = addressService;
        this.orderMainService = orderMainService;
        this.orderSubService = orderSubService;
        this.conversionUtil = conversionUtil;
    }

    @GetMapping("/cart-products")
    public String getProductsFromCart(Principal principal, Model model) {
        UserDto userDto = userService.findByUsername(principal.getName());
        List<Cart> cart = cartService.findByUser(userDto);
        model.addAttribute("TotalAmount",cartService.totalAmount(cart));
        model.addAttribute("ProductDetails", cart);
        return "User/shop-cart";
    }

    @PostMapping("/add-to-cart")
    public String addToCart(@RequestParam("ProductId") Long productId,
                            @RequestParam(value = "Quantity",defaultValue = "1", required = false) Integer qty,
                            Principal principal, HttpServletRequest request){

        UserDto userDto = userService.findByUsername(principal.getName());
        ProductDto productDto = productService.getProductById(productId);
        if (qty > productDto.getQty() ) {
            String redirectUrl = request.getHeader("Referer");
            String path = redirectUrl.substring(redirectUrl.lastIndexOf("0")+1);
            return "redirect:"+path;
        }
        if (cartService.isExistsByProduct(userDto, productDto)) {
            CartDto cartDto = cartService.findByUserAndProduct(userDto, productDto);
            Integer newQty = cartDto.getQty();
            cartDto.setQty( ++newQty );
            cartService.addToCart(cartDto);
        }
        else {
            CartDto cart = new CartDto();
            cart.setQty(qty);
            cart.setProductID(productId);
            cart.setUserID(userDto.getId());
            cartService.addToCart(cart);
        }
        return "redirect:/cart-products";
    }

    @PutMapping("/updateQuantity")
    public String updateQuantity(@RequestParam("CartID") Integer cartId,
                                 @RequestParam("qty") Integer qty) {
        CartDto cartDto = cartService.findById(cartId);
        cartDto.setQty(qty);
        cartService.addToCart(cartDto);
        return "redirect:/cart-products";
    }

    @DeleteMapping("/delete-product/{CartId}")
    public String deleteFromCart(@PathVariable("CartId") Integer cartId) {
        if (cartService.isExistsById(cartId)) {
            cartService.deleteById(cartId);
        }
        return "redirect:/cart-products";
    }

    @DeleteMapping("/clear-cart")
    public String clearCart(Principal principal) {
        UserDto userDto = userService.findByUsername(principal.getName());
        cartService.deleteByUser(userDto);
        return "redirect:/cart-products";
    }

    @GetMapping("/checkout-page")
    public String getCheckOutPage(Model model, Principal principal) {
        AddressDto addressDto = new AddressDto();
        UserDto user = userService.findByUsername(principal.getName());
        List<AddressDto> addressDtoList = addressService.findByUser(user.getId(),true);
        List<Cart> cart = cartService.findByUser(user);

        model.addAttribute("User", user);
        model.addAttribute("NewAddress", addressDto);
        model.addAttribute("ProductDetails", cart);
        model.addAttribute("AddressList", addressDtoList);
        model.addAttribute("TotalAmount",cartService.totalAmount(cart));
        return "User/shop-checkout";
    }

    @PostMapping("/add-new-address")
    public String saveNewAddress(@ModelAttribute("NewAddress") @Valid AddressDto newAddress, BindingResult result,
                                 Principal principal, Model model) {
        if (result.hasErrors()) {
            return getCheckOutPage(model,principal);
        }
        UserDto user = userService.findByUsername(principal.getName());
        newAddress.setUserId(user.getId());
        addressService.save(newAddress);
        return "redirect:/checkout-page";
    }
}