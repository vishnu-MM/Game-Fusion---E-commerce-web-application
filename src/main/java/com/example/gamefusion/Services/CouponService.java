package com.example.gamefusion.Services;

import com.example.gamefusion.Dto.CouponDto;
import com.example.gamefusion.Dto.PaginationInfo;

public interface CouponService {
    String generateCoupon();
    void delete(Integer couponId);
    CouponDto findById(Integer id);
    Boolean isExistById(Integer id);

    CouponDto findByCoupon(String couponCode);

    CouponDto save(CouponDto couponDto);
    Boolean isExistByCouponCode(String couponCode);
    PaginationInfo findAll(Integer pageNo, Integer pageSize);
}
