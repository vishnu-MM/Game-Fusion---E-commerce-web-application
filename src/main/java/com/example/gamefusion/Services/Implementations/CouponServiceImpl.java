package com.example.gamefusion.Services.Implementations;

import com.example.gamefusion.Configuration.ExceptionHandlerConfig.EntityNotFound;
import com.example.gamefusion.Configuration.UtilityClasses.EntityDtoConversionUtil;
import com.example.gamefusion.Configuration.UtilityClasses.CouponCodeGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.gamefusion.Repository.CouponRepository;
import com.example.gamefusion.Services.CouponService;
import org.springframework.data.domain.PageRequest;
import com.example.gamefusion.Dto.PaginationInfo;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.example.gamefusion.Dto.CouponDto;
import com.example.gamefusion.Entity.Coupon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import java.util.Optional;
import java.util.List;

@Service
public class CouponServiceImpl implements CouponService {
    private final CouponRepository couponRepository;
    private final EntityDtoConversionUtil conversionUtil;
    @Autowired
    public CouponServiceImpl(CouponRepository couponRepository,
                             EntityDtoConversionUtil conversionUtil) {
        this.conversionUtil = conversionUtil;
        this.couponRepository = couponRepository;
    }

    @Override
    public CouponDto findById(Integer id) {
        Optional<Coupon> coupon = couponRepository.findById(id);
        if (coupon.isPresent())
            return conversionUtil.entityToDto(coupon.get());
        throw new EntityNotFound("Coupon With This ID Is Not Found");
    }
    
    @Override
    public CouponDto findByCoupon(String couponCode) {
        Coupon coupon = couponRepository.findByCouponCode(couponCode);
        if (coupon != null )
            return conversionUtil.entityToDto(coupon);
        throw new EntityNotFound("Coupon With This ID Is Not Found");
    }

    @Override
    public CouponDto save(CouponDto couponDto) {
        return conversionUtil.entityToDto(
            couponRepository.save( conversionUtil.dtoToEntity(couponDto) )
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
