package com.example.gamefusion.Services.Implementations;

import com.example.gamefusion.Configuration.ExceptionHandlerConfig.EntityNotFound;
import com.example.gamefusion.Configuration.UtilityClasses.EntityDtoConversionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.gamefusion.Repository.ProductRepository;
import com.example.gamefusion.Services.ProductService;
import org.springframework.data.domain.PageRequest;
import com.example.gamefusion.Dto.PaginationInfo;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.example.gamefusion.Dto.CategoryDto;
import com.example.gamefusion.Entity.Product;
import com.example.gamefusion.Dto.ProductDto;
import com.example.gamefusion.Entity.Brand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import jakarta.transaction.Transactional;
import java.util.Optional;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository repository;
    private final EntityDtoConversionUtil conversionUtil;
    @Autowired
    public ProductServiceImpl(ProductRepository repository,
                              EntityDtoConversionUtil conversionUtil) {

        this.repository = repository;
        this.conversionUtil = conversionUtil;
    }

    @Override
    public PaginationInfo getAllProducts(Integer pageNo, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by("id"));
        Page<Product> products = repository.findAll(pageable);
        List<ProductDto> contents = products.getContent().stream().map(conversionUtil::entityToDto).toList();
        return new PaginationInfo(
                contents,products.getNumber(),products.getSize(),
                products.getTotalElements(),products.getTotalPages(),
                products.isLast(),products.hasNext()
        );
    }

    @Override
    public PaginationInfo getAllActiveProducts(Integer pageNo, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNo,pageSize);
        Page<Product> products = repository.findByStatusAndCategoryStatus(true,true,pageable);
        List<Product> listOfProducts = products.getContent();
        List<ProductDto> contents = listOfProducts.stream().map(conversionUtil::entityToDto).toList();
        return new PaginationInfo(
                contents,products.getNumber(),products.getSize(),
                products.getTotalElements(),products.getTotalPages(),
                products.isLast(),products.hasNext()
        );
    }

    @Override
    public PaginationInfo getAllActiveProductsFromCategory(CategoryDto categoryDto, Integer pageNo, Integer pageSize) {

        Pageable pageable = PageRequest.of(pageNo,pageSize);
        Page<Product> page = repository.findByCategoryAndStatusAndCategoryStatus(
                            conversionUtil.dtoToEntity(categoryDto),
                            true, true, pageable
        );
        List<ProductDto> listOfProductDto = page.getContent().stream().map(conversionUtil::entityToDto).toList();
        return new PaginationInfo(
            listOfProductDto, pageNo, pageSize, page.getTotalElements(),
            page.getTotalPages(),page.isLast(),page.hasNext()
        );
    }

    @Override
    public Long save(ProductDto dto) {
        Product newProduct = conversionUtil.dtoToEntity(dto);
        return repository.save(newProduct).getId();
    }

    @Override
    public ProductDto getProductById(Long id) {
        Optional<Product> product = repository.findById(id);
        if (product.isPresent())
            return conversionUtil.entityToDto(product.get());
        throw new EntityNotFound("Product not fount");
    }

    @Override
    public Boolean isProductActive(Long id) {
        return repository.findStatusById(id);
    }

    @Override
    public Boolean isProductExists(Long id) {
        return repository.existsById(id);
    }

    @Override
    @Transactional
    public void activateProduct(Long id) {
        if (repository.existsById(id) && !isProductActive(id) )
            repository.unBlockProduct(id);
    }

    @Override
    @Transactional
    public void deActivateProduct(Long id) {
        if (repository.existsById(id) && isProductActive(id) )
            repository.blockProduct(id);
    }

    @Override
    @Transactional
    public void updateQuantity(Long productID, Integer qty) {
        if (productID != null && qty != null)
            repository.updateQty(productID,qty);
    }

    @Override
    public Integer getCountByBrand(Brand brand) {
        return repository.countAllByBrand(brand);
    }

    @Override
    public List<ProductDto> search(String search) {
        return repository.searchAllByNameContainsIgnoreCase(search)
                .stream().map(conversionUtil::entityToDto).toList();
    }
}
