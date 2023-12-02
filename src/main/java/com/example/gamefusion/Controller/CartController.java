package com.example.gamefusion.Controller;

import com.example.gamefusion.Configuration.UtilityClasses.EntityDtoConversionUtil;
import com.example.gamefusion.Dto.CartDto;
import com.example.gamefusion.Dto.ProductDto;
import com.example.gamefusion.Dto.UserDto;
import com.example.gamefusion.Dto.WishListDto;
import com.example.gamefusion.Entity.Cart;
import com.example.gamefusion.Services.CartService;
import com.example.gamefusion.Services.ProductService;
import com.example.gamefusion.Services.UserService;
import com.example.gamefusion.Services.WishListService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
public class CartController {
    private final UserService userService;
    private final CartService cartService;
    private final ProductService productService;
    private final WishListService wishListService;
    private final EntityDtoConversionUtil conversionUtil;
    @Autowired
    public CartController(UserService userService, CartService cartService,
                          ProductService productService, WishListService wishListService,
                          EntityDtoConversionUtil conversionUtil) {
        this.userService = userService;
        this.cartService = cartService;
        this.conversionUtil = conversionUtil;
        this.productService = productService;
        this.wishListService = wishListService;
    }

    @GetMapping("/cart-products")
    public String getProductsFromCart(Principal principal, Model model) {
        UserDto userDto = userService.findByUsername(principal.getName());
        List<Cart> cart = cartService.findByUser(userDto);
        List<CartDto> cartDto = cart.stream().map(conversionUtil::entityToDto).toList();

        model.addAttribute("ProductDetails", cart);
        model.addAttribute("TotalAmount",cartService.totalAmount(cartDto));
        model.addAttribute("OfferApplied", cartService.getOfferCount(cartDto));
        return "User/shop-cart";
    }

    @GetMapping("/add-to-cart")
    @ResponseBody
    public  Boolean addToCartWithQty(@RequestParam("ProductId") Long productId,
                                     @RequestParam(value = "Quantity",defaultValue = "1", required = false) Integer qty,
                                     Principal principal, HttpServletRequest request) {
        UserDto userDto = userService.findByUsername(principal.getName());
        ProductDto productDto = productService.getProductById(productId);
        if (qty > productDto.getQty() )
            return false;

        CartDto cartDto;
        if (cartService.isExistsByProduct(userDto, productDto)) {
            cartDto = cartService.findByUserAndProduct(userDto, productDto);
            Integer newQty = cartDto.getQty();
            cartDto.setQty( ++newQty );
        }
        else {
            cartDto = new CartDto();
            cartDto.setQty(qty);
            cartDto.setProductID(productId);
            cartDto.setUserID(userDto.getId());
        }
        cartService.addToCart(cartDto);
        return true;
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

    //? WISH LIST

    @PostMapping("/add-to-wishlist")
    @ResponseBody
    public  Boolean addToWishList(@RequestParam("ProductId") Long productId,Principal principal) {
        UserDto userDto = userService.findByUsername(principal.getName());
        ProductDto productDto = productService.getProductById(productId);
        if (!wishListService.isExistsByProduct(userDto, productDto)) {
            WishListDto wishListDto = new WishListDto();
            wishListDto.setProductId(productId);
            wishListDto.setUserId(userDto.getId());
            wishListService.save(wishListDto);
        }
        return true;
    }

    @DeleteMapping("/delete-wishlist-product/{productId}")
    public String deleteFromWishList(@PathVariable("productId") Long productId,Principal principal) {
        if(!productService.isProductExists(productId))
            return "redirect:/wish-list";

        UserDto userDto = userService.findByUsername(principal.getName());
        ProductDto productDto = productService.getProductById(productId);
        if (wishListService.isExistsByProduct(userDto,productDto))
            wishListService.deleteByProduct(userDto,productDto);
        return "redirect:/wish-list";
    }

    @DeleteMapping("/clear-wishlist")
    public String clearWishList(Principal principal) {
        UserDto userDto = userService.findByUsername(principal.getName());
        wishListService.deleteByUser(userDto);
        return "redirect:/wish-list";
    }

    @GetMapping("/wish-list")
    public String getWishList(Principal principal, Model model) {
        UserDto userDto = userService.findByUsername(principal.getName());
        List<WishListDto> wishListDtoList = wishListService.findByUser(userDto);
        List<ProductDto> productDtoList = new ArrayList<>();
        for (WishListDto wishList : wishListDtoList) {
            productDtoList.add(productService.getProductById(wishList.getProductId()));
        }
        model.addAttribute("Products",productDtoList);
        return "User/shop-wishlist";
    }
}