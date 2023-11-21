package com.example.gamefusion.Services;

import com.example.gamefusion.Dto.OrderMainDto;
import com.example.gamefusion.Dto.PaginationInfo;
import com.example.gamefusion.Dto.UserDto;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Map;

public interface OrderMainService {
    void cancelOrder(Integer orderId);
    Boolean isExistsByID(Integer orderId);
    OrderMainDto findOrderById(Integer orderId);
    OrderMainDto save(OrderMainDto orderMainDto);
    void decrementQuantity(OrderMainDto orderMainDto);
    void incrementQuantity(OrderMainDto orderMainDto);
    Map<String, Integer> getOrderCountByWeek(LocalDate startDate, LocalDate endDate);
    Map<String, Integer> getOrderCountByMonth(LocalDate startDate, LocalDate endDate);
    Map<String, Integer> getOrderCountByYear(LocalDate startDate, LocalDate endDate);
    PaginationInfo findAll(Integer pageNo, Integer pageSize);
    PaginationInfo findAllFilterByDay(Integer pageNo, Integer pageSize, Date date);
    PaginationInfo findOrderByUser(UserDto userDto,Integer pageNo, Integer pageSize);
    PaginationInfo findOrderByStatus(String status, Integer pageNo, Integer pageSize);
    OrderMainDto save(Integer addressId, Integer paymentOption, UserDto userDto, Integer totalAmount);
    PaginationInfo findAllBetweenDay(Integer pageNo, Integer pageSize, Date startDate, Date endDate);
    PaginationInfo findByDayAndStatus(Integer pageNo, Integer pageSize, Date date, String statusFilter);
    PaginationInfo findByDatesBetweenAndStatus(Integer pageNo, Integer pageSize, Date startDate, Date endDate, String status);
}
