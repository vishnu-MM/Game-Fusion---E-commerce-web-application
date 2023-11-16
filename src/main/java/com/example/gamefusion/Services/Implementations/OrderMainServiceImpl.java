package com.example.gamefusion.Services.Implementations;

import com.example.gamefusion.Configuration.UtilityClasses.EntityDtoConversionUtil;
import com.example.gamefusion.Dto.OrderMainDto;
import com.example.gamefusion.Dto.PaginationInfo;
import com.example.gamefusion.Dto.UserDto;
import com.example.gamefusion.Entity.OrderMain;
import com.example.gamefusion.Entity.User;
import com.example.gamefusion.Repository.OrderMainRepository;
import com.example.gamefusion.Services.OrderMainService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderMainServiceImpl implements OrderMainService {
    private final OrderMainRepository orderMainRepository;
    private final EntityDtoConversionUtil conversionUtil;
    @Autowired
    public OrderMainServiceImpl(OrderMainRepository orderMainRepository, EntityDtoConversionUtil conversionUtil) {
        this.orderMainRepository = orderMainRepository;
        this.conversionUtil = conversionUtil;
    }

    @Override
    public PaginationInfo findOrderByUser(UserDto userDto, Integer pageNo, Integer pageSize) {
        User user = conversionUtil.dtoToEntity(userDto);

        Pageable pageable = PageRequest.of(pageNo,pageSize, Sort.by("date").descending());
        Page<OrderMain> orderMainPage = orderMainRepository.findByUser( pageable, user);
        List<OrderMain> orderMainList = orderMainPage.getContent();
        List<OrderMainDto> orderMainDtoList = orderMainList.stream().map(conversionUtil::entityToDto).toList();

        return new PaginationInfo(
                orderMainDtoList, orderMainPage.getNumber(), orderMainPage.getSize(),
                orderMainPage.getTotalElements(), orderMainPage.getTotalPages(),
                orderMainPage.isLast(), orderMainPage.hasNext()
        );
    }

    @Override
    public PaginationInfo findAll(Integer pageNo, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNo,pageSize, Sort.by("date").descending());
        Page<OrderMain> orderMainPage = orderMainRepository.findAll( pageable);
        List<OrderMain> orderMainList = orderMainPage.getContent();
//        List<OrderMainDto> orderMainDtoList = orderMainList.stream().map(conversionUtil::entityToDto).toList();

        return new PaginationInfo(
                orderMainList, orderMainPage.getNumber(), orderMainPage.getSize(),
                orderMainPage.getTotalElements(), orderMainPage.getTotalPages(),
                orderMainPage.isLast(), orderMainPage.hasNext()
        );
    }

    @Override
    public OrderMainDto save(OrderMainDto orderMainDto) {
        OrderMain orderMain = orderMainRepository.save(
                conversionUtil.dtoToEntity(orderMainDto));
        return conversionUtil.entityToDto(orderMain);
    }

    @Override
    public OrderMainDto findOrderById(Integer orderId) {
        return conversionUtil.entityToDto(
                orderMainRepository.findById(orderId)
                .orElseThrow(EntityNotFoundException::new));
    }

    @Override
    public Boolean isExistsByID(Integer orderId) {
        return orderMainRepository.existsById(orderId);
    }
}
