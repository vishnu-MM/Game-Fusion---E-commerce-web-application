package com.example.gamefusion.Services;

import com.example.gamefusion.Dto.OrderHistoryDto;
import com.example.gamefusion.Dto.OrderMainDto;

import java.util.List;

public interface OrderHistoryService {
    Boolean isExistsById(Long id);
    OrderHistoryDto findById(Long id);
    Boolean isExistsByOrderId(Integer orderId);
    List<OrderHistoryDto> findByOrderId(Integer orderId);
    OrderHistoryDto save(OrderMainDto orderMainDto);
    Boolean isExistsByOrderId(OrderMainDto orderMain);
    OrderHistoryDto save(OrderHistoryDto orderHistoryDto);
}
