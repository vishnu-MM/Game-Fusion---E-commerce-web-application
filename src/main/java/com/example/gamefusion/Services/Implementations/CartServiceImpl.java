package com.example.gamefusion.Services.Implementations;

import com.example.gamefusion.Configuration.UtilityClasses.EntityDtoConversionUtil;
import com.example.gamefusion.Configuration.ExceptionHandlerConfig.EntityNotFound;
import com.example.gamefusion.Repository.CategoryOfferRepository;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.gamefusion.Repository.CartRepository;
import com.example.gamefusion.Services.CartService;
import org.springframework.data.domain.PageRequest;
import com.example.gamefusion.Dto.PaginationInfo;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.example.gamefusion.Dto.ProductDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import com.example.gamefusion.Dto.CartDto;
import com.example.gamefusion.Dto.UserDto;
import jakarta.transaction.Transactional;
import com.example.gamefusion.Entity.*;
import java.time.LocalDate;
import java.util.Optional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final CategoryOfferRepository categoryOfferRepository;
    private final EntityDtoConversionUtil conversionUtil;
    @Autowired
    public CartServiceImpl(CartRepository cartRepository,
                           EntityDtoConversionUtil conversionUtil,
                           CategoryOfferRepository categoryOfferRepository) {
        this.cartRepository = cartRepository;
        this.conversionUtil = conversionUtil;
        this.categoryOfferRepository = categoryOfferRepository;
    }

    @Override
    public CartDto findById(Integer cartId) {
        Optional<Cart> cart = cartRepository.findById(cartId);
        if (cart.isPresent())
            return conversionUtil.entityToDto(cart.get());
        else
            throw new EntityNotFound("Not Found");
    }

    @Override
    public PaginationInfo findByUser(UserDto user, Integer pageNo, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNo,pageSize, Sort.by("id"));
        Page<Cart> cartPage = cartRepository.findByUser(conversionUtil.dtoToEntity(user),pageable);
        List<CartDto> cartList = cartPage.getContent().stream().map(conversionUtil::entityToDto).toList();
        return new PaginationInfo(
                cartList,cartPage.getNumber(),cartPage.getSize(),
                cartPage.getTotalElements(),cartPage.getTotalPages(),
                cartPage.isLast(),cartPage.hasNext()
        );
    }

    @Override
    public List<Cart> findByUser(UserDto user) {
        return cartRepository.findByUser(conversionUtil.dtoToEntity(user));
    }

    @Override
    public List<CartDto> findAvailableProductsByUser(UserDto userDto) {
        User user = conversionUtil.dtoToEntity(userDto);
        return cartRepository.findByUserAndProductStatusAndPositiveProductQty(user,true)
                             .stream().map(conversionUtil::entityToDto).toList();
    }


    @Override
    public List<CartDto> getCartByUser(UserDto userDto) {
        List<Cart> cart = cartRepository.findByUser(conversionUtil.dtoToEntity(userDto));
        return cart.stream().map(conversionUtil::entityToDto).toList();
    }

    @Override
    public Map<Long,Map<String,Double>> getOfferCount(List<CartDto> cartListDto) {
        List<Cart> cartList = cartListDto.stream().map(conversionUtil::dtoToEntity).toList();
        Map<Long,Map<String,Double>> offerList = new HashMap<>();
        for ( Cart cart : cartList ) {
            Product product = cart.getProduct();
            if (!product.getStatus()) continue;

            if (product.getDiscountPrice() != product.getPrice()){
                Double discount =   ((product.getPrice() - (double) product.getDiscountPrice()) / product.getPrice()) * 100;
                offerList.put(product.getId(),Map.of("Product Offer", discount));
            }

            if(getFinalDiscount(product) != 0.0){
                offerList.put(product.getId(),Map.of("Category Offer", getFinalDiscount(product)));
            }
        }
        return offerList;
    }


    @Override
    public Integer totalAmount(List<CartDto> cartListDto) {
        List<Cart> cartList = cartListDto.stream().map(conversionUtil::dtoToEntity).toList();
        int totalAmount = 0;
        for ( Cart cart : cartList ) {
            Product product = cart.getProduct();
            if (!product.getStatus())
                continue;
            int price = product.getDiscountPrice();
            price = (int) (price - ((getFinalDiscount(product) / 100) * price));
            int qty = cart.getQty();
            totalAmount += price * qty;
        }
        return totalAmount;
    }

    private Double getFinalDiscount(Product product) {
        int price = product.getDiscountPrice();
        Category category = product.getCategory();
        if (!categoryOfferRepository.existsByCategory(category))
            return 0.0;

        CategoryOffer Offer = categoryOfferRepository.findByCategory(category);
        if (price < Offer.getMinimumAmount() ||
            LocalDate.now().isAfter(Offer.getExpiryDate().toLocalDate()))
            return 0.0;

        return Offer.getDiscount();
    }

    @Override
    public CartDto findByUserAndProduct(UserDto userDto, ProductDto productDto) {
        User user = conversionUtil.dtoToEntity(userDto);
        Product product = conversionUtil.dtoToEntity(productDto);
        return conversionUtil.entityToDto(cartRepository.findByUserAndProduct(user,product));
    }

    @Override
    public Boolean isExistsByProduct(UserDto user, ProductDto product) {
        return cartRepository.existsByUserAndProduct(
            conversionUtil.dtoToEntity(user), conversionUtil.dtoToEntity(product));
    }

    @Override
    public Boolean isExistsById(Integer id) {
        return cartRepository.existsById(id);
    }

    @Override
    public void deleteById(Integer id) {
        cartRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteByUser(UserDto user) {
        cartRepository.deleteByUser(conversionUtil.dtoToEntity(user));
    }

    @Override
    public void addToCart(CartDto cart) {
        cartRepository.save(conversionUtil.dtoToEntity(cart));
    }
}