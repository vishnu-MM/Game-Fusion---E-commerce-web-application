package com.example.gamefusion.Services.Implementations;

import com.example.gamefusion.Configuration.UtilityClasses.EntityDtoConversionUtil;
import com.example.gamefusion.Dto.CategoryDto;
import com.example.gamefusion.Dto.PaginationInfo;
import com.example.gamefusion.Dto.ProductDto;
import com.example.gamefusion.Entity.Brand;
import com.example.gamefusion.Entity.Product;
import com.example.gamefusion.Repository.ProductRepository;
import com.example.gamefusion.Services.ProductService;
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
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final EntityDtoConversionUtil conversionUtil;
    @Autowired
    public ProductServiceImpl(
            ProductRepository productRepository, EntityDtoConversionUtil conversionUtil) {

        this.productRepository = productRepository;
        this.conversionUtil = conversionUtil;
    }

    @Override
    public PaginationInfo getAllProducts(Integer pageNo, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by("id"));
        Page<Product> products = productRepository.findAll(pageable);
        List<Product> listOfProducts = products.getContent();
        List<ProductDto> contents = listOfProducts.stream().map(conversionUtil::entityToDto).toList();
        return new PaginationInfo(
                contents,products.getNumber(),products.getSize(),
                products.getTotalElements(),products.getTotalPages(),
                products.isLast(),products.hasNext()
        );
    }

    @Override
    public PaginationInfo getAllActiveProducts(Integer pageNo, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNo,pageSize);
        Page<Product> products = productRepository.findByStatusAndCategoryStatus(true,true,pageable);
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
        Page<Product> page = productRepository.findByCategoryAndStatusAndCategoryStatus(
                            conversionUtil.dtoToEntity(categoryDto),
                            true, true, pageable
        );
        List<Product> listProduct = page.getContent();
        List<ProductDto> listOfProductDto = listProduct.stream().map(conversionUtil::entityToDto).toList();

        return new PaginationInfo(
                listOfProductDto,pageNo,pageSize,
                page.getTotalElements(),page.getTotalPages(),page.isLast(),page.hasNext()
        );
    }

    @Override
    public Long save(ProductDto dto) {
        Product newProduct = conversionUtil.dtoToEntity(dto);
        return productRepository.save(newProduct).getId();
    }

    @Override
    public ProductDto getProductById(Long id) {
        Optional<Product> product = productRepository.findById(id);
        if (product.isPresent()){
            return conversionUtil.entityToDto(product.get());
        }
        else {
            throw new EntityNotFoundException("Product not fount");
        }
    }

    @Override
    public Boolean isProductActive(Long id) {
        return productRepository.findStatusById(id);
    }

    @Override
    public Boolean isProductExists(Long id) {
        return productRepository.existsById(id);
    }

    @Override
    @Transactional
    public void activateProduct(Long id) {
        if (productRepository.existsById(id) && !isProductActive(id) ){
            productRepository.unBlockProduct(id);
        }
    }

    @Override
    @Transactional
    public void deActivateProduct(Long id) {
        if (productRepository.existsById(id) && isProductActive(id) ){
            productRepository.blockProduct(id);
        }
    }

    @Override
    @Transactional
    public void updateQuantity(Long productID, Integer qty) {
        if (productID == null || qty == null) return;
        productRepository.updateQty(productID,qty);
    }

    @Override
    public Integer getCountByBrand(Brand brand) {
        return productRepository.countAllByBrand(brand);
    }
}
