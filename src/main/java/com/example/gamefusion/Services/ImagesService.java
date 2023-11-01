package com.example.gamefusion.Services;

import com.example.gamefusion.Dto.ImagesDto;
import com.example.gamefusion.Entity.Images;

import java.util.List;

public interface ImagesService {

    ImagesDto findImageById(Long id);

    ImagesDto mapToDto(Images entity);
    Images mapToEntity(ImagesDto dto);

    List<ImagesDto> findImageByProduct(Long productId);
    List<Long> findImageIdByProductId(Long productId);

    List<byte[]> getImageOfSingleProduct(Long productId);

    void deleteImageById(Long imageId);

    boolean isImageExists(Long imageId);
}
