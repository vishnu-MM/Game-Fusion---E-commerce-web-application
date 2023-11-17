package com.example.gamefusion.Services.Implementations;

import com.example.gamefusion.Configuration.UtilityClasses.EntityDtoConversionUtil;
import com.example.gamefusion.Dto.OrderMainDto;
import com.example.gamefusion.Dto.OrderSubDto;
import com.example.gamefusion.Entity.OrderSub;
import com.example.gamefusion.Repository.OrderSubRepository;
import com.example.gamefusion.Services.OrderSubService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class OrderSubServiceImpl implements OrderSubService {
    private final OrderSubRepository orderSubRepository;
    private final EntityDtoConversionUtil conversionUtil;
    @Autowired
    public OrderSubServiceImpl(OrderSubRepository orderSubRepository, EntityDtoConversionUtil conversionUtil) {
        this.orderSubRepository = orderSubRepository;
        this.conversionUtil = conversionUtil;
    }

    @Override
    public List<OrderSubDto> findOrderByOrder(OrderMainDto orderMainDto) {
        List<OrderSub> orderSub = orderSubRepository.findByOrderMain(
                                    conversionUtil.dtoToEntity(orderMainDto));
        return orderSub.stream().map(conversionUtil::entityToDto).toList();
    }

    @Override
    public OrderSubDto save(OrderSubDto orderSubDto) {
        OrderSub orderSub = conversionUtil.dtoToEntity(orderSubDto);
        System.out.println(orderSub);
        return conversionUtil.entityToDto(
                orderSubRepository.save(orderSub)
        );
    }
}