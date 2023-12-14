package com.example.gamefusion.Services;

import com.example.gamefusion.Dto.CartDto;
import com.example.gamefusion.Dto.OrderMainDto;
import com.example.gamefusion.Dto.OrderSubDto;
import com.example.gamefusion.Entity.OrderSub;

import java.util.List;
import java.util.Map;

public interface OrderSubService {
    OrderSubDto save(OrderSubDto orderSubDto);
    void save(Integer orderMainId, List<CartDto> cartDtoList);
    List<OrderSubDto> findOrderByOrder(OrderMainDto orderMainDto);
    Map<Integer,List<OrderSub>> findByOrder(List<OrderMainDto> orderMainList);

    List<OrderSub> findByOrder(OrderMainDto orderMain);
}
