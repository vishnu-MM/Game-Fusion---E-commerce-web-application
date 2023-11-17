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
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.StringJoiner;

@Controller
public class CartAndOrderController {
    private final UserService userService;
    private final CartService cartService;
    private final ProductService productService;
    private final AddressService addressService;
    private final OrderMainService orderMainService;
    private final OrderSubService orderSubService;
    private final EntityDtoConversionUtil conversionUtil;
    @Autowired
    public CartAndOrderController(UserService userService, CartService cartService,
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

    @PutMapping("/delete-product/{CartId}")
    public String deleteFromCart(@PathVariable("CartId") Integer cartId) {
        if (cartService.isExistsById(cartId)) {
            cartService.deleteById(cartId);
        }
        return "redirect:/cart-products";
    }

    @GetMapping("/cart-products")
    public String getProductsFromCart(Principal principal, Model model) {
        UserDto userDto = userService.findByUsername(principal.getName());
        List<Cart> cart = cartService.findByUser(userDto);
        model.addAttribute("TotalAmount",cartService.totalAmount(cart));
        model.addAttribute("ProductDetails", cart);
        return "User/shop-cart";
    }

    @PutMapping("/updateQuantity")
    public String updateQuantity(@RequestParam("CartID") Integer cartId,
                                 @RequestParam("qty") Integer qty) {
        CartDto cartDto = cartService.findById(cartId);
        cartDto.setQty(qty);
        cartService.addToCart(cartDto);
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
        List<AddressDto> addressDtoList = addressService.findByUser(user.getId());
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
            System.out.println(result.getFieldError());
            return getCheckOutPage(model,principal);
        }
        UserDto user = userService.findByUsername(principal.getName());
        newAddress.setUserId(user.getId());
        addressService.save(newAddress);
        return "redirect:/checkout-page";
    }

    @PostMapping("/checkout")
    public String checkout(@RequestParam("addressId") Integer addressId,
                           @RequestParam("payment_option") Integer paymentOption,
                           Principal principal) {
        UserDto userDto = userService.findByUsername(principal.getName());
        List<CartDto> cart = cartService.getCartByUser(userDto);
        OrderMainDto orderMainDto = saveOrderMainDto(addressId,paymentOption,userDto,cart);
        generateOrderId(orderMainDto);
        saveOrderSubDto(orderMainDto.getId(),cart);
        decrementQuantity(orderMainDto);
        return "User/shop-place_order-success";
    }

        @GetMapping("/my-orders")
        public String orderSummary(@RequestParam(name = "pageNo", defaultValue = "0", required = false) Integer pageNo,
                                    @RequestParam(name = "pageSize", defaultValue = "1", required = false ) Integer pageSize,
                                    Model model,Principal principal) {
            UserDto userDto = userService.findByUsername(principal.getName());
            PaginationInfo orderMainDtoPage= orderMainService.findOrderByUser(userDto, pageNo, pageSize);
            model.addAttribute("OrderSubDetails",
                    getOrderSubList((OrderMainDto) orderMainDtoPage.getContents().get(0)));
            model.addAttribute("OrderDetails",orderMainDtoPage);
            generateOrderId((OrderMainDto) orderMainDtoPage.getContents().get(0));
            return "User/page-my-orders";
        }

        private List<OrderSub> getOrderSubList( OrderMainDto orderMainDto) {
            return orderSubService.findOrderByOrder(orderMainDto)
                    .stream().map(conversionUtil::dtoToEntity).toList();
        }

        @PutMapping("/cancel-order/{orderID}")
        private String cancelOrder(@PathVariable("orderID") Integer orderId) {
            if (orderMainService.isExistsByID(orderId)){
                OrderMainDto orderMainDto = orderMainService.findOrderById(orderId);
                orderMainDto.setStatus(String.valueOf(OrderStatusUtil.CANCELED));
                orderMainService.save(orderMainDto);
                incrementQuantity(orderMainDto);
            }
            return "redirect:/my-orders";
        }

    private void decrementQuantity(OrderMainDto orderMainDto) {
        if (orderMainDto == null) return;
        List<OrderSubDto> orderSubDtoList = orderSubService.findOrderByOrder(orderMainDto);
        for (OrderSubDto orderSub : orderSubDtoList ) {
            long productID = orderSub.getProductId();
            ProductDto productDto = productService.getProductById(productID);
            int orderQty = orderSub.getQty();
            int oldQty = productDto.getQty();
            int newQty = oldQty - orderQty;
            productService.updateQuantity(productID,newQty);
        }
    }

    private void incrementQuantity(OrderMainDto orderMainDto) {
        if (orderMainDto == null) return;
        List<OrderSubDto> orderSubDtoList = orderSubService.findOrderByOrder(orderMainDto);
        for (OrderSubDto orderSub : orderSubDtoList ) {
            long productID = orderSub.getProductId();
            ProductDto productDto = productService.getProductById(productID);
            int orderQty = orderSub.getQty();
            int oldQty = productDto.getQty();
            int newQty = oldQty + orderQty;
            productService.updateQuantity(productID,newQty);
        }
    }

    private void generateOrderId(OrderMainDto orderMainDto){
        LocalDate date =Date.valueOf(orderMainDto.getDate().toString()).toLocalDate();
        String orderId = "#" +
                date.getYear() +
                date.getMonthValue() +
                date.getDayOfMonth() +
                orderMainDto.getId();
        orderMainDto.setOrderId(orderId);
        orderMainService.save(orderMainDto);
    }

    private void saveOrderSubDto(Integer id, List<CartDto> cartDtoList) {
        List<Cart> cartList = cartDtoList.stream().map(conversionUtil::dtoToEntity).toList();
        for (Cart cart : cartList) {
            if (cart.getProduct().getStatus()) {
                OrderSubDto orderSubDto = new OrderSubDto();
                orderSubDto.setOrderMainId(id);
                orderSubDto.setProductId(cart.getProduct().getId());
                orderSubDto.setQty(cart.getQty());
                orderSubService.save(orderSubDto);
            }
        }
    }

    private OrderMainDto saveOrderMainDto(Integer addressId, Integer paymentOption,
                                          UserDto userDto, List<CartDto> cart) {

        OrderMainDto orderMainDto = new OrderMainDto();
        orderMainDto.setDate( Date.valueOf(LocalDate.now()) );
        orderMainDto.setStatus(String.valueOf(OrderStatusUtil.PENDING));
        orderMainDto.setUserId(userDto.getId());
        orderMainDto.setAmount(cartService.totalAmount(
                cart.stream().map(conversionUtil::dtoToEntity).toList()
        ));
        if (paymentOption.equals(PaymentMethodUtil.ONLINE_PAYMENT.getValue()))
            orderMainDto.setPaymentMethod(String.valueOf(PaymentMethodUtil.ONLINE_PAYMENT));
        else if (paymentOption.equals(PaymentMethodUtil.CASH_ON_DELIVERY.getValue()))
            orderMainDto.setPaymentMethod(String.valueOf(PaymentMethodUtil.CASH_ON_DELIVERY));
        else throw new RuntimeException();

        if (addressService.isExistById(addressId)) {
            AddressDto addressDto = addressService.findById(addressId);
            orderMainDto.setCountry(addressDto.getCountry());
            orderMainDto.setState(addressDto.getState());
            orderMainDto.setDistrict(addressDto.getDistrict());
            orderMainDto.setStreetAddress(addressDto.getStreetAddress());
            orderMainDto.setPinCode(addressDto.getPinCode());
            orderMainDto.setPhone(addressDto.getPhone());
        }

        return orderMainService.save(orderMainDto);
    }
}