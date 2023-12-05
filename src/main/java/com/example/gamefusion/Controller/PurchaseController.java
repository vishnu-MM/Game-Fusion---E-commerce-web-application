package com.example.gamefusion.Controller;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import com.example.gamefusion.Services.*;
import com.razorpay.RazorpayException;
import com.example.gamefusion.Dto.*;
import org.springframework.ui.Model;
import com.razorpay.RazorpayClient;
import java.security.Principal;
import org.json.JSONObject;
import java.time.LocalDate;
import com.razorpay.Order;
import java.util.HashMap;
import java.util.List;
import java.sql.Date;
import java.util.Map;

@Controller
public class PurchaseController {
    @Value("${rzp_key_id}")
    private String key;
    @Value("${rzp_key_secret}")
    private String secretKey;

    private final UserService userService;
    private final WalletService walletService;
    private final CouponService couponService;
    private final AddressService addressService;
    private final ProductService productService;
    private final PaymentService paymentService;
    private final OrderSubService orderSubService;
    private final CategoryService categoryService;
    private final OrderMainService orderMainService;
    private final UserCouponsService userCouponsService;
    private final OrderHistoryService orderHistoryService;
    private final CategoryOfferService categoryOfferService;
    @Autowired
    public PurchaseController(WalletService walletService, CouponService couponService,
                              ProductService productService, PaymentService paymentService,
                              UserService userService,CategoryOfferService categoryOfferService,
                              OrderSubService orderSubService, OrderMainService orderMainService,
                              UserCouponsService userCouponsService, AddressService addressService,
                              CategoryService categoryService, OrderHistoryService orderHistoryService) {
        this.userService = userService;
        this.couponService = couponService;
        this.walletService = walletService;
        this.addressService = addressService;
        this.paymentService = paymentService;
        this.productService = productService;
        this.categoryService = categoryService;
        this.orderSubService = orderSubService;
        this.orderMainService = orderMainService;
        this.userCouponsService = userCouponsService;
        this.orderHistoryService = orderHistoryService;
        this.categoryOfferService = categoryOfferService;
    }

    @GetMapping("/online-payment")
    public String getPaymentPage(@RequestParam("orderId") Integer orderId, Model model)
                                                            throws RazorpayException
    {
        OrderMainDto orderMainDto = orderMainService.findOrderById(orderId);

        if (paymentService.isPaymentSuccess(orderMainDto))
            return "redirect:/my-orders";

        List<OrderSubDto> orderSubDtoList = orderSubService.findOrderByOrder(orderMainDto);
        AddressDto addressDto = addressService.findById(orderMainDto.getAddressId());
        UserDto userDto = userService.findById(orderMainDto.getUserId());
        PaymentDto paymentDto = paymentService.findByOrderMain(orderMainDto);

        Map<Long,ProductDto> productDtoMap = new HashMap<>();
        for (OrderSubDto orderSubDto: orderSubDtoList) {
            productDtoMap.put(
                orderSubDto.getProductId(),productService.getProductById(orderSubDto.getProductId())
            );
        }
        RazorpayClient razorpayClient = new RazorpayClient(key,secretKey);
        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount",50000);
        orderRequest.put("currency","INR");
        orderRequest.put("receipt", "receipt#1");
        Order order = razorpayClient.orders.create(orderRequest);

        model.addAttribute("orderSubDtoList",orderSubDtoList);
        model.addAttribute("productDtoMap",productDtoMap);
        model.addAttribute("orderMainDto",orderMainDto);
        model.addAttribute("paymentDto",paymentDto);
        model.addAttribute("addressDto",addressDto);
        model.addAttribute("userDto",userDto);
        model.addAttribute("order",order);
        return "User/shop-payment";
    }

