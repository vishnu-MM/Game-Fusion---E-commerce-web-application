package com.example.gamefusion.Services.Implementations;

import jakarta.persistence.EntityNotFoundException;
import com.example.gamefusion.Dto.PaginationInfo;
import com.example.gamefusion.Dto.ProductDto;
import com.example.gamefusion.Entity.Images;
import com.example.gamefusion.Entity.Product;
import com.example.gamefusion.Repository.ProductRepository;
import com.example.gamefusion.Services.BrandService;
import com.example.gamefusion.Services.CategoryService;
import com.example.gamefusion.Services.ImagesService;
import com.example.gamefusion.Services.ProductService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryService categoryService;
    private final BrandService brandService;
    private final ImagesService imagesService;

    @Autowired
    public ProductServiceImpl(
            ProductRepository productRepository, @Lazy CategoryService categoryService,
            @Lazy BrandService brandService, @Lazy ImagesService imagesService ) {

        this.productRepository = productRepository;
        this.categoryService = categoryService;
        this.brandService = brandService;
        this.imagesService = imagesService;
    }

    @Override
    public PaginationInfo getAllProducts(Integer pageNo, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by("id"));
        Page<Product> products = productRepository.findAll(pageable);
        List<Product> listOfProducts = products.getContent();
        List<ProductDto> contents = listOfProducts.stream().map(this::entityToDto).toList();

        return new PaginationInfo(
                contents,products.getNumber(),products.getSize(),
                products.getTotalElements(),products.getTotalPages(),
                products.isLast(),products.hasNext()
        );
    }

    @Override
    public PaginationInfo getAllActiveProducts(Integer pageNo, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNo,pageSize);
        Page<Product> products = productRepository.findByStatus(true,pageable);
        List<Product> listOfProducts = products.getContent();
        List<ProductDto> contents = listOfProducts.stream().map(this::entityToDto).toList();

        return new PaginationInfo(
                contents,products.getNumber(),products.getSize(),
                products.getTotalElements(),products.getTotalPages(),
                products.isLast(),products.hasNext()
        );
    }

    @Override
    public Long save(ProductDto dto) {
        Product newProduct = new Product();
        if (dto.getId() != null ) {
            newProduct.setId(dto.getId());
        }
        newProduct.setName(dto.getName());
        newProduct.setDescription(dto.getDescription());
        newProduct.setPrice(dto.getPrice());
        newProduct.setQty(dto.getQty());
        newProduct.setStatus(dto.getStatus());
        newProduct.setCategory(
            categoryService.mapToEntity(
                categoryService.findById(dto.getCategoryId())
            )
        );
        newProduct.setBrand(
            brandService.mapToEntity(
                brandService.findById(dto.getBrandId())
            )
        );
        return productRepository.save(newProduct).getId();
    }

    @Override
    public ProductDto getProductById(Long id) {
        Optional<Product> product = productRepository.findById(id);
        System.out.println(product);
        if (product.isPresent()){
            return entityToDto(product.get());
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
    public ProductDto entityToDto(Product product) {
        List<Long> imageIds = new ArrayList<>();
        for (Images image : product.getImages() )
            imageIds.add(image.getId());

        return new ProductDto(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getQty(),
                product.getBrand().getId(),
                product.getCategory().getId(),
                product.getStatus(),
                imageIds
        );
    }

    @Override
    public Product dtoToEntity(ProductDto dto) {
        List<Images> images = new ArrayList<>();
        for (Long imgId : dto.getImageIds() )
            images.add(
                imagesService.mapToEntity( imagesService.findImageById(imgId) )
            );
        return new Product(
                dto.getId(),
                dto.getName(),
                dto.getDescription(),
                dto.getPrice(),
                dto.getQty(),
                dto.getStatus(),
                brandService.mapToEntity(
                    brandService.findById(dto.getBrandId())
                ),
                categoryService.mapToEntity(
                    categoryService.findById(dto.getCategoryId())
                ),
                images
        );
    }
}
