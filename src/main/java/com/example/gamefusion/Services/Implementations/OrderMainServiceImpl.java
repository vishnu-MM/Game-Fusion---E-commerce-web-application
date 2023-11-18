package com.example.gamefusion.Services.Implementations;

import com.example.gamefusion.Configuration.UtilityClasses.EntityDtoConversionUtil;
import com.example.gamefusion.Configuration.UtilityClasses.OrderStatusUtil;
import com.example.gamefusion.Configuration.UtilityClasses.PaymentMethodUtil;
import com.example.gamefusion.Dto.*;
import com.example.gamefusion.Entity.OrderMain;
import com.example.gamefusion.Entity.User;
import com.example.gamefusion.Repository.OrderMainRepository;
import com.example.gamefusion.Services.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

@Service
public class OrderMainServiceImpl implements OrderMainService {
    private final OrderMainRepository orderMainRepository;
    private final EntityDtoConversionUtil conversionUtil;
    private final CartService cartService;
    private final ProductService productService;
    private final OrderSubService orderSubService;
    @Autowired
    public OrderMainServiceImpl(OrderMainRepository orderMainRepository, ProductService productService,
                                EntityDtoConversionUtil conversionUtil, OrderSubService orderSubService,
                                CartService cartService ) {
        this.orderMainRepository = orderMainRepository;
        this.conversionUtil = conversionUtil;
        this.cartService = cartService;
        this.productService = productService;
        this.orderSubService = orderSubService;
    }

    @Override
    public PaginationInfo findOrderByUser(UserDto userDto, Integer pageNo, Integer pageSize) {
        User user = conversionUtil.dtoToEntity(userDto);

        Pageable pageable = PageRequest.of(pageNo,pageSize, Sort.by("date").descending());
        Page<OrderMain> orderMainPage = orderMainRepository.findByUser( pageable, user);
        List<OrderMain> orderMainList = orderMainPage.getContent();
        List<OrderMainDto> orderMainDtoList = orderMainList.stream().map(conversionUtil::entityToDto).toList();

        return new PaginationInfo(
                orderMainDtoList, orderMainPage.getNumber(), orderMainPage.getSize(),
                orderMainPage.getTotalElements(), orderMainPage.getTotalPages(),
                orderMainPage.isLast(), orderMainPage.hasNext()
        );
    }

    @Override
    public PaginationInfo findAll(Integer pageNo, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNo,pageSize, Sort.by("date").descending());
        Page<OrderMain> orderMainPage = orderMainRepository.findAll( pageable);
        List<OrderMain> orderMainList = orderMainPage.getContent();
//        List<OrderMainDto> orderMainDtoList = orderMainList.stream().map(conversionUtil::entityToDto).toList();

        return new PaginationInfo(
                orderMainList, orderMainPage.getNumber(), orderMainPage.getSize(),
                orderMainPage.getTotalElements(), orderMainPage.getTotalPages(),
                orderMainPage.isLast(), orderMainPage.hasNext()
        );
    }

    @Override
    public OrderMainDto save(OrderMainDto orderMainDto) {

        OrderMain orderMain = orderMainRepository.save(
                conversionUtil.dtoToEntity(orderMainDto));
        return conversionUtil.entityToDto(orderMain);
    }

    @Override
    public OrderMainDto save(Integer addressId, Integer paymentOption,
                             UserDto userDto, List<CartDto> cart) {

        String paymentMethod = PaymentMethodUtil.getPaymentMethodByValue(paymentOption);
        int totalAmount = cartService.totalAmount( cart.stream().map(conversionUtil::dtoToEntity).toList() );
        OrderMainDto orderMainDto = new OrderMainDto();
        orderMainDto.setDate( Date.valueOf(LocalDate.now()) );
        orderMainDto.setStatus(String.valueOf(OrderStatusUtil.PENDING));
        orderMainDto.setUserId(userDto.getId());
        orderMainDto.setAddressId(addressId);
        orderMainDto.setAmount(totalAmount);
        orderMainDto.setPaymentMethod(paymentMethod);

        orderMainDto = save(orderMainDto);;
        decrementQuantity(orderMainDto);
        orderMainDto.setOrderId(generateOrderId(orderMainDto));
        return save(orderMainDto);
    }

    @Override
    public OrderMainDto findOrderById(Integer orderId) {
        return conversionUtil.entityToDto(
                orderMainRepository.findById(orderId)
                .orElseThrow(EntityNotFoundException::new));
    }

    @Override
    public Boolean isExistsByID(Integer orderId) {
        return orderMainRepository.existsById(orderId);
    }

    @Override
    public void decrementQuantity(OrderMainDto orderMainDto) {
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

    @Override
    public void incrementQuantity(OrderMainDto orderMainDto) {
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

    @Override
    public void cancelOrder(Integer orderId) {
        OrderMainDto orderMainDto = findOrderById(orderId);
        orderMainDto.setStatus(String.valueOf(OrderStatusUtil.CANCELED));
        save(orderMainDto);
        incrementQuantity(orderMainDto);
    }

    private String generateOrderId(OrderMainDto orderMainDto){
        LocalDate date = Date.valueOf(orderMainDto.getDate().toString()).toLocalDate();
        return  "#" +
                date.getYear() +
                date.getMonthValue() +
                date.getDayOfMonth() +
                orderMainDto.getId();
    }
}
