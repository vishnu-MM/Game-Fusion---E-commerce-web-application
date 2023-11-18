package com.example.gamefusion.Services.Implementations;

import com.example.gamefusion.Configuration.UtilityClasses.EntityDtoConversionUtil;
import com.example.gamefusion.Dto.AddressDto;
import com.example.gamefusion.Entity.Address;
import com.example.gamefusion.Repository.AddressRepository;
import com.example.gamefusion.Repository.UserRepository;
import com.example.gamefusion.Services.AddressService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AddressServiceImpl implements AddressService {
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final EntityDtoConversionUtil conversionUtil;
    @Autowired
    public AddressServiceImpl(AddressRepository addressRepository,
                              UserRepository userRepository, EntityDtoConversionUtil conversionUtil) {
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
        this.conversionUtil = conversionUtil;
    }

    @Override
    public List<AddressDto> findByUser(Integer userId) {
        if (!userRepository.existsById(userId)) throw new EntityNotFoundException("Uer not found for given user id");
        if (!isExistsByUser(userId)) throw new EntityNotFoundException("Address not found for given user id");
        List<Address> addresses = addressRepository.findByUserId(userId);
        return addresses.stream().map(conversionUtil::entityToDto).toList();
    }

    @Override
    public List<AddressDto> findByUser(Integer userId, Boolean status) {
        if (!userRepository.existsById(userId)) throw new EntityNotFoundException("Uer not found for given user id");
        if (!isExistsByUser(userId)) throw new EntityNotFoundException("Address not found for given user id");
        List<Address> addresses = addressRepository.findByUserIdAndStatus(userId, status);
        return addresses.stream().map(conversionUtil::entityToDto).toList();
    }

    @Override
    public Boolean isExistsByUser(Integer userId) {
        return addressRepository.existsByUserId(userId);
    }

    @Override
    public Boolean isExistById(Integer addressId) {
        return addressRepository.existsById(addressId);
    }

    @Override
    public void save(AddressDto newAddress) {
        Address address = conversionUtil.dtoToEntity(newAddress);
        addressRepository.save(address);
    }

    @Override
    public AddressDto findById(Integer addressId) {
        Optional<Address> address = addressRepository.findById(addressId);
        if (address.isPresent())
            return conversionUtil.entityToDto(address.get());
        else
            throw new EntityNotFoundException("Address not fount");
    }

    @Override
    @Transactional
    public void deleteById(Integer addressId) {
        addressRepository.blockAddress(addressId);
    }
}
