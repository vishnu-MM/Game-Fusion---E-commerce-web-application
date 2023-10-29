package com.example.gamefusion.Services.Implementations;

import com.example.gamefusion.Configuration.ExceptionHandlerConfig.EntityNotFoundException;
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

    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public void save(CategoryDto dto) {
        Category category = categoryRepository.findById(dto.getId()).orElse(null);
        if (category == null)
            category = new Category();

        category.setName(dto.getName());
        category.setStatus(dto.getStatus());
        if (dto.getParentId() != null)
            category.setParentCategory(categoryRepository.findById(dto.getParentId()).orElse(null));
        else
            category.setParentCategory(null);

        categoryRepository.save(category);
    }

    @Override
    public CategoryDto findById(Long id) {
        Optional<Category> category = categoryRepository.findById(id);
        if (category.isPresent()) {
            Long parentId = null;
            if (category.get().getParentCategory() != null)
                parentId = category.get().getParentCategory().getId();
            return new CategoryDto(
                    category.get().getId(),
                    category.get().getName(),
                    category.get().getStatus(),
                    parentId
            );
        } else {
            throw new EntityNotFoundException("Category not found");
        }
    }

    @Override
    public PaginationInfo findAll(Integer pageNo, Integer pageSize) {
        Pageable page = PageRequest.of(pageNo, pageSize, Sort.by("id"));
        Page<Category> categories = categoryRepository.findAll(page);
        List<Category> listOfCategories = categories.getContent();
        List<CategoryDto> contents = listOfCategories.stream().map(this::mapToDto).toList();

        return new PaginationInfo(
                contents, categories.getNumber(), categories.getSize(),
                categories.getTotalElements(), categories.getTotalPages(),
                categories.isLast(), categories.hasNext());
    }

    @Override
    public List<CategoryDto> getAll() {
        return categoryRepository.findAll(Sort.by("id"))
                .stream().map(this::mapToDto).toList();
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

    private CategoryDto mapToDto(Category category) {
        CategoryDto dto = new CategoryDto();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setStatus(category.getStatus());
        if (category.getParentCategory() != null)
            dto.setParentId(category.getParentCategory().getId());
        return dto;
    }
}
