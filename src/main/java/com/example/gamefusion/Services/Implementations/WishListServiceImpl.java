package com.example.gamefusion.Services.Implementations;

import com.example.gamefusion.Configuration.ExceptionHandlerConfig.EntityNotFound;
import com.example.gamefusion.Configuration.UtilityClasses.EntityDtoConversionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.gamefusion.Repository.WishListRepository;
import com.example.gamefusion.Services.WishListService;
import com.example.gamefusion.Dto.ProductDto;
import org.springframework.stereotype.Service;
import com.example.gamefusion.Dto.WishListDto;
import com.example.gamefusion.Entity.WishList;
import com.example.gamefusion.Dto.UserDto;
import jakarta.transaction.Transactional;
import java.util.Optional;
import java.util.List;

@Service
public class WishListServiceImpl implements WishListService {
    private final WishListRepository repository;
    private final EntityDtoConversionUtil conversionUtil;
    @Autowired
    public WishListServiceImpl(WishListRepository repository,
                               EntityDtoConversionUtil conversionUtil) {
        this.repository = repository;
        this.conversionUtil = conversionUtil;
    }

    @Override
    public void delete(WishListDto wishListDto) {
        repository.delete(conversionUtil.dtoToEntity(wishListDto));
    }

    @Override
    public Boolean isExitsById(Integer id) {
        return repository.existsById(id);
    }

    @Override
    public WishListDto findById(Integer id) {
        Optional<WishList> wishList = repository.findById(id);
        if (wishList.isPresent())
            return conversionUtil.entityToDto(wishList.get());
        throw new EntityNotFound("WishList with this id, Does not exists");
    }

    @Override
    public List<WishListDto> findByUser(UserDto userDto) {
        return repository.findByUser(conversionUtil.dtoToEntity(userDto))
                         .stream().map(conversionUtil::entityToDto).toList();
    }

    @Override
    public Boolean isExistsByProduct(UserDto userDto, ProductDto productDto) {
        return repository.existsByUserAndProduct(
            conversionUtil.dtoToEntity(userDto), conversionUtil.dtoToEntity(productDto)
        );
    }

    @Override
    @Transactional
    public void deleteByProduct(UserDto userDto, ProductDto productDto) {
        repository.deleteByUserAndProduct(conversionUtil.dtoToEntity(userDto),conversionUtil.dtoToEntity(productDto));
    }

    @Override
    @Transactional
    public void deleteByUser(UserDto userDto) {
        repository.deleteByUser(conversionUtil.dtoToEntity(userDto));
    }

    @Override
    @Transactional
    public void deleteById(Integer wishlistId) {
        repository.deleteById(wishlistId);
    }

    @Override
    public Boolean isExistsByUser(UserDto userDto) {
        return repository.existsByUser(conversionUtil.dtoToEntity(userDto));
    }

    @Override
    public WishListDto save(WishListDto wishListDto) {
        return conversionUtil.entityToDto(
            repository.save(conversionUtil.dtoToEntity(wishListDto))
        );
    }
}