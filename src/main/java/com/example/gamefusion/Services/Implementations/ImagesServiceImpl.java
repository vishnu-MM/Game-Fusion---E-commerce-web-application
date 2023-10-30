package com.example.gamefusion.Services.Implementations;

import com.example.gamefusion.Configuration.ExceptionHandlerConfig.EntityNotFoundException;
import com.example.gamefusion.Dto.ImagesDto;
import com.example.gamefusion.Entity.Images;
import com.example.gamefusion.Repository.ImagesRepository;
import com.example.gamefusion.Services.ImagesService;
import com.example.gamefusion.Services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class ImagesServiceImpl implements ImagesService {
    private final ImagesRepository imagesRepository;
    private final ProductService productService;

    @Autowired
    public ImagesServiceImpl(
            ImagesRepository imagesRepository, @Lazy ProductService productService) {
        this.imagesRepository = imagesRepository;
        this.productService = productService;
    }

    @Override
    public ImagesDto findImageById(Long id) {
        Optional<Images> img = imagesRepository.findById(id);
        if (img.isPresent())
            return mapToDto(img.get());
        else
            throw new EntityNotFoundException("Image Not Found");
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
        Images images =new Images();
        if (dto.getId() != null)
            images.setId(dto.getId());
        images.setName(dto.getName());
        images.setType(dto.getType());
        images.setFilePath(dto.getFilePath());
        images.setProduct(
            productService.dtoToEntity(
                    productService.getProductById(dto.getProductId())
            )
        );
        return images;
    }
}
