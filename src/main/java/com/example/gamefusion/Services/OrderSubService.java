package com.example.gamefusion.Services;

import com.example.gamefusion.Dto.CartDto;
import com.example.gamefusion.Dto.OrderMainDto;
import com.example.gamefusion.Dto.OrderSubDto;
import java.util.List;

public interface OrderSubService {
    List<OrderSubDto> findOrderByOrder(OrderMainDto orderMainDto);
    OrderSubDto save(OrderSubDto orderSubDto);

    void save(Integer orderMainId, List<CartDto> cartDtoList);
}
