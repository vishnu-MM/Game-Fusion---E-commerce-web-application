package com.example.gamefusion.Controller;

import com.example.gamefusion.Configuration.UtilityClasses.EntityDtoConversionUtil;
import com.example.gamefusion.Dto.*;
import com.example.gamefusion.Entity.Cart;
import com.example.gamefusion.Entity.Coupon;
import com.example.gamefusion.Entity.OrderSub;
import com.example.gamefusion.Services.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;

@Controller
public class OrderController {
    private final UserService userService;
    private final CartService cartService;
    private final CouponService couponService;
    private final AddressService addressService;
    private final PaymentService paymentService;
    private final OrderSubService orderSubService;
    private final OrderMainService orderMainService;
    private final UserCouponsService userCouponsService;
    private final EntityDtoConversionUtil conversionUtil;

    @Autowired
    public OrderController(UserService userService, AddressService addressService,
                           CartService cartService, CouponService couponService, PaymentService paymentService,
                           OrderSubService orderSubService, OrderMainService orderMainService,
                           UserCouponsService userCouponsService, EntityDtoConversionUtil conversionUtil) {
        this.userService = userService;
        this.cartService = cartService;
        this.couponService = couponService;
        this.conversionUtil = conversionUtil;
        this.paymentService = paymentService;
        this.addressService = addressService;
        this.orderSubService = orderSubService;
        this.orderMainService = orderMainService;
        this.userCouponsService = userCouponsService;
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
                           @RequestParam(name="coupon", required = false) String couponCode,
                           Principal principal) {
        Double discount = 0.0;
        UserDto userDto = userService.findByUsername(principal.getName());
        List<CartDto> cart = cartService.findAvailableProductsByUser(userDto);
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

        if (paymentOption != 1) {
            return "redirect:/online-payment?orderId="+orderMainDto.getId();
        }
        return "User/shop-place_order-success";
    }

    @GetMapping("/my-orders")
    public String orderSummary(@RequestParam(name = "pageNo", defaultValue = "0", required = false) Integer pageNo,
                               @RequestParam(name = "pageSize", defaultValue = "1", required = false ) Integer pageSize,
                               Model model, Principal principal) {
        UserDto userDto = userService.findByUsername(principal.getName());
        PaginationInfo orderMainDtoPage= orderMainService.findOrderByUser(userDto, pageNo, pageSize);
        OrderMainDto orderMainDto = (OrderMainDto) orderMainDtoPage.getContents().get(0);
        model.addAttribute("PaymentInfo",paymentService.findByOrderMain(orderMainDto));
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
