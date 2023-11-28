package com.example.gamefusion.Services.Implementations;

import com.example.gamefusion.Configuration.UtilityClasses.CouponCodeGenerator;
import com.example.gamefusion.Configuration.UtilityClasses.EntityDtoConversionUtil;
import com.example.gamefusion.Dto.CouponDto;
import com.example.gamefusion.Dto.PaginationInfo;
import com.example.gamefusion.Entity.Coupon;
import com.example.gamefusion.Repository.CouponRepository;
import com.example.gamefusion.Services.CouponService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CouponServiceImpl implements CouponService {
    private final EntityDtoConversionUtil conversionUtil;
    private final CouponRepository couponRepository;
    @Autowired
    public CouponServiceImpl(EntityDtoConversionUtil conversionUtil,
                             CouponRepository couponRepository) {
        this.conversionUtil = conversionUtil;
        this.couponRepository = couponRepository;
    }

    @Override
    public CouponDto findById(Integer id) {
        Optional<Coupon> coupon = couponRepository.findById(id);
        if (coupon.isPresent()) return conversionUtil.entityToDto(coupon.get());
        throw new EntityNotFoundException("Coupon With This ID Is Not Found");
    }
    @Override
    public CouponDto findByCoupon(String couponCode) {
        Coupon coupon = couponRepository.findByCouponCode(couponCode);
        if (coupon != null ) return conversionUtil.entityToDto(coupon);
        throw new EntityNotFoundException("Coupon With This ID Is Not Found");
    }

    @Override
    public CouponDto save(CouponDto couponDto) {
        return conversionUtil.entityToDto(
            couponRepository.save(
                conversionUtil.dtoToEntity(couponDto)
            )
        );
    }

    @Override
    public PaginationInfo findAll(Integer pageNo, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNo,pageSize, Sort.by("id"));
        Page<Coupon> couponPage = couponRepository.findAll(pageable);
        List<CouponDto> couponDtoList = couponPage.getContent().stream().map(conversionUtil::entityToDto).toList();
        return new PaginationInfo(
                couponDtoList,couponPage.getNumber(),couponPage.getSize(),
                couponPage.getTotalElements(),couponPage.getTotalPages(),
                couponPage.isLast(),couponPage.hasNext()
        );
    }

    @Override
    public void delete(Integer couponId) {
        couponRepository.deleteById(couponId);
    }

    @Override
    public Boolean isExistById(Integer id) {
        return couponRepository.existsById(id);
    }

    @Override
    public Boolean isExistByCouponCode(String couponCode) {
        return couponRepository.existsByCouponCode(couponCode);
    }

    @Override
    public String generateCoupon() {
        String couponCode = CouponCodeGenerator.generate();
        if (isExistByCouponCode(couponCode)) generateCoupon();
        return couponCode;
    }
}
