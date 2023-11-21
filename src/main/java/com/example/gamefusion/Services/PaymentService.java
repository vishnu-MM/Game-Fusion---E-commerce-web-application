package com.example.gamefusion.Services;

import com.example.gamefusion.Dto.OrderMainDto;
import com.example.gamefusion.Dto.PaymentDto;
import com.example.gamefusion.Entity.OrderMain;

public interface PaymentService {
    PaymentDto findByOrderMain(OrderMainDto orderMainDto);
    Boolean existsByOrderMain(OrderMainDto orderMainDto);
    PaymentDto save(PaymentDto paymentDto);
}
