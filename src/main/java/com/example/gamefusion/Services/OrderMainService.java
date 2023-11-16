package com.example.gamefusion.Services;

import com.example.gamefusion.Dto.OrderMainDto;
import com.example.gamefusion.Dto.PaginationInfo;
import com.example.gamefusion.Dto.UserDto;

import java.util.List;

public interface OrderMainService {
    PaginationInfo findOrderByUser(UserDto userDto,Integer pageNo, Integer pageSize);
    OrderMainDto save(OrderMainDto orderMainDto);

    OrderMainDto findOrderById(Integer orderId);

    Boolean isExistsByID(Integer orderId);

    PaginationInfo findAll(Integer pageNo, Integer pageSize);
}
