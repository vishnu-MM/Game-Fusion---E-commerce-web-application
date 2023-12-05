package com.example.gamefusion.Services;

import com.example.gamefusion.Dto.OrderMainDto;
import com.example.gamefusion.Dto.PaymentDto;
import com.example.gamefusion.Entity.OrderMain;

public interface PaymentService {
    PaymentDto save(PaymentDto paymentDto);
    Boolean isPaymentSuccess(OrderMainDto orderMainDto);
    void updatePaymentStatus(OrderMainDto orderMainDto);
    Boolean existsByOrderMain(OrderMainDto orderMainDto);
    PaymentDto findByOrderMain(OrderMainDto orderMainDto);
    PaymentDto save(Integer paymentOption, OrderMainDto orderMainDto);
}
