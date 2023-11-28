package com.example.gamefusion.Services;

import com.example.gamefusion.Dto.UserCouponsDto;
import com.example.gamefusion.Dto.CouponDto;
import com.example.gamefusion.Dto.UserDto;

public interface UserCouponsService {
    UserCouponsDto findById(Integer id);
    Boolean isExistById(Integer id);
    UserCouponsDto findByUser(UserDto userDto);
    UserCouponsDto findByCoupon(CouponDto couponDto);
    UserCouponsDto save(UserCouponsDto userCouponsDto);
    Boolean isExistByUserAndCoupon(UserDto userDto,CouponDto couponDto);
}
