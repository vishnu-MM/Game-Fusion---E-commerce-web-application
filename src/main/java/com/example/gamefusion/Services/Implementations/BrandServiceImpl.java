package com.example.gamefusion.Services.Implementations;


import com.example.gamefusion.Dto.BrandDto;
import com.example.gamefusion.Entity.Brand;
import com.example.gamefusion.Repository.BrandRepository;
import com.example.gamefusion.Services.BrandService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class BrandServiceImpl implements BrandService {

    private final BrandRepository brandRepository;
    @Autowired
    public BrandServiceImpl(BrandRepository brandRepository) {
        this.brandRepository = brandRepository;
    }

    @Override
    public BrandDto findById(Long id) {
        Optional<Brand> brand = brandRepository.findById(id);
        if (brand.isPresent())
            return mapToDto(brand.get());
        else
            throw new EntityNotFoundException("Brand Not Found");
    }

    @Override
    public BrandDto mapToDto(Brand entity) {
        return new BrandDto(
                entity.getId(),
                entity.getName(),
                entity.isStatus(),
                entity.getLogo()
        );
    }

    @Override
    public Brand mapToEntity(BrandDto dto) {
        return new Brand(
                dto.getId(),
                dto.getName(),
                dto.isStatus(),
                dto.getLogo()
        );
    }

    @Override
    public List<BrandDto> getAll() {
        List<Brand> brands = brandRepository.findAll(Sort.by("id"));
        return brands.stream().map(this::mapToDto).toList();
    }


}
