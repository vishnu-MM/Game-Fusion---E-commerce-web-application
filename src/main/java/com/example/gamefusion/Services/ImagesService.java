package com.example.gamefusion.Services;

import com.example.gamefusion.Dto.ImagesDto;
import java.util.List;

public interface ImagesService {
    ImagesDto findImageById(Long id);
    List<ImagesDto> findImageByProduct(Long productId);
    List<Long> findImageIdByProductId(Long productId);
    List<byte[]> getImageOfSingleProduct(Long productId);
    void deleteImageById(Long imageId);
    boolean isImageExists(Long imageId);
}
