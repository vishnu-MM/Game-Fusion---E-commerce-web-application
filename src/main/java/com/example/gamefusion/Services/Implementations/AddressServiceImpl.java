package com.example.gamefusion.Services.Implementations;

import com.example.gamefusion.Configuration.ExceptionHandlerConfig.EntityNotFound;
import com.example.gamefusion.Configuration.UtilityClasses.EntityDtoConversionUtil;
import com.example.gamefusion.Dto.OrderMainDto;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.gamefusion.Repository.AddressRepository;
import com.example.gamefusion.Repository.UserRepository;
import com.example.gamefusion.Services.AddressService;
import org.springframework.stereotype.Service;
import com.example.gamefusion.Dto.AddressDto;
import com.example.gamefusion.Entity.Address;
import jakarta.transaction.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.List;

@Service
public class AddressServiceImpl implements AddressService {
    private final AddressRepository repository;
    private final UserRepository userRepository;
    private final EntityDtoConversionUtil conversionUtil;
    @Autowired
    public AddressServiceImpl(AddressRepository repository,
                              UserRepository userRepository, EntityDtoConversionUtil conversionUtil) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.conversionUtil = conversionUtil;
    }


    @Override
    public AddressDto findById(Integer addressId) {
        Optional<Address> address = repository.findById(addressId);
        if (address.isPresent())
            return conversionUtil.entityToDto(address.get());
        throw new EntityNotFound("Address not fount");
    }

    @Override
    public List<AddressDto> findByUser(Integer userId) {
        if (!userRepository.existsById(userId))
            throw new EntityNotFound("Uer not found for given user id");
        if (!isExistsByUser(userId))
            throw new EntityNotFound("Address not found for given user id");
        List<Address> addresses = repository.findByUserId(userId);
        return addresses.stream().map(conversionUtil::entityToDto).toList();
    }

    @Override
    public List<AddressDto> findByUser(Integer userId, Boolean status) {
        if (!userRepository.existsById(userId))
            throw new EntityNotFound("Uer not found for given user id");
        if (!isExistsByUser(userId))
            throw new EntityNotFound("Address not found for given user id");
        List<Address> addresses = repository.findByUserIdAndStatus(userId, status);
        return addresses.stream().map(conversionUtil::entityToDto).toList();
    }

    @Override
    public Map<Integer, AddressDto> findByOrder(List<OrderMainDto> orderMainList) {
        Map<Integer, AddressDto> response = new HashMap<>();
        for (OrderMainDto order: orderMainList) {
            Optional<Address> address = repository.findById(order.getAddressId());
            address.ifPresent( value ->
                    response.put(order.getId(), conversionUtil.entityToDto(value))
            );
        }
        return response;
    }

    @Override
    public Boolean isExistsByUser(Integer userId) {
        return repository.existsByUserId(userId);
    }

    @Override
    public Boolean isExistById(Integer addressId) {
        return repository.existsById(addressId);
    }

    @Override
    public void save(AddressDto newAddress) {
        Address address = conversionUtil.dtoToEntity(newAddress);
        repository.save(address);
    }

    @Override
    @Transactional
    public void deleteById(Integer addressId) {
        repository.blockAddress(addressId);
    }
}