package com.example.gamefusion.Services;

import com.example.gamefusion.Dto.ProductDto;
import com.example.gamefusion.Entity.Product;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface StorageService {
    List<String> uploadImagesToFileSystem(List<MultipartFile> files, Product id);
    byte[] downloadImageFromFileSystem(String fileName) throws IOException;


}
