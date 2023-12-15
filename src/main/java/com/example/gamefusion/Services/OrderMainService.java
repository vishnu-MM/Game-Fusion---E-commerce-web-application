package com.example.gamefusion.Services;

import com.example.gamefusion.Dto.OrderMainDto;
import com.example.gamefusion.Dto.PaginationInfo;
import com.example.gamefusion.Dto.UserDto;
import com.example.gamefusion.Entity.OrderMain;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface OrderMainService {
    List<OrderMain> findAll();
    Integer countCancelRequest();
    Boolean isExistsByID(Integer orderId);
    Boolean isExistsByUser(UserDto userDto);
    OrderMainDto findOrderById(Integer orderId);
    OrderMainDto save(OrderMainDto orderMainDto);
    OrderMainDto requestCancelOrder(Integer orderId);
    OrderMainDto rejectCancelRequest(Integer orderId);
    void decrementQuantity(OrderMainDto orderMainDto);
    void incrementQuantity(OrderMainDto orderMainDto);
    OrderMainDto approveCancelRequest(Integer orderId);
    List<OrderMainDto> findOrderByUser(UserDto userDto);
    PaginationInfo findAll(Integer pageNo, Integer pageSize);
    PaginationInfo findAllFilterByDay(Integer pageNo, Integer pageSize, Date date);
    Map<String, Integer> getOrderCountByWeek(LocalDate startDate, LocalDate endDate);
    Map<String, Integer> getOrderCountByYear(LocalDate startDate, LocalDate endDate);
    PaginationInfo findOrderByUser(UserDto userDto,Integer pageNo, Integer pageSize);
    PaginationInfo findOrderByStatus(String status, Integer pageNo, Integer pageSize);
    Map<String, Integer> getOrderCountByMonth(LocalDate startDate, LocalDate endDate);
    PaginationInfo findAllBetweenDay(Integer pageNo, Integer pageSize, Date startDate, Date endDate);
    PaginationInfo findByDayAndStatus(Integer pageNo, Integer pageSize, Date date, String statusFilter);
    OrderMainDto save(Integer addressId, Integer paymentOption, UserDto userDto, Integer totalAmount,Double discount);
    PaginationInfo findByDatesBetweenAndStatus(Integer pageNo, Integer pageSize, Date startDate, Date endDate, String status);

}
