package com.example.gamefusion.Services.Implementations;

import com.example.gamefusion.Configuration.ExceptionHandlerConfig.EntityNotFound;
import com.example.gamefusion.Configuration.UtilityClasses.EntityDtoConversionUtil;
import com.example.gamefusion.Dto.BrandDto;
import com.example.gamefusion.Entity.Category;
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
    public ProductServiceImpl(ProductRepository repository, EntityDtoConversionUtil conversionUtil) {
        this.repository = repository;
        this.conversionUtil = conversionUtil;
    }

    @Override
    public ProductDto getProductById(Long id) {
        Optional<Product> product = repository.findById(id);
        if (product.isPresent())
            return conversionUtil.entityToDto(product.get());
        throw new EntityNotFound("Product not fount");
    }

    @Override
    public PaginationInfo getAllProducts(Integer pageNo, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by("id"));
        Page<Product> products = repository.findAll(pageable);
        List<ProductDto> contents = products.getContent().stream().map(conversionUtil::entityToDto).toList();

        return new PaginationInfo(
            contents, products.getNumber(), products.getSize(), products.getTotalElements(),
            products.getTotalPages(), products.isLast(), products.hasNext()
        );
    }

    @Override
    public PaginationInfo getAllActiveProducts(Integer pageNo, Integer pageSize) {
        Pageable pageable = PageRequest.of( pageNo, pageSize );
        Page<Product> products = repository.findByStatusAndCategoryStatusAndBrandStatus(
                true,true,true, pageable
        );
        List<ProductDto> contents = products.getContent().stream().map(conversionUtil::entityToDto).toList();

        return new PaginationInfo(
                contents, products.getNumber(), products.getSize(), products.getTotalElements(),
                products.getTotalPages(), products.isLast(), products.hasNext()
        );
    }

    @Override
    public PaginationInfo getAllActiveProductsFromCategory(CategoryDto categoryDto, Integer pageNo, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNo,pageSize);
        Page<Product> page = repository.findByCategoryAndBrandStatusAndCategoryStatusAndStatus(
                conversionUtil.dtoToEntity(categoryDto),true, true,true, pageable
        );
        List<ProductDto> listOfProductDto = page.getContent().stream().map(conversionUtil::entityToDto).toList();
        return new PaginationInfo(
                listOfProductDto, pageNo, pageSize, page.getTotalElements(),
                page.getTotalPages(),page.isLast(),page.hasNext()
        );
    }

    @Override
    public PaginationInfo getAllActiveProductsFromBrand(BrandDto brandDto, Integer pageNo, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNo,pageSize);
        Page<Product> page = repository.findByBrandAndBrandStatusAndCategoryStatusAndStatus(
                conversionUtil.dtoToEntity(brandDto), true, true,true, pageable
        );
        List<ProductDto> listOfProductDto = page.getContent().stream().map(conversionUtil::entityToDto).toList();
        return new PaginationInfo(
                listOfProductDto, pageNo, pageSize, page.getTotalElements(),
                page.getTotalPages(),page.isLast(),page.hasNext()
        );
    }

    //* PRODUCT CONDITIONS

    @Override
    public Boolean isProductActive(Long id) {
        return repository.findStatusById(id);
    }

    @Override
    public Boolean isProductExists(Long id) {
        return repository.existsById(id);
    }

    //* PRODUCT UPDATES & SAVE

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
    public Long save(ProductDto dto) {
        Product newProduct = conversionUtil.dtoToEntity(dto);
        return repository.save(newProduct).getId();
    }

    //* PRODUCT COUNT

    @Override
    public Integer getCountByBrand(Brand brand) {
        return repository.countAllByBrandAndBrandStatusAndCategoryStatusAndStatus(
            brand,true,true,true
        );
    }

    @Override
    public Integer getCountByCategory(Category category) {
        return repository.countAllByCategoryAndBrandStatusAndCategoryStatusAndStatus(
            category,true,true,true
        );
    }

    @Override
    public Integer getCountByBrand(BrandDto brand) {
        return getCountByBrand(conversionUtil.dtoToEntity(brand));
    }

    @Override
    public Integer getCountByCategory(CategoryDto category) {
        return getCountByCategory(conversionUtil.dtoToEntity(category));
    }

    @Override
    public Integer getCountByPriceFilter(Integer minPrice, Integer maxPrice) {
        return null;
    }

    @Override
    public Integer getCountByBrandPriceFilter(BrandDto brand, int minPrice, int maxPrice) {
        return repository.countAllByBrandAndBrandStatusAndCategoryStatusAndStatusAndPriceBetween(
            conversionUtil.dtoToEntity(brand),true,true,true,minPrice,maxPrice
        );
    }

    @Override
    public Integer getCountByCategoryPriceFilter(CategoryDto category, int minPrice, int maxPrice) {
        return repository.countAllByCategoryAndBrandStatusAndCategoryStatusAndStatusAndPriceBetween(
            conversionUtil.dtoToEntity(category),true,true,true,minPrice,maxPrice
        );
    }

    //* PRODUCT SEARCH

    @Override
    public List<ProductDto> search(String search) {
        return repository.searchAllByNameContainsIgnoreCase(search)
                .stream().map(conversionUtil::entityToDto).toList();
    }

    @Override
    public PaginationInfo search(Integer pageNo, Integer pageSize,String search) {
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by("id"));

        Page<Product> products = repository.searchAllByNameContainsIgnoreCaseAndStatusAndCategoryStatusAndBrandStatus(
            search,true,true,true, pageable
        );
        List<Product> productList = products.getContent();

        products = repository.searchAllByCategoryNameContainsIgnoreCaseAndStatusAndCategoryStatusAndBrandStatus(
            search,true,true,true, pageable
        );
        productList.addAll(products.getContent());

        products = repository.searchAllByBrandNameContainsIgnoreCaseAndStatusAndCategoryStatusAndBrandStatus(
            search,true,true,true, pageable
        );
        productList.addAll(products.getContent());

        List<ProductDto> contents = productList.stream().map(conversionUtil::entityToDto).toList();
        return new PaginationInfo(
                contents,products.getNumber(),products.getSize(),
                products.getTotalElements(),products.getTotalPages(),
                products.isLast(),products.hasNext()
        );
    }

    //* PRODUCT FILTER

    @Override
    public PaginationInfo filterByPrice(Integer minPrice, Integer maxPrice, Integer pageNo, Integer pageSize) {
        return null;
    }

    @Override
    public PaginationInfo filterByPriceAndBrand(BrandDto brand, int minPrice, int maxPrice, Integer pageNo, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNo,pageSize);
        Page<Product> page = repository.findByBrandAndBrandStatusAndCategoryStatusAndStatusAndPriceBetween(
            conversionUtil.dtoToEntity(brand), true,true,true, minPrice, maxPrice, pageable
        );
        List<ProductDto> listOfProductDto = page.getContent().stream().map(conversionUtil::entityToDto).toList();

        return new PaginationInfo(
            listOfProductDto, pageNo, pageSize, page.getTotalElements(),
            page.getTotalPages(),page.isLast(),page.hasNext()
        );
    }

    @Override
    public PaginationInfo filterByPriceAndCategory(CategoryDto category, int minPrice, int maxPrice, Integer pageNo, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNo,pageSize);
        Page<Product> page = repository.findByCategoryAndBrandStatusAndCategoryStatusAndStatusAndPriceBetween(
            conversionUtil.dtoToEntity(category), true,true,true, minPrice, maxPrice, pageable
        );
        List<ProductDto> listOfProductDto = page.getContent().stream().map(conversionUtil::entityToDto).toList();

        return new PaginationInfo(
            listOfProductDto, pageNo, pageSize, page.getTotalElements(),
            page.getTotalPages(),page.isLast(),page.hasNext()
        );
    }
}