package com.example.gamefusion.Services.Implementations;

import com.example.gamefusion.Configuration.UtilityClasses.EntityDtoConversionUtil;
import com.example.gamefusion.Dto.CouponDto;
import com.example.gamefusion.Dto.UserCouponsDto;
import com.example.gamefusion.Dto.UserDto;
import com.example.gamefusion.Entity.UserCoupons;
import com.example.gamefusion.Repository.UserCouponsRepository;
import com.example.gamefusion.Services.UserCouponsService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class UserCouponsServiceImpl implements UserCouponsService {
    private final UserCouponsRepository repository;
    private final EntityDtoConversionUtil conversionUtil;
    @Autowired
    public UserCouponsServiceImpl(UserCouponsRepository repository,
                                  EntityDtoConversionUtil conversionUtil) {
        this.repository = repository;
        this.conversionUtil = conversionUtil;
    }

    @Override
    public UserCouponsDto findById(Integer id) {
        Optional<UserCoupons> userCoupons = repository.findById(id);
        if (userCoupons.isPresent()) return conversionUtil.entityToDto(userCoupons.get());
        throw new EntityNotFoundException("UserCoupons Not Fount");
    }

    @Override
    public Boolean isExistById(Integer id) {
        return repository.existsById(id);
    }

    @Override
    public UserCouponsDto findByUser(UserDto userDto) {
        UserCoupons userCoupons = repository.findByUser(conversionUtil.dtoToEntity(userDto));
        if (userCoupons != null) return conversionUtil.entityToDto(userCoupons);
        throw new EntityNotFoundException("UserCoupons Not Fount");
    }

    @Override
    public UserCouponsDto findByCoupon(CouponDto couponDto) {
        UserCoupons userCoupons = repository.findByCoupon(conversionUtil.dtoToEntity(couponDto));
        if (userCoupons != null) return conversionUtil.entityToDto(userCoupons);
        throw new EntityNotFoundException("UserCoupons Not Fount");
    }

    @Override
    public UserCouponsDto save(UserCouponsDto userCouponsDto) {
        return conversionUtil.entityToDto(
            repository.save(
                conversionUtil.dtoToEntity(
                    userCouponsDto
                )
            )
        );
    }

    @Override
    public Boolean isExistByUserAndCoupon(UserDto userDto, CouponDto couponDto) {
        return repository.existsByUserAndCoupon(
            conversionUtil.dtoToEntity(userDto),conversionUtil.dtoToEntity(couponDto)
        );
    }
}
