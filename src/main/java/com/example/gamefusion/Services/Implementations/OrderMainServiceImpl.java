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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class OrderMainServiceImpl implements OrderMainService {
    private final UserService userService;
    private final WalletService walletService;
    private final ProductService productService;
    private final OrderSubService orderSubService;
    private final EntityDtoConversionUtil conversionUtil;
    private final OrderMainRepository repository;
    @Autowired
    public OrderMainServiceImpl(UserService userService, OrderMainRepository repository,
                                ProductService productService, EntityDtoConversionUtil conversionUtil,
                                WalletService walletService, OrderSubService orderSubService) {
        this.userService = userService;
        this.walletService = walletService;
        this.repository = repository;
        this.conversionUtil = conversionUtil;
        this.productService = productService;
        this.orderSubService = orderSubService;
    }

    @Override
    public PaginationInfo findOrderByUser(UserDto userDto, Integer pageNo, Integer pageSize) {
        User user = conversionUtil.dtoToEntity(userDto);

        Pageable pageable = PageRequest.of(pageNo,pageSize, Sort.by("date").descending());
        Page<OrderMain> orderMainPage = repository.findByUser( pageable, user);
        List<OrderMain> orderMainList = orderMainPage.getContent();
        List<OrderMainDto> orderMainDtoList = orderMainList.stream().map(conversionUtil::entityToDto).toList();

        return new PaginationInfo(
                orderMainDtoList, orderMainPage.getNumber(), orderMainPage.getSize(),
                orderMainPage.getTotalElements(), orderMainPage.getTotalPages(),
                orderMainPage.isLast(), orderMainPage.hasNext()
        );
    }

    @Override
    public PaginationInfo findOrderByStatus(String status, Integer pageNo, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNo,pageSize, Sort.by("date").descending());
        Page<OrderMain> orderMainPage = repository.findByStatus(status,pageable);
        List<OrderMain> orderMainList = orderMainPage.getContent();
//        List<OrderMainDto> orderMainDtoList = orderMainList.stream().map(conversionUtil::entityToDto).toList();

        return new PaginationInfo(
                orderMainList, orderMainPage.getNumber(), orderMainPage.getSize(),
                orderMainPage.getTotalElements(), orderMainPage.getTotalPages(),
                orderMainPage.isLast(), orderMainPage.hasNext()
        );
    }

    @Override
    public PaginationInfo findAll(Integer pageNo, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNo,pageSize, Sort.by("date").descending());
        Page<OrderMain> orderMainPage = repository.findAll( pageable);
        List<OrderMain> orderMainList = orderMainPage.getContent();
