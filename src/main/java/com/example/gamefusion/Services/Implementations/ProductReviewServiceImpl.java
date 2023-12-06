package com.example.gamefusion.Services.Implementations;

import com.example.gamefusion.Configuration.UtilityClasses.EntityDtoConversionUtil;
import com.example.gamefusion.Dto.PaginationInfo;
import com.example.gamefusion.Dto.ProductDto;
import com.example.gamefusion.Dto.ProductReviewDto;
import com.example.gamefusion.Dto.UserDto;
import com.example.gamefusion.Entity.ProductReview;
import com.example.gamefusion.Repository.ProductReviewRepository;
import com.example.gamefusion.Services.ProductReviewService;
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
public class ProductReviewServiceImpl implements ProductReviewService {
    private final ProductReviewRepository repository;
    private final EntityDtoConversionUtil conversionUtil;
    @Autowired
    public ProductReviewServiceImpl(ProductReviewRepository repository,
                                    EntityDtoConversionUtil conversionUtil) {
        this.repository = repository;
        this.conversionUtil = conversionUtil;
    }

    @Override
    public Boolean isExistsById(Long id) {
        return repository.existsById(id);
    }

    @Override
    public Boolean isExistsByUser(UserDto userDto) {
        return repository.existsByUser(conversionUtil.dtoToEntity(userDto));
    }

    @Override
    public Boolean isExistsByProduct(ProductDto productDto) {
        return repository.existsByProduct(conversionUtil.dtoToEntity(productDto));
    }

    @Override
    public ProductReviewDto findById(Long id) {
        Optional<ProductReview> review = repository.findById(id);
        if (review.isPresent()) return conversionUtil.entityToDto(review.get());
        throw new EntityNotFoundException("Product review with this id, is not present");
    }

    @Override
    public PaginationInfo findByUser(Integer pageNo, Integer pageSize, UserDto userDto) {
        if(isExistsByUser(userDto)) return null;
        Pageable pageable = PageRequest.of(pageNo,pageSize, Sort.by("date").descending());
        Page<ProductReview> reviewsPage = repository.findByUser(conversionUtil.dtoToEntity(userDto),pageable);
        List<ProductReviewDto> contents = reviewsPage.getContent()
                                                   .stream().map(conversionUtil::entityToDto).toList();
        return new PaginationInfo(
                contents,reviewsPage.getNumber(),reviewsPage.getSize(),
                reviewsPage.getTotalElements(),reviewsPage.getTotalPages(),
                reviewsPage.isLast(),reviewsPage.hasNext()
        );
    }

    @Override
    public PaginationInfo findByProduct(Integer pageNo, Integer pageSize, ProductDto productDto) {
        if(isExistsByProduct(productDto)) return null;
        Pageable pageable = PageRequest.of(pageNo,pageSize, Sort.by("date").descending());
        Page<ProductReview> reviewsPage = repository.findByProduct(conversionUtil.dtoToEntity(productDto),pageable);
        List<ProductReviewDto> contents = reviewsPage.getContent()
                .stream().map(conversionUtil::entityToDto).toList();
        return new PaginationInfo(
                contents,reviewsPage.getNumber(),reviewsPage.getSize(),
                reviewsPage.getTotalElements(),reviewsPage.getTotalPages(),
                reviewsPage.isLast(),reviewsPage.hasNext()
        );
    }

    @Override
    public List<ProductReviewDto> findByUser(UserDto userDto) {
        return repository.findByUser(conversionUtil.dtoToEntity(userDto))
                .stream().map(conversionUtil::entityToDto).toList();
    }

    @Override
    public List<ProductReview> findByProduct(ProductDto productDto) {
        return repository.findByProduct(conversionUtil.dtoToEntity(productDto));
    }

    @Override
    public ProductReviewDto save(ProductReviewDto productReviewDto) {
        return conversionUtil.entityToDto(
            repository.save(
                conversionUtil.dtoToEntity(productReviewDto)
            )
        );
    }
}
