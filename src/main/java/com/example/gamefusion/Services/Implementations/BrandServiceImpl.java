package com.example.gamefusion.Services.Implementations;


import com.example.gamefusion.Configuration.UtilityClasses.EntityDtoConversionUtil;
import com.example.gamefusion.Dto.BrandDto;
import com.example.gamefusion.Dto.PaginationInfo;
import com.example.gamefusion.Entity.Brand;
import com.example.gamefusion.Repository.BrandRepository;
import com.example.gamefusion.Services.BrandService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BrandServiceImpl implements BrandService {

    private final BrandRepository brandRepository;
    private final EntityDtoConversionUtil conversionUtil;
    @Autowired
    public BrandServiceImpl(BrandRepository brandRepository, EntityDtoConversionUtil conversionUtil) {
        this.brandRepository = brandRepository;
        this.conversionUtil = conversionUtil;
    }

    @Override
    public BrandDto findById(Long id) {
        Optional<Brand> brand = brandRepository.findById(id);
        if (brand.isPresent())
            return conversionUtil.entityToDto(brand.get());
        else
            throw new EntityNotFoundException("Brand Not Found");
    }

    @Override
    public BrandDto saveOrUpdate(BrandDto brandDto) {
        return (brandDto == null) ? null :
            conversionUtil.entityToDto(
                brandRepository.save(
                    conversionUtil.dtoToEntity(brandDto)
                )
            );
    }

    @Override
    public Boolean existsById(Long brandId) {
        return brandRepository.existsById(brandId);
    }

    @Override
    public Boolean existsByName(String name) {
        return brandRepository.existsByName(name);
    }

    @Override
    public Boolean isActive(Long brandId) {
        return brandRepository.findStatusById(brandId);
    }

    @Override
    @Transactional
    public void updateStatusActive(Long brandId) {
        if (existsById(brandId)) brandRepository.inActivate(brandId);
        else throw new EntityNotFoundException("Brand not found");
    }

    @Override
    @Transactional
    public void updateStatusInActive(Long brandId) {
        if (existsById(brandId)) brandRepository.activate(brandId);
        else throw new EntityNotFoundException("Brand not found");
    }

    @Override
    public List<BrandDto> getAll() {
        List<Brand> brands = brandRepository.findAll(Sort.by("id"));
        return brands.stream().map(conversionUtil::entityToDto).toList();
    }

    @Override
    public PaginationInfo getAll(Integer pageNo, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNo,pageSize,Sort.by("id"));
        Page<Brand> brandPage = brandRepository.findAll(pageable);
        List<BrandDto> brandDtoList = brandPage.getContent()
                                    .stream().map(conversionUtil::entityToDto).toList();
        return new PaginationInfo(
                brandDtoList,brandPage.getNumber(),brandPage.getSize(),
                brandPage.getTotalElements(),brandPage.getTotalPages(),
                brandPage.isLast(),brandPage.hasNext()
        );
    }
}
