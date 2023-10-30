package com.example.gamefusion.Services.Implementations;

import com.example.gamefusion.Configuration.ExceptionHandlerConfig.EntityNotFoundException;
import com.example.gamefusion.Dto.ProductDto;
import com.example.gamefusion.Entity.Images;
import com.example.gamefusion.Entity.Product;
import com.example.gamefusion.Repository.ProductRepository;
import com.example.gamefusion.Services.BrandService;
import com.example.gamefusion.Services.CategoryService;
import com.example.gamefusion.Services.ImagesService;
import com.example.gamefusion.Services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.context.annotation.Lazy;
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
    public Long save(ProductDto dto) {
        Product newProduct = new Product();
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
        if (product.isPresent()){
            return entityToDto(product.get());
        }
        else {
            throw new EntityNotFoundException("Product not fount");
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
