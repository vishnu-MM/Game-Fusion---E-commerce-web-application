package com.example.gamefusion.Services;

import com.example.gamefusion.Entity.BrandLogo;
import com.example.gamefusion.Entity.Product;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface StorageService {
    List<String> uploadImagesToFileSystem(List<MultipartFile> files, Product id);
    byte[] downloadImageFromFileSystem(Long id) throws IOException;
    boolean deleteImage(Long id) throws IOException;

    BrandLogo uploadImage(MultipartFile file) throws IOException;

    byte[] downloadImage(String fileName) throws IOException;
}
