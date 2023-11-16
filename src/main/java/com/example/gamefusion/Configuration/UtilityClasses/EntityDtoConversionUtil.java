package com.example.gamefusion.Configuration.UtilityClasses;

import com.example.gamefusion.Dto.*;
import com.example.gamefusion.Entity.*;
import com.example.gamefusion.Repository.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

@Component
public class EntityDtoConversionUtil {
    private final ModelMapper mapper;
    private final ImagesRepository imagesRepository;
    private final AddressRepository addressRepository;
    private final ProductRepository productRepository;
    private final OrderSubRepository orderSubRepository;
    private final OrderMainRepository orderMainRepository;
    @Autowired
    public EntityDtoConversionUtil(ModelMapper mapper, ImagesRepository imagesRepository, AddressRepository addressRepository, ProductRepository productRepository, OrderSubRepository orderSubRepository, OrderMainRepository orderMainRepository) {
        this.mapper = mapper;
        this.imagesRepository = imagesRepository;
        this.addressRepository = addressRepository;
        this.productRepository = productRepository;
        this.orderSubRepository = orderSubRepository;
        this.orderMainRepository = orderMainRepository;
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
        UserDto userDto = mapper.map(user,UserDto.class);
        if (user.getAddressList() != null) {
            List<Integer> addressIds =  new ArrayList<>();
            for (Address address : user.getAddressList()) {
                addressIds.add(address.getId());
            }
            userDto.setAddressId(addressIds);
        }
        return userDto;
    }
    public AddressDto entityToDto(Address address) {
      return mapper.map(address,AddressDto.class);
    }
    public CartDto entityToDto(Cart cart) {
      return mapper.map(cart,CartDto.class);
    }
    public OrderMainDto entityToDto(OrderMain orderMain) {
      return mapper.map(orderMain,OrderMainDto.class);
    }
    public OrderSubDto entityToDto(OrderSub orderSub) {
      return mapper.map(orderSub,OrderSubDto.class);
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
    public User dtoToEntity(UserDto userDto) {
        User user = mapper.map(userDto,User.class);
        List<Address> addressList = new ArrayList<>();
        if (userDto.getAddressId() != null) {
            for (Integer id : userDto.getAddressId()) {
                addressList.add(addressRepository.findById(id).orElse(null));
            }
            user.setAddressList(addressList);
        }
        return user;
    }
    public Address dtoToEntity(AddressDto addressDto) {
        return mapper.map(addressDto,Address.class);
    }
    public Cart dtoToEntity(CartDto cartDto) {
        Cart cart = mapper.map(cartDto,Cart.class);
        Product product = cart.getProduct();
        product = productRepository.findById(product.getId()).orElse(null);
        cart.setProduct(product);
        return cart;
    }
    public OrderMain dtoToEntity(OrderMainDto orderMainDto) {
        OrderMain orderMain = mapper.map(orderMainDto,OrderMain.class);
        orderMain.setAddress(
                addressRepository.findById(orderMainDto.getAddressId()).orElse(null)
        );
        return  orderMain;
    }
    public OrderSub dtoToEntity(OrderSubDto orderSubDto) {
        OrderSub orderSub = mapper.map(orderSubDto,OrderSub.class);
        orderSub.setOrderMain(
                orderMainRepository.findById(
                        orderSub.getOrderMain().getId()
                ).orElse(null)
        );
        orderSub.setProduct(
                productRepository.findById(
                        orderSub.getProduct().getId()
                ).orElse(null)
        );
         return orderSub;
    }
}
