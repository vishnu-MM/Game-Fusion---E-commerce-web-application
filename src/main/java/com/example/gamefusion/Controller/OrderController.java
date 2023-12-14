package com.example.gamefusion.Controller;

import com.example.gamefusion.Configuration.UtilityClasses.EntityDtoConversionUtil;
import com.example.gamefusion.Configuration.UtilityClasses.PDFGenerator;
import com.example.gamefusion.Dto.*;
import com.example.gamefusion.Entity.Cart;
import com.example.gamefusion.Entity.OrderSub;
import com.example.gamefusion.Services.*;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Controller
public class OrderController {
    private final UserService userService;
    private final CartService cartService;
    private final CouponService couponService;
    private final ProductService productService;
    private final AddressService addressService;
    private final PaymentService paymentService;
    private final OrderSubService orderSubService;
    private final OrderMainService orderMainService;
    private final UserCouponsService userCouponsService;
    private final EntityDtoConversionUtil conversionUtil;
    private final OrderHistoryService orderHistoryService;

    @Autowired
    public OrderController(CartService cartService, CouponService couponService,
                           UserService userService, ProductService productService, AddressService addressService,
                           PaymentService paymentService, OrderSubService orderSubService,
                           OrderMainService orderMainService, OrderHistoryService orderHistoryService,
                           UserCouponsService userCouponsService, EntityDtoConversionUtil conversionUtil) {
        this.userService = userService;
        this.cartService = cartService;
        this.couponService = couponService;
        this.productService = productService;
        this.conversionUtil = conversionUtil;
        this.paymentService = paymentService;
        this.addressService = addressService;
        this.orderSubService = orderSubService;
        this.orderMainService = orderMainService;
        this.userCouponsService = userCouponsService;
        this.orderHistoryService = orderHistoryService;
    }

    @GetMapping("/checkout-page")
    public String getCheckOutPage(Model model, Principal principal) {
        AddressDto addressDto = new AddressDto();
        List<AddressDto> addressDtoList = new ArrayList<>();
        UserDto user = userService.findByUsername(principal.getName());
        List<CartDto> cartDtoList = cartService.findAvailableProductsByUser(user);
        List<Cart> cart = cartDtoList.stream().map(conversionUtil::dtoToEntity).toList();

        if ( addressService.isExistsByUser(user.getId()))
            addressDtoList = addressService.findByUser(user.getId(),true);

        model.addAttribute("User", user);
        model.addAttribute("ProductDetails", cart);
        model.addAttribute("NewAddress", addressDto);
        model.addAttribute("AddressList", addressDtoList);
        model.addAttribute("TotalAmount",cartService.totalAmount(cartDtoList));
        return "User/shop-checkout";
    }

    @PostMapping("/checkout")
    public String checkout(@RequestParam("addressId") Integer addressId,
                           @RequestParam("payment_option") Integer paymentOption,
                           @RequestParam(name="coupon", required = false) String couponCode,
                           Principal principal) {
        Double discount = 0.0;
        UserDto userDto = userService.findByUsername(principal.getName());
        List<CartDto> cart = cartService.findAvailableProductsByUser(userDto);

        if (cart == null) return "redirect:/cart-products";
        if (addressId == null || paymentOption==null) return "redirect:/checkout-page";

        Integer totalAmount = cartService.totalAmount(cart);

        if (couponCode != null) {
            CouponDto coupon = couponService.findByCoupon(couponCode);
            discount = coupon.getDiscount();
            UserCouponsDto userCoupons = new UserCouponsDto();
            userCoupons.setCouponId(coupon.getId());
            userCoupons.setUserId(userDto.getId());
            userCouponsService.save(userCoupons);
        }

        OrderMainDto orderMainDto = orderMainService.save(addressId,paymentOption,userDto,totalAmount,discount);

        orderSubService.save(orderMainDto.getId(),cart);
        paymentService.save(paymentOption,orderMainDto);

        if (paymentOption == 2)
            return "redirect:/online-payment?orderId="+orderMainDto.getId();

        else if (paymentOption == 3)
            return "redirect:/wallet-payment?orderId="+orderMainDto.getId();

        OrderHistoryDto historyDto = new OrderHistoryDto();
        historyDto.setOrderId(orderMainDto.getId());
        historyDto.setOrderStatus(orderMainDto.getStatus());
        historyDto.setDate(Date.valueOf(LocalDate.now()));
        orderHistoryService.save(historyDto);
        orderMainService.decrementQuantity(orderMainDto);
        return "User/shop-place_order-success";
    }

