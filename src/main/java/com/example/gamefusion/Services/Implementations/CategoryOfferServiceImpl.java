package com.example.gamefusion.Services.Implementations;

import com.example.gamefusion.Configuration.UtilityClasses.EntityDtoConversionUtil;
import com.example.gamefusion.Dto.CategoryDto;
import com.example.gamefusion.Dto.CategoryOfferDto;
import com.example.gamefusion.Entity.CategoryOffer;
import com.example.gamefusion.Repository.CategoryOfferRepository;
import com.example.gamefusion.Services.CategoryOfferService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CategoryOfferServiceImpl implements CategoryOfferService {

    private final CategoryOfferRepository repository;
    private final EntityDtoConversionUtil conversionUtil;
    @Autowired
    public CategoryOfferServiceImpl(CategoryOfferRepository repository,
                                    EntityDtoConversionUtil conversionUtil) {
        this.repository = repository;
        this.conversionUtil = conversionUtil;
    }

    @Override
    public Boolean isExistsById(Integer id) {
        return repository.existsById(id);
    }

    @Override
    public CategoryOfferDto findById(Integer id) {
        Optional<CategoryOffer> categoryOffer = repository.findById(id);
        if (categoryOffer.isPresent()) return conversionUtil.entityToDto(categoryOffer.get());
        throw new EntityNotFoundException("CategoryOffer with this id is not present");
    }

    @Override
    public Boolean isExistsByCategory(CategoryDto categoryDto) {
        return repository.existsByCategory(conversionUtil.dtoToEntity(categoryDto));
    }

    @Override
    public CategoryOfferDto save(CategoryOfferDto categoryOfferDto) {
        return conversionUtil.entityToDto(
            repository.save( conversionUtil.dtoToEntity(categoryOfferDto) )
        );
    }

    @Override
    public CategoryOfferDto findByCategory(CategoryDto categoryDto) {
        if (!isExistsByCategory(categoryDto))
            throw new EntityNotFoundException("CategoryOffer with this category is not present");
        return conversionUtil.entityToDto(
            repository.findByCategory(conversionUtil.dtoToEntity(categoryDto))
        );
    }
}
