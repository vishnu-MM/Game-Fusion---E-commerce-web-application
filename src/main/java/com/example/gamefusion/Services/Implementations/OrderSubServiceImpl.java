package com.example.gamefusion.Services.Implementations;

import com.example.gamefusion.Configuration.UtilityClasses.EntityDtoConversionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.gamefusion.Repository.OrderSubRepository;
import com.example.gamefusion.Services.OrderSubService;
import com.example.gamefusion.Dto.OrderMainDto;
import com.example.gamefusion.Dto.OrderSubDto;
import com.example.gamefusion.Entity.OrderSub;
import org.springframework.stereotype.Service;
import com.example.gamefusion.Dto.CartDto;
import com.example.gamefusion.Entity.Cart;
import java.util.List;

@Service
public class OrderSubServiceImpl implements OrderSubService {
    private final OrderSubRepository orderSubRepository;
    private final EntityDtoConversionUtil conversionUtil;
    @Autowired
    public OrderSubServiceImpl(OrderSubRepository orderSubRepository,
                               EntityDtoConversionUtil conversionUtil) {
        this.conversionUtil = conversionUtil;
        this.orderSubRepository = orderSubRepository;
    }

    @Override
    public List<OrderSubDto> findOrderByOrder(OrderMainDto orderMainDto) {
        List<OrderSub> orderSub = orderSubRepository.findByOrderMain( conversionUtil.dtoToEntity(orderMainDto) );
        return orderSub.stream().map(conversionUtil::entityToDto).toList();
    }

    @Override
    public OrderSubDto save(OrderSubDto orderSubDto) {
        OrderSub orderSub = conversionUtil.dtoToEntity(orderSubDto);
        return conversionUtil.entityToDto( orderSubRepository.save(orderSub) );
    }

    @Override
    public void save(Integer orderMainId, List<CartDto> cartDtoList) {
        List<Cart> cartList = cartDtoList.stream().map(conversionUtil::dtoToEntity).toList();
        for (Cart cart : cartList) {
            if (cart.getProduct().getStatus()) {
                OrderSubDto orderSubDto = new OrderSubDto();
                orderSubDto.setOrderMainId(orderMainId);
                orderSubDto.setProductId(cart.getProduct().getId());
                orderSubDto.setQty(cart.getQty());
                save(orderSubDto);
            }
        }
    }
}
