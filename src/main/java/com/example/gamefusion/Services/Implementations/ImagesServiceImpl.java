package com.example.gamefusion.Services.Implementations;

import com.example.gamefusion.Configuration.UtilityClasses.EntityDtoConversionUtil;
import com.example.gamefusion.Configuration.ExceptionHandlerConfig.EntityNotFound;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.gamefusion.Repository.ImagesRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import com.example.gamefusion.Dto.ProductDto;
import com.example.gamefusion.Dto.ImagesDto;
import com.example.gamefusion.Entity.Images;
import com.example.gamefusion.Services.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.List;

@Service
public class ImagesServiceImpl implements ImagesService {
    private final ProductService productService;
    private final StorageService storageService;
    private final ImagesRepository imagesRepository;
    private final EntityDtoConversionUtil conversionUtil;
    @Autowired
    public ImagesServiceImpl( ImagesRepository imagesRepository, @Lazy ProductService productService,
                              StorageServiceImpl storageService, EntityDtoConversionUtil conversionUtil) {
        this.productService = productService;
        this.storageService = storageService;
        this.conversionUtil = conversionUtil;
        this.imagesRepository = imagesRepository;
    }

    @Override
    public ImagesDto findImageById(Long imageId) {
        Optional<Images> img = imagesRepository.findById(imageId);
        if (img.isPresent())
            return conversionUtil.entityToDto(img.get());
        throw new EntityNotFound("Image Not Found");
    }

    @Override
    public List<ImagesDto> findImageByProduct(Long productId) {
        ProductDto dto = productService.getProductById(productId);
        List<Images> images = imagesRepository.findByProduct( conversionUtil.dtoToEntity(dto) );
        if (images.isEmpty()) return null;
        return images.stream().map(conversionUtil::entityToDto).toList();
    }

    @Override
    public List<Long> findImageIdByProductId(Long productId) {
        List<ImagesDto> imagesDtoList = findImageByProduct(productId);
        if (imagesDtoList == null) return null;
        List<Long> imageIds = new ArrayList<>();
        for (ImagesDto image : imagesDtoList)
            imageIds.add(image.getId());
        return imageIds;
    }

    @Override
    public List<byte[]> getImageOfSingleProduct(Long productId) {
        List<byte[]> images = new ArrayList<>();
        List<Long> imageIds = findImageIdByProductId(productId);
        if (imageIds == null) return null;
        try {
            for (Long id : imageIds) {
                images.add(storageService.downloadImageFromFileSystem(id));
            }
            return images;
        } catch (EntityNotFound | IOException e) { return null; }
    }

    @Override
    public void deleteImageById(Long imageId) {
        imagesRepository.deleteById(imageId);
    }

    @Override
    public boolean isImageExists(Long imageId) {
        return imagesRepository.existsById(imageId);
    }
}