    @GetMapping("/my-orders")
    public String orderSummary(Model model, Principal principal) {
        UserDto user = userService.findByUsername(principal.getName());
        List<OrderMainDto> orderMainList = orderMainService.findOrderByUser(user);
        model.addAttribute("OrderMain",orderMainList);
        model.addAttribute("Products",orderSubService.findByOrder(orderMainList));
        model.addAttribute("Address",addressService.findByOrder(orderMainList));
        model.addAttribute("Payment",paymentService.findByOrder(orderMainList));
        return "User/My-Orders";
    }

    @GetMapping("/order-details")
    public String getOrderDetails(@RequestParam("orderId") Integer orderId,Principal principal, Model model) {
        if (!orderMainService.isExistsByID(orderId))
            return "redirect:/";
        OrderMainDto orderMainDto = orderMainService.findOrderById(orderId);
        UserDto userDto = userService.findByUsername(principal.getName());

        if (!Objects.equals(orderMainDto.getUserId(), userDto.getId()))
            return "redirect:/";

        Boolean isExist = orderHistoryService.isExistsByOrderId(orderId);
        model.addAttribute("OrderMain",orderMainDto);
        model.addAttribute("OrderHistory",(isExist) ? orderHistoryService.findByOrderId(orderId) : null);
        model.addAttribute("Payment",paymentService.findByOrderMain(orderMainDto));
        model.addAttribute("Products",orderSubService.findByOrder(orderMainDto));
        model.addAttribute("Address",addressService.findById(orderMainDto.getAddressId()));
        return "User/page-order-details";

    }

    @PutMapping("/cancel-order/{orderID}")
    private String cancelOrder(@PathVariable("orderID") Integer orderId) {
        if (orderMainService.isExistsByID(orderId)) {
            OrderMainDto orderMainDto = orderMainService.requestCancelOrder(orderId);
            orderHistoryService.save(orderMainDto);
        }
        return "redirect:/my-orders";
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

    @GetMapping("/download-invoice/{orderID}")
    public void generatePdf(HttpServletResponse response, @PathVariable Integer orderID) {
        response.setContentType("application/pdf");
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd:HH:mm:ss");
        String currentDateTime = dateFormat.format(new java.util.Date());
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=pdf_order_invoice"+currentDateTime+".pdf";
        response.setHeader(headerKey, headerValue);

        OrderMainDto orderMainDto = orderMainService.findOrderById(orderID);
        List<OrderSub> orderSubList = orderSubService.findOrderByOrder(orderMainDto)
                .stream().map(conversionUtil::dtoToEntity).toList();
        PDFGenerator.generate(response,orderSubList,conversionUtil.dtoToEntity(orderMainDto));
    }

    @GetMapping("/buy-now")
    public String buyNow(@RequestParam("ProductId") Long productId, @RequestParam("Qty") Integer qty,
                         Principal principal, Model model) {

        if (productId == null) return "redirect:/user/home";
        if (qty == null) qty = 1;

        AddressDto addressDto = new AddressDto();
        List<AddressDto> addressDtoList = new ArrayList<>();
        UserDto user = userService.findByUsername(principal.getName());
        ProductDto product = productService.getProductById(productId);
        List<Cart> cartDtoList = new ArrayList<>();
        Cart cart = new Cart();
        cart.setQty(qty);
        cart.setUser(conversionUtil.dtoToEntity(user));
        cart.setProduct(conversionUtil.dtoToEntity(product));
        cartDtoList.add(cart);

        if ( addressService.isExistsByUser(user.getId()))
            addressDtoList = addressService.findByUser(user.getId(),true);

        model.addAttribute("User", user);
        model.addAttribute("ProductDetails", cartDtoList);
        model.addAttribute("NewAddress", addressDto);
        model.addAttribute("AddressList", addressDtoList);
        model.addAttribute("TotalAmount",(product.getDiscountPrice()*qty));
        return "User/shop-checkout";
    }
}
