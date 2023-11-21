package com.example.gamefusion.Services.Implementations;

import com.example.gamefusion.Configuration.UtilityClasses.EntityDtoConversionUtil;
import com.example.gamefusion.Dto.OrderMainDto;
import com.example.gamefusion.Dto.PaymentDto;
import com.example.gamefusion.Entity.OrderMain;
import com.example.gamefusion.Entity.Payment;
import com.example.gamefusion.Repository.PaymentRepository;
import com.example.gamefusion.Services.PaymentService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final EntityDtoConversionUtil conversionUtil;
    @Autowired
    public PaymentServiceImpl(PaymentRepository paymentRepository,
                              EntityDtoConversionUtil conversionUtil) {
        this.paymentRepository = paymentRepository;
        this.conversionUtil = conversionUtil;
    }

    @Override
    public PaymentDto findByOrderMain(OrderMainDto orderMainDto) {
        if (existsByOrderMain(orderMainDto))
            return conversionUtil.entityToDto(
                paymentRepository.findByOrderMain(
                    conversionUtil.dtoToEntity(orderMainDto)
                )
            );
        else throw new EntityNotFoundException("OrderMain Not Found");
    }

    @Override
    public Boolean existsByOrderMain(OrderMainDto orderMainDto) {
        OrderMain orderMain = conversionUtil.dtoToEntity(orderMainDto);
        return paymentRepository.existsByOrderMain(orderMain);
    }

    @Override
    public PaymentDto save(PaymentDto paymentDto) {
        Payment payment = conversionUtil.dtoToEntity(paymentDto);
        payment = paymentRepository.save(payment);
        return conversionUtil.entityToDto(payment);
    }
}
