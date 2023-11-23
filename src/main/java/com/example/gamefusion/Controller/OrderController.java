package com.example.gamefusion.Controller;

import com.example.gamefusion.Configuration.UtilityClasses.EntityDtoConversionUtil;
import com.example.gamefusion.Configuration.UtilityClasses.PaymentMethodUtil;
import com.example.gamefusion.Dto.*;
import com.example.gamefusion.Entity.Cart;
import com.example.gamefusion.Entity.OrderSub;
import com.example.gamefusion.Services.*;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import jakarta.validation.Valid;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class OrderController {
    private final UserService userService;
    private final CartService cartService;
    private final AddressService addressService;
    private final ProductService productService;
    private final PaymentService paymentService;
    private final OrderSubService orderSubService;
    private final OrderMainService orderMainService;
    private final EntityDtoConversionUtil conversionUtil;
    @Value("${rzp_key_id}")
    private String key;
    @Value("${rzp_key_secret}")
    private String secretkey;
    @Autowired
    public OrderController(UserService userService, AddressService addressService,
                           CartService cartService, ProductService productService, EntityDtoConversionUtil conversionUtil,
                           OrderSubService orderSubService, OrderMainService orderMainService,
                           PaymentService paymentService) {
        this.userService = userService;
        this.addressService = addressService;
        this.cartService = cartService;
        this.productService = productService;
        this.orderMainService = orderMainService;
        this.orderSubService = orderSubService;
        this.conversionUtil = conversionUtil;
        this.paymentService = paymentService;
    }

    @GetMapping("/checkout-page")
    public String getCheckOutPage(Model model, Principal principal) {
        AddressDto addressDto = new AddressDto();
        UserDto user = userService.findByUsername(principal.getName());
        List<AddressDto> addressDtoList = addressService.findByUser(user.getId(),true);
        List<CartDto> cartDtoList = cartService.findAvailableProductsByUser(user);
        List<Cart> cart = cartDtoList.stream().map(conversionUtil::dtoToEntity).toList();

        model.addAttribute("User", user);
        model.addAttribute("NewAddress", addressDto);
        model.addAttribute("ProductDetails", cart);
        model.addAttribute("AddressList", addressDtoList);
        model.addAttribute("TotalAmount",cartService.totalAmount(cartDtoList));
        return "User/shop-checkout";
    }

    @PostMapping("/checkout")
    public String checkout(@RequestParam("addressId") Integer addressId,
                           @RequestParam("payment_option") Integer paymentOption,
                           Principal principal, Model model) {
        UserDto userDto = userService.findByUsername(principal.getName());

        List<CartDto> cart = cartService.findAvailableProductsByUser(userDto);
        Integer totalAmount = cartService.totalAmount(cart);
        OrderMainDto orderMainDto = orderMainService.save(addressId,paymentOption,userDto,totalAmount);
        orderSubService.save(orderMainDto.getId(),cart);

        PaymentDto paymentDto = new PaymentDto();
        paymentDto.setPaymentMethod(PaymentMethodUtil.getPaymentMethodByValue(paymentOption));
        paymentDto.setPaymentId("UW"+orderMainDto.getOrderId());
        paymentDto.setOrderId(orderMainDto.getId());
        paymentDto.setAmount(orderMainDto.getAmount());
        paymentDto.setDate(Date.valueOf(LocalDate.now()));
        paymentDto.setPaymentStatus(false);
        paymentService.save(paymentDto);

        if (paymentOption != 1) {
            return "redirect:/online-payment?orderId="+orderMainDto.getId();
        }
        return "User/shop-place_order-success";
    }

    @GetMapping("/online-payment")
    public String getPaymentPage(@RequestParam("orderId") Integer orderId, Model model) throws RazorpayException{

        OrderMainDto orderMainDto = orderMainService.findOrderById(orderId);
        List<OrderSubDto> orderSubDtoList = orderSubService.findOrderByOrder(orderMainDto);
        AddressDto addressDto = addressService.findById(orderMainDto.getAddressId());
        UserDto userDto = userService.findById(orderMainDto.getUserId());
        PaymentDto paymentDto = paymentService.findByOrderMain(orderMainDto);
        Map<Long,ProductDto> productDtoMap = new HashMap<>();
        for (OrderSubDto orderSubDto: orderSubDtoList) {
            productDtoMap.put(
                orderSubDto.getProductId(),
                productService.getProductById(orderSubDto.getProductId())
            );
        }
        RazorpayClient razorpayClient = new RazorpayClient(key,secretkey);
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

    @GetMapping("/verify-payment")
    public String verifyPayment(@RequestParam("orderId") Integer orderMainId) {
        OrderMainDto orderMainDto = orderMainService.findOrderById(orderMainId);
        orderMainService.decrementQuantity(orderMainDto);
        PaymentDto paymentDto = paymentService.findByOrderMain(orderMainDto);
        paymentDto.setPaymentStatus(true);
        paymentService.save(paymentDto);
        return "User/shop-place_order-success";
    }

    @GetMapping("/my-orders")
    public String orderSummary(@RequestParam(name = "pageNo", defaultValue = "0", required = false) Integer pageNo,
                               @RequestParam(name = "pageSize", defaultValue = "1", required = false ) Integer pageSize,
                               Model model, Principal principal) {
        UserDto userDto = userService.findByUsername(principal.getName());
        PaginationInfo orderMainDtoPage= orderMainService.findOrderByUser(userDto, pageNo, pageSize);
        OrderMainDto orderMainDto = (OrderMainDto) orderMainDtoPage.getContents().get(0);
        model.addAttribute("OrderSubDetails", getOrderSubList(orderMainDto));
        model.addAttribute("OrderDetails",orderMainDtoPage);
        model.addAttribute("Address",addressService.findById(orderMainDto.getAddressId()));
        return "User/page-my-orders";
    }

    private List<OrderSub> getOrderSubList(OrderMainDto orderMainDto) {
        return orderSubService.findOrderByOrder(orderMainDto)
                .stream().map(conversionUtil::dtoToEntity).toList();
    }

    @PutMapping("/cancel-order/{orderID}")
    private String cancelOrder(@PathVariable("orderID") Integer orderId) {
        if (orderMainService.isExistsByID(orderId))
            orderMainService.cancelOrder(orderId);
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
}
