package com.example.gamefusion.Services;

import com.example.gamefusion.Dto.ImagesDto;
import com.example.gamefusion.Entity.Images;

public interface ImagesService {

    ImagesDto findImageById(Long id);

    ImagesDto mapToDto(Images entity);
    Images mapToEntity(ImagesDto dto);
}