//        List<OrderMainDto> orderMainDtoList = orderMainList.stream().map(conversionUtil::entityToDto).toList();

        return new PaginationInfo(
                orderMainList, orderMainPage.getNumber(), orderMainPage.getSize(),
                orderMainPage.getTotalElements(), orderMainPage.getTotalPages(),
                orderMainPage.isLast(), orderMainPage.hasNext()
        );
    }

    @Override
    public PaginationInfo findAllFilterByDay(Integer pageNo, Integer pageSize, Date date) {
        Pageable pageable = PageRequest.of(pageNo,pageSize, Sort.by("date").descending());
        Page<OrderMain> orderMainPage = repository.findByDate( pageable, date);
        List<OrderMain> orderMainList = orderMainPage.getContent();
//        List<OrderMainDto> orderMainDtoList = orderMainList.stream().map(conversionUtil::entityToDto).toList();

        return new PaginationInfo(
                orderMainList, orderMainPage.getNumber(), orderMainPage.getSize(),
                orderMainPage.getTotalElements(), orderMainPage.getTotalPages(),
                orderMainPage.isLast(), orderMainPage.hasNext()
        );
    }

    @Override
    public PaginationInfo findAllBetweenDay(Integer pageNo, Integer pageSize, Date startDate, Date endDate) {
        Pageable pageable = PageRequest.of(pageNo,pageSize, Sort.by("date").descending());
        Page<OrderMain> orderMainPage = repository.findByDateBetween(startDate, endDate, pageable);
        List<OrderMain> orderMainList = orderMainPage.getContent();
//        List<OrderMainDto> orderMainDtoList = orderMainList.stream().map(conversionUtil::entityToDto).toList();

        return new PaginationInfo(
                orderMainList, orderMainPage.getNumber(), orderMainPage.getSize(),
                orderMainPage.getTotalElements(), orderMainPage.getTotalPages(),
                orderMainPage.isLast(), orderMainPage.hasNext()
        );
    }

    @Override
    public PaginationInfo findByDayAndStatus(Integer pageNo, Integer pageSize, Date date, String status) {
        Pageable pageable = PageRequest.of(pageNo,pageSize, Sort.by("date").descending());
        Page<OrderMain> orderMainPage = repository.findByDateAndStatus(date,status,pageable);
        List<OrderMain> orderMainList = orderMainPage.getContent();
//        List<OrderMainDto> orderMainDtoList = orderMainList.stream().map(conversionUtil::entityToDto).toList();

        return new PaginationInfo(
                orderMainList, orderMainPage.getNumber(), orderMainPage.getSize(),
                orderMainPage.getTotalElements(), orderMainPage.getTotalPages(),
                orderMainPage.isLast(), orderMainPage.hasNext()
        );
    }

    @Override
    public PaginationInfo findByDatesBetweenAndStatus(Integer pageNo, Integer pageSize, Date startDate, Date endDate, String status) {
        Pageable pageable = PageRequest.of(pageNo,pageSize, Sort.by("date").descending());
        Page<OrderMain> orderMainPage = repository.findByDateBetweenAndStatus(startDate,endDate,status,pageable);
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

        OrderMain orderMain = repository.save(
                conversionUtil.dtoToEntity(orderMainDto));
        return conversionUtil.entityToDto(orderMain);
    }

    @Override
    public OrderMainDto save(Integer addressId, Integer paymentOption,
                             UserDto userDto, Integer totalAmount, Double discount) {

        Double discountedAmount = totalAmount - ((discount / 100) * totalAmount);
        String paymentMethod = PaymentMethodUtil.getPaymentMethodByValue(paymentOption);

        OrderMainDto orderMainDto = new OrderMainDto();
        orderMainDto.setAddressId(addressId);
        orderMainDto.setAmount(discountedAmount);
        orderMainDto.setPaymentMethod(paymentMethod);
        orderMainDto.setStatus(String.valueOf(OrderStatusUtil.PENDING));
        orderMainDto.setDate(Date.valueOf(LocalDate.now()));
        orderMainDto.setUserId(userDto.getId());
        orderMainDto = save(orderMainDto);
        orderMainDto.setOrderId(generateOrderId(orderMainDto));

        return save(orderMainDto);
    }

    @Override
    public OrderMainDto findOrderById(Integer orderId) {
        return conversionUtil.entityToDto(
                repository.findById(orderId)
                .orElseThrow(EntityNotFoundException::new));
    }

    @Override
    public Boolean isExistsByID(Integer orderId) {
        return repository.existsById(orderId);
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
    public Map<String, Integer> getOrderCountByMonth(LocalDate startDate, LocalDate endDate) {
        Map<String, Integer> dateCountMap = new HashMap<>(31);
        LocalDate current = startDate;
        while (!current.isAfter(endDate)) {
            dateCountMap.put(
                String.valueOf(current.getDayOfMonth()),
                repository.countAllByDate(Date.valueOf(current))
            );
            current = current.plusDays(1);
        }
        return dateCountMap;
    }


    @Override
    public Map<String, Integer> getOrderCountByWeek(LocalDate startDate, LocalDate endDate) {
        Map<String, Integer> dateCountMap = new HashMap<>(31);
        LocalDate current = startDate;
        while (!current.isAfter(endDate)) {
            dateCountMap.put(
                String.valueOf(current.getDayOfWeek()),
                repository.countAllByDate(Date.valueOf(current))
            );
            current = current.plusDays(1);
        }
        return dateCountMap;
    }


    @Override
    public Map<String, Integer> getOrderCountByYear(LocalDate startDate, LocalDate endDate) {
        Map<String, Integer> dateCountMap = new HashMap<>(12);
        LocalDate current = startDate;
        int year = current.getYear();
        while (current.getMonthValue() <= endDate.getMonthValue()) {
            dateCountMap.put(
                String.valueOf(current.getMonth()),
                repository.countAllByDateYearAndDateMonth(year,current.getMonthValue())
            );
            current = current.plusMonths(1);
        }
        return dateCountMap;
    }

    @Override
    public void requestCancelOrder(Integer orderId) {
        OrderMainDto orderMainDto = findOrderById(orderId);
        orderMainDto.setStatus(String.valueOf(OrderStatusUtil.REQUEST_CANCEL));
        save(orderMainDto);
    }

    @Override
    public void approveCancelRequest(Integer orderId) {
        OrderMainDto orderMainDto = findOrderById(orderId);
        UserDto userDto = userService.findById(orderMainDto.getUserId());
        orderMainDto.setStatus(String.valueOf(OrderStatusUtil.CANCELED));

        if (!Objects.equals(orderMainDto.getPaymentMethod(), String.valueOf(PaymentMethodUtil.CASH_ON_DELIVERY))) {
            WalletDto walletDto = new WalletDto();
            walletDto.setUserId(userDto.getId());
            walletDto.setDateTime(Date.valueOf(LocalDate.now()));
            walletDto.setTransactionType("Credit");
            walletDto.setAmount(orderMainDto.getAmount());
            walletDto.setDescription("Refund for order " + orderMainDto.getOrderId());
            walletService.save(walletDto);
        }

        save(orderMainDto);
        incrementQuantity(orderMainDto);
    }

    @Override
    public Integer countCancelRequest() {
        return repository.countAllByStatus(String.valueOf(OrderStatusUtil.REQUEST_CANCEL));
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
