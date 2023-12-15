package com.example.gamefusion.Services.Implementations;

import com.example.gamefusion.Configuration.UtilityClasses.EntityDtoConversionUtil;
import com.example.gamefusion.Dto.PaginationInfo;
import com.example.gamefusion.Entity.OrderMain;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.gamefusion.Repository.OrderSubRepository;
import com.example.gamefusion.Services.OrderSubService;
import com.example.gamefusion.Dto.OrderMainDto;
import com.example.gamefusion.Dto.OrderSubDto;
import com.example.gamefusion.Entity.OrderSub;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import com.example.gamefusion.Dto.CartDto;
import com.example.gamefusion.Entity.Cart;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OrderSubServiceImpl implements OrderSubService {
    private final OrderSubRepository repository;
    private final EntityDtoConversionUtil conversionUtil;
    @Autowired
    public OrderSubServiceImpl(OrderSubRepository repository,
                               EntityDtoConversionUtil conversionUtil) {
        this.conversionUtil = conversionUtil;
        this.repository = repository;
    }

    @Override
    public PaginationInfo findAll(Integer pageNo, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNo,pageSize, Sort.by("id").descending());
        Page<OrderSub> page = repository.findAll(pageable);
        return new PaginationInfo(
            page.getContent(), pageNo, pageSize, page.getTotalElements(),
            page.getTotalPages(), page.isLast(), page.hasNext()
        );
    }

    @Override
    public List<OrderSubDto> findOrderByOrder(OrderMainDto orderMainDto) {
        List<OrderSub> orderSub = repository.findByOrderMain( conversionUtil.dtoToEntity(orderMainDto) );
        return orderSub.stream().map(conversionUtil::entityToDto).toList();
    }

    @Override
    public OrderSubDto save(OrderSubDto orderSubDto) {
        OrderSub orderSub = conversionUtil.dtoToEntity(orderSubDto);
        return conversionUtil.entityToDto( repository.save(orderSub) );
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

    @Override
    public Map<Integer, List<OrderSub>> findByOrder(List<OrderMainDto> orderMainList) {
        Map<Integer, List<OrderSub>> response = new HashMap<>();
        for ( OrderMainDto order : orderMainList ) {
            OrderMain orderMain = conversionUtil.dtoToEntity(order);
            if (repository.existsByOrderMain(orderMain))
                response.put(order.getId(), repository.findByOrderMain(orderMain));
        }
        return response;
    }

    @Override
    public List<OrderSub> findByOrder(OrderMainDto orderMain) {
        return repository.findByOrderMain(conversionUtil.dtoToEntity(orderMain));
    }
}
