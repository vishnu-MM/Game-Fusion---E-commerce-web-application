package com.example.gamefusion.Services;

import com.example.gamefusion.Dto.BrandDto;
import com.example.gamefusion.Dto.PaginationInfo;
import com.example.gamefusion.Entity.Brand;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BrandService {
    List<BrandDto> getAll();
    PaginationInfo getAll(Integer pageNo, Integer pageSize);
    BrandDto findById(Long brandId);
    BrandDto saveOrUpdate(BrandDto brandDto);
    Boolean existsById(Long brandId);
    Boolean existsByName(String name);
    Boolean isActive(Long brandId);
    void updateStatusActive(Long brandId);
    void updateStatusInActive(Long brandId);
}
