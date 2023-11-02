package com.example.gamefusion.Services.Implementations;

import com.example.gamefusion.Dto.*;
import com.example.gamefusion.Entity.Images;
import com.example.gamefusion.Entity.Product;
import com.example.gamefusion.Services.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {
    private final UserService userService;
    private final ProductService productService;
    private final CategoryService categoryService;
    private final StorageService storageService;
    private final ImagesService imagesService;
    private final BrandService brandService;

    @Autowired
    public AdminServiceImpl(
            UserService userService,
            @Lazy ProductService productService,
            CategoryService categoryService,
            StorageService storageService,
            ImagesService imagesService,
            BrandService brandService       ) {
        this.userService = userService;
        this.productService = productService;
        this.categoryService = categoryService;
        this.storageService = storageService;
        this.imagesService = imagesService;
        this.brandService = brandService;
    }

    //* USER OPS

    @Override
    public PaginationInfo getAllUsers(Integer pageNo, Integer pageSize) {
        return userService.findAllUsers(pageNo, pageSize);
    }

    @Override
    public Boolean isUserExists(Integer id) {
        return userService.isExistsById(id);
    }

    @Override
    public Boolean isUserBlocked(Integer id) {
        return userService.isBlocked(id);
    }

    @Override
    @Transactional
    public void blockUser(Integer id) {
        userService.block(id);
    }

    @Override
    @Transactional
    public void unBlockUser(Integer id) {
        userService.unBlock(id);
    }


    //* CATEGORY OPS

    @Override
    public void addNewCategory(CategoryDto categoryDto) {
        categoryService.save(categoryDto);
    }

    @Override
    public void updateCategory(CategoryDto categoryDto) {
        categoryService.save(categoryDto);
    }

    @Override
    public PaginationInfo getAllCategory(Integer pageNo, Integer pageSize) {
        return categoryService.findAll(pageNo, pageSize);
    }

    @Override
    public List<CategoryDto> getAllCategory() {
        return categoryService.getAll();
    }

    @Override
    public CategoryDto getCategoryInfo(Long id) {
        return categoryService.findById(id);
    }

    @Override
    public void toggleCategoryStatus(Long id) {
        if (categoryService.isCategoryActive(id))
            categoryService.deActivateCategory(id);
        else
            categoryService.activateCategory(id);
    }

    // PRODUCT OPS

    @Override
    public Long addOrUpdateProduct(ProductDto productDto) {
        return productService.save(productDto);
    }

    @Override
    public List<String> uploadImage(List<MultipartFile> file, Long id) {
        ProductDto productDto = productService.getProductById(id);
        List<Images> images = new ArrayList<>();
        for (Long imgId : productDto.getImageIds() ) {
            images.add(
                    imagesService.mapToEntity(imagesService.findImageById(imgId))
            );
        }
        Product product = new Product(
                productDto.getId(), productDto.getName(),
                productDto.getDescription(), productDto.getPrice(),
                productDto.getQty(), productDto.getStatus(),
                brandService.mapToEntity(
                        brandService.findById(productDto.getBrandId())
                ),
                categoryService.mapToEntity(
                        categoryService.findById(productDto.getCategoryId())
                ), images
        );
        return storageService.uploadImagesToFileSystem(file,product);
    }

    @Override
    public ProductDto getProduct(Long id) {
        return productService.getProductById(id);
    }

    @Override
    public void toggleStatus(Long id) {
        if (!productService.isProductExists(id)){
            throw new EntityNotFoundException("Product not found");
        }
        if (productService.isProductActive(id)) {
            productService.deActivateProduct(id);
        }
        else {
            productService.activateProduct(id);
        }
    }

    @Override
    public List<BrandDto> getAllBrands() {
        return brandService.getAll();
    }

    @Override
    public byte[] getImages(Long imageId) {
        try{
            return storageService.downloadImageFromFileSystem(imageId);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<byte[]> getImageOfSingleProduct(Long productId) {
        return imagesService.getImageOfSingleProduct(productId);
    }

    @Override
    public void deleteImage(Long imageId) {
        try {
            if (!imagesService.isImageExists(imageId))
                throw new EntityNotFoundException("Image not found");
            if (storageService.deleteImage(imageId)) {
            imagesService.deleteImageById(imageId);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public PaginationInfo getAllProduct(Integer pageNo, Integer pageSize) {
        return productService.getAllProducts(pageNo,pageSize);
    }
}
