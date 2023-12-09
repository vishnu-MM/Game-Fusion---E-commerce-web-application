package com.example.gamefusion.Services.Implementations;

import com.example.gamefusion.Configuration.UtilityClasses.EntityDtoConversionUtil;
import com.example.gamefusion.Configuration.ExceptionHandlerConfig.EntityNotFound;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.gamefusion.Repository.CategoryRepository;
import com.example.gamefusion.Services.CategoryService;
import org.springframework.data.domain.PageRequest;
import com.example.gamefusion.Dto.PaginationInfo;
import org.springframework.data.domain.Pageable;
import com.example.gamefusion.Dto.CategoryDto;
import com.example.gamefusion.Entity.Category;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import jakarta.transaction.Transactional;
import java.util.Optional;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository repository;
    private final EntityDtoConversionUtil conversionUtil;
    @Autowired
    public CategoryServiceImpl(CategoryRepository repository,
                               EntityDtoConversionUtil conversionUtil) {
        this.repository = repository;
        this.conversionUtil = conversionUtil;
    }

    @Override
    public CategoryDto findById(Long id) {
        Optional<Category> category = repository.findById(id);
        if (category.isPresent())
            return conversionUtil.entityToDto(category.get());
        throw new EntityNotFound("Category not found");
    }

    @Override
    public PaginationInfo findAll(Integer pageNo, Integer pageSize) {
        Pageable page = PageRequest.of(pageNo, pageSize, Sort.by("id"));
        Page<Category> categories = repository.findAll(page);
        List<CategoryDto> listOfCategories = categories.getContent().stream().map(conversionUtil::entityToDto).toList();
        return new PaginationInfo(
                listOfCategories, categories.getNumber(), categories.getSize(),
                categories.getTotalElements(), categories.getTotalPages(),
                categories.isLast(), categories.hasNext()
        );
    }

    @Override
    public CategoryDto save(CategoryDto dto) {
        Category category = conversionUtil.dtoToEntity(dto);
        return conversionUtil.entityToDto(repository.save(category));
    }

    @Override
    public Boolean isExistsByName(String name) {
        return repository.existsByName(name);
    }

    @Override
    public List<CategoryDto> search(String search) {
        return repository.searchAllByNameContainingIgnoreCase(search)
                .stream().map(conversionUtil::entityToDto).toList();
    }

    @Override
    public List<CategoryDto> searchAvailable(String search) {
        return repository.searchAllByNameContainingIgnoreCaseAndStatus(search,true)
                .stream().map(conversionUtil::entityToDto).toList();
    }

    @Override
    public List<CategoryDto> getAll() {
        return repository.findAll(Sort.by("id"))
                .stream().map(conversionUtil::entityToDto).toList();
    }

    @Override
    public List<CategoryDto> getAllAvailable() {
        return repository.findByStatus(true).stream().map(conversionUtil::entityToDto).toList();
    }

    @Override
    public List<String> getAllNames() {
        return repository.findAllCategoryNames();
    }

    @Override
    @Transactional
    public void activateCategory(Long id) {
        if (isCategoryExist(id) && !isCategoryActive(id)) {
            repository.setCategoryActive(id);
        }
    }

    @Override
    @Transactional
    public void deActivateCategory(Long id) {
        if (isCategoryExist(id) && isCategoryActive(id)) {
            repository.setCategoryInactive(id);
        }
    }

    @Override
    public Boolean isCategoryExist(Long id) {
        return repository.existsById(id);
    }

    @Override
    public Boolean isCategoryActive(Long id) {
        if (isCategoryExist(id))
            return repository.findStatusById(id);
        else
            throw new EntityNotFound("Category not found");
    }
}
