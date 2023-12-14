package com.example.gamefusion.Repository;

import com.example.gamefusion.Entity.Coupon;
import com.example.gamefusion.Entity.User;
import com.example.gamefusion.Entity.UserCoupons;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserCouponsRepository extends JpaRepository<UserCoupons, Integer> {
    UserCoupons findByUser(User user);
    UserCoupons findByCoupon(Coupon coupon);
    Boolean existsByUserAndCoupon(User user, Coupon coupon);
}
