package com.example.gamefusion.Services;

import com.example.gamefusion.Dto.BrandDto;
import com.example.gamefusion.Entity.Brand;

import java.util.List;

public interface BrandService {
    BrandDto findById(Long brandId);

    BrandDto mapToDto(Brand entity);
    Brand mapToEntity(BrandDto dto);

    List<BrandDto> getAll();
}