    @GetMapping("/wallet-payment")
    public String walletPayment(@RequestParam("orderId") Integer orderId, Principal principal) {
        OrderMainDto orderMainDto = orderMainService.findOrderById(orderId);
        UserDto userDto = userService.findByUsername(principal.getName());

        WalletDto walletDto = new WalletDto();
        walletDto.setUserId(userDto.getId());
        walletDto.setTransactionType("Debit");
        walletDto.setAmount(orderMainDto.getAmount());
        walletDto.setDateTime(Date.valueOf(LocalDate.now()));
        walletDto.setDescription("Payment on Purchase - Order "+orderMainDto.getOrderId());
        walletService.save(walletDto);

        return paymentSuccessHandler(orderMainDto);
    }

    @GetMapping("/payment-status-confirmation/{orderId}")
    @ResponseBody
    public Boolean getPaymentStatus(@PathVariable Integer orderId) {
        System.out.println("Order ID"+orderId);
        OrderMainDto orderMainDto = orderMainService.findOrderById(orderId);
        return paymentService.isPaymentSuccess(orderMainDto);
    }

    @GetMapping("/verify-payment")
    public String verifyPayment(@RequestParam("orderId") Integer orderId) {
        OrderMainDto orderMainDto = orderMainService.findOrderById(orderId);
        return paymentSuccessHandler(orderMainDto);
    }

    @NotNull
    private String paymentSuccessHandler(OrderMainDto orderMainDto) {
        orderMainService.decrementQuantity(orderMainDto);
        PaymentDto paymentDto = paymentService.findByOrderMain(orderMainDto);
        paymentDto.setPaymentStatus(true);
        paymentService.save(paymentDto);
        orderHistoryService.save(orderMainDto);
        return "User/shop-place_order-success";
    }

    @PostMapping("/apply-coupon")
    public ResponseEntity<Double> applyCoupon(@RequestParam("total") Integer totalAmount,
                                              @RequestParam("couponCode") String couponCode,
                                              Principal principal) {

        if (!couponService.isExistByCouponCode(couponCode))
            return ResponseEntity.notFound().build();

        CouponDto couponDto = couponService.findByCoupon(couponCode);
        UserDto userDto = userService.findByUsername(principal.getName());

        if (userCouponsService.isExistByUserAndCoupon(userDto,couponDto))
            return ResponseEntity.status(409).build();

        if (LocalDate.now().isAfter(couponDto.getExpiryDate().toLocalDate()))
            return ResponseEntity.notFound().build();

        if ( totalAmount < couponDto.getMinimumAmount())
            return new ResponseEntity<>((double)couponDto.getMinimumAmount(), HttpStatus.BAD_REQUEST);

        return new ResponseEntity<>((double)couponDto.getDiscount(), HttpStatus.OK);
    }

    @GetMapping("/get-category-offer")
    @ResponseBody
    public ResponseEntity<CategoryOfferDto> applyCoupon(@RequestParam("productId") Long productId,
                                                        @RequestParam("categoryId") Long categoryId) {
        if ( (!categoryService.isCategoryExist(categoryId)) || (!productService.isProductExists(productId)))
            return ResponseEntity.status(501).build();

        CategoryDto categoryDto = categoryService.findById(categoryId);
        ProductDto productDto = productService.getProductById(productId);
        CategoryOfferDto categoryOfferDto = categoryOfferService.findByCategory(categoryDto);

        if (LocalDate.now().isAfter(categoryOfferDto.getExpiryDate().toLocalDate()))
            return ResponseEntity.status(502).build();
        if (productDto.getDiscountPrice() < categoryOfferDto.getMinimumAmount())
            return ResponseEntity.badRequest().build();

        return new ResponseEntity<>(categoryOfferDto, HttpStatus.OK);
    }

    @GetMapping("/wallet-balance")
    @ResponseBody
    public Double getWalletBalance(Principal principal) {
        double balance = 0;
        UserDto userDto = userService.findByUsername(principal.getName());
        if (walletService.existByUser(userDto.getId())) {
            balance = walletService.getWalletBalance(userDto);
        }
        return balance;
    }
}
