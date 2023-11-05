package com.example.gamefusion.Configuration.UtilityClasses;

import com.example.gamefusion.Dto.*;
import com.example.gamefusion.Entity.*;
import com.example.gamefusion.Repository.ImagesRepository;
import com.example.gamefusion.Services.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

@Component
public class EntityDtoConversionUtil {
    private final ModelMapper mapper;
    private final ImagesRepository imagesRepository;
    @Autowired
    public EntityDtoConversionUtil(ModelMapper mapper, ImagesRepository imagesRepository) {
        this.mapper = mapper;
        this.imagesRepository = imagesRepository;
    }

    //? ENTITY TO DTO

    public ProductDto entityToDto(Product product) {
      ProductDto productDto = mapper.map(product,ProductDto.class);
      List<Long> imageIds=  new ArrayList<>();
      for (Images img : product.getImages()) {
          imageIds.add(img.getId());
      }
      productDto.setImageIds(imageIds);
      return productDto;
    }
    public CategoryDto entityToDto(Category category) {
        return mapper.map(category,CategoryDto.class);
    }
    public BrandDto entityToDto(Brand brand) {
      return mapper.map(brand,BrandDto.class);
    }
    public ImagesDto entityToDto(Images images) {
      return mapper.map(images,ImagesDto.class);
    }
    public UserDto entityToDto(User user) {
      return mapper.map(user,UserDto.class);
    }

    //? DTO TO ENTITY

    public Product dtoToEntity(ProductDto productDto) {
        Product product = mapper.map(productDto,Product.class);
        List<Images> imagesList = new ArrayList<>();
        for (Long id : productDto.getImageIds() )
            imagesList.add( imagesRepository.findById(id).orElse(null) );
        product.setImages(imagesList);
        return product;
    }
    public Category dtoToEntity(CategoryDto categoryDto) {
        return mapper.map(categoryDto,Category.class);
    }
    public Brand dtoToEntity(BrandDto brand) {
        return mapper.map(brand,Brand.class);
    }
    public Images dtoToEntity(ImagesDto images) {
        return mapper.map(images,Images.class);
    }
    public User dtoToEntity(UserDto user) {
        return mapper.map(user,User.class);
    }
}
