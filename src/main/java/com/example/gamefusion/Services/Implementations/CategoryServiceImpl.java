package com.example.gamefusion.Services.Implementations;

import com.example.gamefusion.Configuration.UtilityClasses.EntityDtoConversionUtil;
import jakarta.persistence.EntityNotFoundException;
import com.example.gamefusion.Dto.CategoryDto;
import com.example.gamefusion.Dto.PaginationInfo;
import com.example.gamefusion.Entity.Category;
import com.example.gamefusion.Repository.CategoryRepository;
import com.example.gamefusion.Services.CategoryService;
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
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final EntityDtoConversionUtil conversionUtil;
    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository, EntityDtoConversionUtil conversionUtil) {
        this.categoryRepository = categoryRepository;
        this.conversionUtil = conversionUtil;
    }

    @Override
    public CategoryDto save(CategoryDto dto) {
        Category category = conversionUtil.dtoToEntity(dto);
        return conversionUtil.entityToDto(categoryRepository.save(category));
    }

    @Override
    public CategoryDto findById(Long id) throws EntityNotFoundException{
        Optional<Category> category = categoryRepository.findById(id);
        if (category.isPresent()) return conversionUtil.entityToDto(category.get());
        else throw new EntityNotFoundException("Category not found");
    }

    @Override
    public PaginationInfo findAll(Integer pageNo, Integer pageSize) {
        Pageable page = PageRequest.of(pageNo, pageSize, Sort.by("id"));
        Page<Category> categories = categoryRepository.findAll(page);
        List<Category> listOfCategories = categories.getContent();
        List<CategoryDto> contents = listOfCategories.stream().map(conversionUtil::entityToDto).toList();

        return new PaginationInfo(
                contents, categories.getNumber(), categories.getSize(),
                categories.getTotalElements(), categories.getTotalPages(),
                categories.isLast(), categories.hasNext()
        );
    }

    @Override
    public Boolean isExistsByName(String name) {
        return categoryRepository.existsByName(name);
    }

    @Override
    public List<CategoryDto> getAll() {
        return categoryRepository.findAll(Sort.by("id"))
                .stream().map(conversionUtil::entityToDto).toList();
    }

    @Override
    public List<String> getAllNames() {
        return categoryRepository.findAllCategoryNames();
    }

    @Override
    @Transactional
    public void activateCategory(Long id) {
        if (isCategoryExist(id) && !isCategoryActive(id)) {
            categoryRepository.setCategoryActive(id);
        }
    }

    @Override
    @Transactional
    public void deActivateCategory(Long id) {
        if (isCategoryExist(id) && isCategoryActive(id)) {
            categoryRepository.setCategoryInactive(id);
        }
    }

    @Override
    public Boolean isCategoryExist(Long id) {
        return categoryRepository.existsById(id);
    }

    @Override
    public Boolean isCategoryActive(Long id) {
        if (isCategoryExist(id))
            return categoryRepository.findStatusById(id);
        else
            throw new EntityNotFoundException("Category not found");
    }
}
