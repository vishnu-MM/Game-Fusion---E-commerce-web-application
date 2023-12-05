package com.example.gamefusion.Configuration.UtilityClasses;

import com.example.gamefusion.Dto.*;
import com.example.gamefusion.Entity.*;
import com.example.gamefusion.Repository.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class EntityDtoConversionUtil {
    private final ModelMapper mapper;
    private final UserRepository userRepository;
    private final CouponRepository couponRepository;
    private final ImagesRepository imagesRepository;
    private final AddressRepository addressRepository;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final OrderMainRepository orderMainRepository;
    private final BrandLogoRepository brandLogoRepository;
    @Autowired
    public EntityDtoConversionUtil(ModelMapper mapper,
                                   UserRepository userRepository, CouponRepository couponRepository,
                                   AddressRepository addressRepository, ProductRepository productRepository,
                                   CategoryRepository categoryRepository, ImagesRepository imagesRepository,
                                   OrderMainRepository orderMainRepository, BrandLogoRepository brandLogoRepository) {
        this.mapper = mapper;
        this.userRepository = userRepository;
        this.imagesRepository = imagesRepository;
        this.couponRepository = couponRepository;
        this.addressRepository = addressRepository;
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.orderMainRepository = orderMainRepository;
        this.brandLogoRepository = brandLogoRepository;
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
    public UserCouponsDto entityToDto(UserCoupons userCoupons) {
        return new UserCouponsDto(
            userCoupons.getId(),
            userCoupons.getUser().getId(),
            userCoupons.getCoupon().getId()
        );
    }
    public BrandDto entityToDto(Brand brand) {
        BrandDto brandDto = new BrandDto();
        brandDto.setId(brand.getId());
        brandDto.setName(brand.getName());
        brandDto.setStatus(brand.getStatus());
        BrandLogo brandLogo = brand.getBrandLogo();
        if (brandLogo != null ) {
            brandDto.setLogo(brandLogo.getId());
        }
        return brandDto;
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
    public WishListDto entityToDto(WishList wishList) {
      return mapper.map(wishList,WishListDto.class);
    }
    public OrderMainDto entityToDto(OrderMain orderMain) {
      return mapper.map(orderMain,OrderMainDto.class);
    }
    public OrderSubDto entityToDto(OrderSub orderSub) {
      return mapper.map(orderSub,OrderSubDto.class);
    }
    public PaymentDto entityToDto(Payment payment) {
      return mapper.map(payment,PaymentDto.class);
    }
    public CouponDto entityToDto(Coupon coupon) {
      return mapper.map(coupon,CouponDto.class);
    }
    public CategoryOfferDto entityToDto(CategoryOffer categoryOffer) {
      return mapper.map(categoryOffer,CategoryOfferDto.class);
    }
    public WalletDto entityToDto(Wallet wallet) {
      return mapper.map(wallet,WalletDto.class);
    }
    public OrderHistoryDto entityToDto(OrderHistory orderHistory) {
        return new OrderHistoryDto(
            orderHistory.getId(),
            orderHistory.getOrder().getId(),
            orderHistory.getOrderStatus(),
            orderHistory.getDate()
        );
    }

    //? DTO TO ENTITY

    public Product dtoToEntity(ProductDto productDto) {
        Product product = mapper.map(productDto,Product.class);
        if (productDto.getImageIds() != null) {
            List<Images> imagesList = new ArrayList<>();
            for (Long id : productDto.getImageIds() )
                imagesList.add( imagesRepository.findById(id).orElse(null) );
            product.setImages(imagesList);
        }
        return product;
    }
    public Category dtoToEntity(CategoryDto categoryDto) {
        return mapper.map(categoryDto,Category.class);
    }
    public Brand dtoToEntity(BrandDto brandDto) {
        Brand brand = new Brand();
        brand.setName(brandDto.getName());
        brand.setStatus(brandDto.getStatus());
        if (brandDto.getId() != null ) brand.setId(brandDto.getId());
        if (brandDto.getLogo() != null) {
            Optional<BrandLogo> brandLogo = brandLogoRepository.findById(brandDto.getLogo());
            brandLogo.ifPresent(brand::setBrandLogo);
        }
        return brand;
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
        cart.setProduct(productRepository.findById(cartDto.getProductID()).orElse(null));
        cart.setUser(userRepository.findById(cartDto.getUserID()).orElse(null));
        return cart;
    }
    public WishList dtoToEntity(WishListDto wishListDto) {
        WishList wishList = mapper.map(wishListDto,WishList.class);
        wishList.setProduct(productRepository.findById(wishListDto.getProductId()).orElse(null));
        wishList.setUser(userRepository.findById(wishListDto.getUserId()).orElse(null));
        return wishList;
    }
    public OrderMain dtoToEntity(OrderMainDto orderMainDto) {
        OrderMain orderMain = mapper.map(orderMainDto,OrderMain.class);
        orderMain.setAddress(
                addressRepository.findById(orderMainDto.getAddressId()).orElse(null)
        );
        return orderMain;
    }
    public Coupon dtoToEntity(CouponDto couponDto) {
        return mapper.map(couponDto,Coupon.class);
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
    public Payment dtoToEntity(PaymentDto paymentDto) {
        Payment payment = mapper.map(paymentDto,Payment.class);
        payment.setOrderMain(
            orderMainRepository.findById(paymentDto.getOrderId()).orElse(null)
        );
        return payment;
    }
    public CategoryOffer dtoToEntity(CategoryOfferDto categoryOfferDto) {
        CategoryOffer categoryOffer = mapper.map(categoryOfferDto,CategoryOffer.class);
        categoryOffer.setCategory(
            categoryRepository.findById(categoryOfferDto.getCategoryId()).orElse(null)
        );
        return categoryOffer;
    }
    public UserCoupons dtoToEntity(UserCouponsDto userCouponsDto) {
        UserCoupons userCoupons = new UserCoupons();
        userCoupons.setUser(userRepository.findById(userCouponsDto.getUserId()).orElse(null));
        userCoupons.setCoupon(couponRepository.findById(userCouponsDto.getCouponId()).orElse(null));
        if (userCouponsDto.getId() != null ) userCoupons.setId(userCouponsDto.getId());
        return userCoupons;
    }
    public Wallet dtoToEntity(WalletDto walletDto) {
        Wallet wallet = mapper.map(walletDto,Wallet.class);
        wallet.setUser(
            userRepository.findById(walletDto.getUserId()).orElse(null)
        );
        return wallet;
    }
    public OrderHistory dtoToEntity(OrderHistoryDto orderHistoryDto) {
        OrderHistory orderHistory = mapper.map(orderHistoryDto,OrderHistory.class);
        orderHistory.setOrder(
            orderMainRepository.findById(orderHistoryDto.getOrderId()).orElse(null)
        );
        return orderHistory;
    }

}
