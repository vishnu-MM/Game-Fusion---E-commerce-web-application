package com.example.gamefusion.Services.Implementations;

import com.example.gamefusion.Configuration.UtilityClasses.EntityDtoConversionUtil;
import com.example.gamefusion.Dto.CartDto;
import com.example.gamefusion.Dto.PaginationInfo;
import com.example.gamefusion.Dto.ProductDto;
import com.example.gamefusion.Dto.UserDto;
import com.example.gamefusion.Entity.Cart;
import com.example.gamefusion.Entity.Product;
import com.example.gamefusion.Entity.User;
import com.example.gamefusion.Repository.CartRepository;
import com.example.gamefusion.Services.CartService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final EntityDtoConversionUtil conversionUtil;
    @Autowired
    public CartServiceImpl(CartRepository cartRepository, EntityDtoConversionUtil conversionUtil) {
        this.cartRepository = cartRepository;
        this.conversionUtil = conversionUtil;
    }


    @Override
    public void addToCart(CartDto cart) {
        cartRepository.save(conversionUtil.dtoToEntity(cart));
    }

    @Override
    public PaginationInfo findByUser(UserDto user, Integer pageNo, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNo,pageSize, Sort.by("id"));
        Page<Cart> cartPage = cartRepository.findByUser(conversionUtil.dtoToEntity(user),pageable);
        List<Cart> cartList = cartPage.getContent();
        List<CartDto> cartDtoList = cartList.stream().map(conversionUtil::entityToDto).toList();

        return new PaginationInfo(
                cartDtoList,cartPage.getNumber(),cartPage.getSize(),
                cartPage.getTotalElements(),cartPage.getTotalPages(),
                cartPage.isLast(),cartPage.hasNext()
        );
    }

    @Override
    public List<Cart> findByUser(UserDto user) {
        return cartRepository.findByUserOrderById(conversionUtil.dtoToEntity(user));
    }

    @Override
    public Integer totalAmount(List<Cart> cartList) {
        int totalAmount = 0;
        for ( Cart cart : cartList ) {
            Product product = cart.getProduct();
            System.out.println(product);
            if ( product.getStatus()) {
                int price = product.getPrice();
                int qty = cart.getQty();
                totalAmount += price * qty;
            }
        }
        return totalAmount;
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
    public CartDto findById(Integer cartId) {
        Optional<Cart> cart = cartRepository.findById(cartId);
        if (cart.isPresent())
            return conversionUtil.entityToDto(cart.get());
        else
            throw new EntityNotFoundException("Not Found");
    }

    @Override
    public List<CartDto> getCartByUser(UserDto userDto) {
        List<Cart> cart = cartRepository.findByUserOrderById(conversionUtil.dtoToEntity(userDto));
        return cart.stream().map(conversionUtil::entityToDto).toList();
    }
}
