package com.example.gamefusion.Services;

import com.example.gamefusion.Dto.OrderMainDto;
import com.example.gamefusion.Dto.PaymentDto;

import java.util.List;
import java.util.Map;

public interface PaymentService {
    PaymentDto save(PaymentDto paymentDto);
    Boolean isPaymentSuccess(OrderMainDto orderMainDto);
    void updatePaymentStatus(OrderMainDto orderMainDto);
    Boolean existsByOrderMain(OrderMainDto orderMainDto);
    PaymentDto findByOrderMain(OrderMainDto orderMainDto);
    PaymentDto save(Integer paymentOption, OrderMainDto orderMainDto);
    Map<Integer,PaymentDto> findByOrder(List<OrderMainDto> orderMainList);
}
