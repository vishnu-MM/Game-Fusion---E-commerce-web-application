package com.example.gamefusion.Services.Implementations;

import com.example.gamefusion.Configuration.ExceptionHandlerConfig.EntityNotFoundException;
import com.example.gamefusion.Dto.ImagesDto;
import com.example.gamefusion.Dto.ProductDto;
import com.example.gamefusion.Entity.Images;
import com.example.gamefusion.Entity.Product;
import com.example.gamefusion.Repository.ImagesRepository;
import com.example.gamefusion.Services.*;
import jakarta.servlet.ServletOutputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ImagesServiceImpl implements ImagesService {
    private final ImagesRepository imagesRepository;
    private final ProductService productService;
    private final StorageService storageService;
    private final CategoryService categoryService;
    private final BrandService brandService;
    @Autowired
    public ImagesServiceImpl(
            ImagesRepository imagesRepository, @Lazy ProductService productService, StorageServiceImpl storageService, CategoryService categoryService, BrandService brandService) {
        this.imagesRepository = imagesRepository;
        this.productService = productService;
        this.storageService = storageService;
        this.categoryService = categoryService;
        this.brandService = brandService;
    }

    @Override
    public ImagesDto findImageById(Long imageId) {
        System.out.println("\n\nFrom findImageById() to imagesRepository.findById(imageId " +imageId+ ");");
        Optional<Images> img = imagesRepository.findById(imageId);
        if (img.isPresent()) {
            System.out.println("\n\nFrom findImageById() to ImagesServiceImpl: mapToDto(imageId: " + img.get() + ");");
            return mapToDto(img.get());
        }
        else
            throw new EntityNotFoundException("Image Not Found");
    }

    @Override
    public List<ImagesDto> findImageByProduct(Long productId) {
        ProductDto dto = productService.getProductById(productId);
        List<Images> images = imagesRepository.findByProduct(productService.dtoToEntity(dto));
        if (images.isEmpty()) return null;
        return images.stream().map(this::mapToDto).toList();
    }

    @Override
    public List<Long> findImageIdByProductId(Long productId) {
        List<ImagesDto> imagesDtos = findImageByProduct(productId);
        if (imagesDtos == null) return null;
        List<Long> imageIds = new ArrayList<>();
        for (ImagesDto image : imagesDtos)
            imageIds.add(image.getId());
        return imageIds;
    }

    @Override
    public List<byte[]> getImageOfSingleProduct(Long productId) {
        List<byte[]> images = new ArrayList<>();
        List<Long> imageIds = findImageIdByProductId(productId);
        if (imageIds == null) {
            return null;
        } else {
            try {
                for (Long id : imageIds) {
                    images.add(storageService.downloadImageFromFileSystem(id));
                }
                return images;
            } catch (jakarta.persistence.EntityNotFoundException | IOException e) {
                return null;
            }
        }
    }

    @Override
    public void deleteImageById(Long imageId) {
        imagesRepository.deleteById(imageId);
    }

    @Override
    public boolean isImageExists(Long imageId) {
        return imagesRepository.existsById(imageId);
    }

    @Override
    public ImagesDto mapToDto(Images entity) {
        return new ImagesDto(
                entity.getId(),
                entity.getName(),
                entity.getType(),
                entity.getFilePath(),
                entity.getProduct().getId()
        );
    }

    @Override
    public Images mapToEntity(ImagesDto dto) {
        Images images = new Images();
        if (dto.getId() != null)
            images.setId(dto.getId());
        images.setName(dto.getName());
        images.setType(dto.getType());
        images.setFilePath(dto.getFilePath());
        System.out.println("\nimage service.mapToEntity imagesService: getProductEntity(productId: "+dto.getProductId()+
                ");\n");
        images.setProduct(
                getProductEntity(dto.getProductId())
        );
        System.out.println("getProductEntity(dto.getProductId()) returnd to imageService.maptoentity with "+dto.getProductId()+"\n");
        return images;
    }

    public Product getProductEntity(Long id) {
        System.out.println("He is the reason");
        System.out.println("\nfrom imagesService.getProductEntity() to imagesService: productService.getProductById(" +
                "(productId: "+id+");");
        ProductDto dto = productService.getProductById(id);
        System.out.println("Suspicious person returned with "+dto+"\n\n");
        List<Images> images = new ArrayList<>();
        for (Long imgId : dto.getImageIds())
            images.add(
                    imagesRepository.findById(imgId).orElse(null)
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
