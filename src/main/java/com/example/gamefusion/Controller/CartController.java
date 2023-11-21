package com.example.gamefusion.Controller;

import com.example.gamefusion.Configuration.UtilityClasses.EntityDtoConversionUtil;
import com.example.gamefusion.Dto.*;
import com.example.gamefusion.Entity.Cart;
import com.example.gamefusion.Services.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
public class CartController {
    private final UserService userService;
    private final CartService cartService;
    private final ProductService productService;
    private final EntityDtoConversionUtil conversionUtil;
    @Autowired
    public CartController(UserService userService, CartService cartService,
                          ProductService productService, EntityDtoConversionUtil conversionUtil) {
        this.userService = userService;
        this.cartService = cartService;
        this.productService = productService;
        this.conversionUtil = conversionUtil;
    }

    @GetMapping("/cart-products")
    public String getProductsFromCart(Principal principal, Model model) {
        UserDto userDto = userService.findByUsername(principal.getName());
        List<Cart> cart = cartService.findByUser(userDto);
        List<CartDto> cartDto = cart.stream().map(conversionUtil::entityToDto).toList();
        model.addAttribute("TotalAmount",cartService.totalAmount(cartDto));
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
}