package com.example.gamefusion.Controller;

import com.example.gamefusion.Configuration.UtilityClasses.EntityDtoConversionUtil;
import com.example.gamefusion.Dto.*;
import com.example.gamefusion.Entity.OrderSub;
import com.example.gamefusion.Services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;

@Controller
public class OrderController {
    private final UserService userService;
    private final CartService cartService;
    private final AddressService addressService;
    private final OrderSubService orderSubService;
    private final OrderMainService orderMainService;
    private final EntityDtoConversionUtil conversionUtil;
    @Autowired
    public OrderController(UserService userService, AddressService addressService,
                           CartService cartService, EntityDtoConversionUtil conversionUtil,
                           OrderSubService orderSubService,OrderMainService orderMainService ) {
        this.userService = userService;
        this.addressService = addressService;
        this.cartService = cartService;
        this.orderMainService = orderMainService;
        this.orderSubService = orderSubService;
        this.conversionUtil = conversionUtil;
    }

    @PostMapping("/checkout")
    public String checkout(@RequestParam("addressId") Integer addressId,
                           @RequestParam("payment_option") Integer paymentOption,
                           Principal principal) {
        UserDto userDto = userService.findByUsername(principal.getName());
        List<CartDto> cart = cartService.getCartByUser(userDto);
        System.out.println("------");
        OrderMainDto orderMainDto = orderMainService.save(addressId,paymentOption,userDto,cart);
        System.out.println("orderMainDto saved");
        orderSubService.save(orderMainDto.getId(),cart);
        System.out.println("orderSubDto saved");
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
}
