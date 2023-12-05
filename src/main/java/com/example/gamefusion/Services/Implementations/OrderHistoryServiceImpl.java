package com.example.gamefusion.Services.Implementations;

import com.example.gamefusion.Configuration.UtilityClasses.EntityDtoConversionUtil;
import com.example.gamefusion.Repository.OrderHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.gamefusion.Services.OrderHistoryService;
import com.example.gamefusion.Services.OrderMainService;
import jakarta.persistence.EntityNotFoundException;
import com.example.gamefusion.Entity.OrderHistory;
import com.example.gamefusion.Dto.OrderHistoryDto;
import com.example.gamefusion.Dto.OrderMainDto;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class OrderHistoryServiceImpl implements OrderHistoryService {
    private final OrderHistoryRepository repository;
    private final OrderMainService orderMainService;
    private final EntityDtoConversionUtil conversionUtil;
    @Autowired
    public OrderHistoryServiceImpl(OrderHistoryRepository repository,
                                   OrderMainService orderMainService,
                                   EntityDtoConversionUtil conversionUtil) {
        this.repository = repository;
        this.conversionUtil = conversionUtil;
        this.orderMainService = orderMainService;
    }

    @Override
    public OrderHistoryDto findById(Long id) {
        Optional<OrderHistory> orderHistory = repository.findById(id);
        if (orderHistory.isPresent())
            return conversionUtil.entityToDto(orderHistory.get());
        throw new EntityNotFoundException("OrderHistory entity with this id is not present");
    }

    @Override
    public List<OrderHistoryDto> findByOrderId(Integer orderId) {
        if (!orderMainService.isExistsByID(orderId))
            throw new EntityNotFoundException("OrderMain entity with this OrderId is not present");
        if (!isExistsByOrderId(orderId))
            throw new EntityNotFoundException("OrderHistory entity with this OrderId is not present");
        return repository.findByOrder(
                conversionUtil.dtoToEntity( orderMainService.findOrderById(orderId) ), Sort.by("id").ascending()
        ).stream().map(conversionUtil::entityToDto).toList();
    }

    @Override
    public OrderHistoryDto save(OrderMainDto orderMainDto) {
        OrderHistoryDto historyDto = new OrderHistoryDto();
        historyDto.setOrderId(orderMainDto.getId());
        historyDto.setOrderStatus(orderMainDto.getStatus());
        historyDto.setDate(Date.valueOf(LocalDate.now()));
        return save(historyDto);
    }

    @Override
    public OrderHistoryDto save(OrderHistoryDto orderHistoryDto) {
        return conversionUtil.entityToDto(
            repository.save(
                conversionUtil.dtoToEntity( orderHistoryDto )
            )
        );
    }

    @Override
    public Boolean isExistsById(Long id) {
        return repository.existsById(id);
    }

    @Override
    public Boolean isExistsByOrderId(Integer orderId) {
        return (orderMainService.isExistsByID(orderId))?
                isExistsByOrderId(orderMainService.findOrderById(orderId)) : false;
    }

    @Override
    public Boolean isExistsByOrderId(OrderMainDto orderMain) {
        return repository.existsByOrder(conversionUtil.dtoToEntity(orderMain));
    }
}
