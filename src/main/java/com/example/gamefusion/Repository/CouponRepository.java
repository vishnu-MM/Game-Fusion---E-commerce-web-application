package com.example.gamefusion.Repository;

import com.example.gamefusion.Entity.Coupon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CouponRepository extends JpaRepository<Coupon,Integer> {
    Coupon findByCouponCode(String couponCode);
    Boolean existsByCouponCode(String couponCode);
    Page<Coupon> searchAllByCouponCodeContainingIgnoreCase(String couponCode, Pageable pageable);
}
