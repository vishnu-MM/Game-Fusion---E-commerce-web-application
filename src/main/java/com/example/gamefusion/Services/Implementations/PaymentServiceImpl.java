package com.example.gamefusion.Services.Implementations;

import com.example.gamefusion.Configuration.UtilityClasses.EntityDtoConversionUtil;
import com.example.gamefusion.Configuration.ExceptionHandlerConfig.EntityNotFound;
import com.example.gamefusion.Configuration.UtilityClasses.PaymentMethodUtil;
import com.example.gamefusion.Dto.PaymentDto;
import com.example.gamefusion.Entity.Payment;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.gamefusion.Repository.PaymentRepository;
import com.example.gamefusion.Services.PaymentService;
import org.springframework.stereotype.Service;
import com.example.gamefusion.Entity.OrderMain;
import com.example.gamefusion.Dto.OrderMainDto;
import com.example.gamefusion.Dto.PaymentDto;
import com.example.gamefusion.Entity.Payment;
import java.time.LocalDate;
import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository repository;
    private final EntityDtoConversionUtil conversionUtil;
    @Autowired
    public PaymentServiceImpl(PaymentRepository repository,
                              EntityDtoConversionUtil conversionUtil) {
        this.repository = repository;
        this.conversionUtil = conversionUtil;
    }

    @Override
    public PaymentDto findByOrderMain(OrderMainDto orderMainDto) {
        if (!existsByOrderMain(orderMainDto))
            throw new EntityNotFound("OrderMain Not Found");
        return conversionUtil.entityToDto(
            repository.findByOrderMain( conversionUtil.dtoToEntity(orderMainDto) )
        );
    }

    @Override
    public PaymentDto save(Integer paymentOption, OrderMainDto orderMainDto) {
        PaymentDto paymentDto = new PaymentDto();
        paymentDto.setPaymentMethod(PaymentMethodUtil.getPaymentMethodByValue(paymentOption));
        paymentDto.setPaymentId("UW"+orderMainDto.getOrderId());
        paymentDto.setOrderId(orderMainDto.getId());
        paymentDto.setAmount(orderMainDto.getAmount());
        paymentDto.setDate(Date.valueOf(LocalDate.now()));
        paymentDto.setPaymentStatus(paymentOption == 1);
        return save(paymentDto);
    }

    @Override
    public Map<Integer, PaymentDto> findByOrder(List<OrderMainDto> orderMainList) {
        Map<Integer, PaymentDto> response = new HashMap<>();
        for (OrderMainDto order: orderMainList) {
            OrderMain orderMain = conversionUtil.dtoToEntity(order);
            if (repository.existsByOrderMain(orderMain))
                response.put(order.getId(), conversionUtil.entityToDto(repository.findByOrderMain(orderMain)));
        }
        return response;
    }

    @Override
    public Boolean existsByOrderMain(OrderMainDto orderMainDto) {
        OrderMain orderMain = conversionUtil.dtoToEntity(orderMainDto);
        return repository.existsByOrderMain(orderMain);
    }

    @Override
    public PaymentDto save(PaymentDto paymentDto) {
        Payment payment = conversionUtil.dtoToEntity(paymentDto);
        payment = repository.save(payment);
        return conversionUtil.entityToDto(payment);
    }

    @Override
    public Boolean isPaymentSuccess(OrderMainDto orderMainDto) {
        return repository.findPaymentStatusByOrderMain(conversionUtil.dtoToEntity(orderMainDto));
    }

    @Override
    public void updatePaymentStatus(OrderMainDto orderMainDto) {
        if (existsByOrderMain(orderMainDto))
            repository.updatePaymentStatus(conversionUtil.dtoToEntity(orderMainDto));
    }
}