package com.example.gamefusion.Services.Implementations;

import com.example.gamefusion.Configuration.ExceptionHandlerConfig.EntityNotFoundException;
import com.example.gamefusion.Dto.ProductDto;
import com.example.gamefusion.Entity.Images;
import com.example.gamefusion.Entity.Product;
import com.example.gamefusion.Repository.ImagesRepository;
import com.example.gamefusion.Services.StorageService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class StorageServiceImpl implements StorageService {


    private final ImagesRepository imagesRepository;
    @Autowired
    public StorageServiceImpl(ImagesRepository imagesRepository) {
        this.imagesRepository = imagesRepository;
    }

    @Override
    @Transactional
    public List<String> uploadImagesToFileSystem(List<MultipartFile> files, Product product) {
        String FOLDER_PATH = "C:/Users/vishn/Projects/Game Fusion/src/main/resources/static/Images/";
        List<String> filePaths = new ArrayList<>();

        for (MultipartFile file : files) {
            String filePath = FOLDER_PATH + file.getOriginalFilename();

            try {
                Images img = Images.builder()
                            .name(file.getOriginalFilename())
                            .type(file.getContentType())
                            .filePath(filePath)
                            .build();
                img.setProduct(product);
                imagesRepository.save(img);
                file.transferTo(new File(filePath));
                filePaths.add("File uploaded successfully: " + filePath);
            }
            catch (IOException e) {
                filePaths.add("Error uploading the file: " + e.getMessage());
            }
            catch (Exception ex) {
                filePaths.add("An error occurred: " + ex.getMessage());
            }
        }
        return filePaths;
    }

    @Override
    public byte[] downloadImageFromFileSystem(String fileName) throws IOException {
        Optional<Images> imageData = imagesRepository.findByName(fileName);
        if (imageData.isPresent()){
            String filePath = imageData.get().getFilePath();
            return Files.readAllBytes(new File(filePath).toPath());
        }
        else {
            throw new EntityNotFoundException("Image Not Fount");
        }
    }

    //?    public String uploadImage(MultipartFile file) throws IOException {
//?        ImageData imageData = repository.save(ImageData.builder()
//?                .name(file.getOriginalFilename())
//?                .type(file.getContentType())
//?                .imageData(ImageUtils.compressImage(file.getBytes())).build());
//?        if (imageData != null) {
//?           return "file uploaded successfully : " + file.getOriginalFilename();
//?        }
//?        return null;
//?    }

//?    public byte[] downloadImage(String fileName) {
//?      Optional<ImageData> dbImageData = repository.findByName(fileName);
//?        byte[] images = ImageUtils.decompressImage(dbImageData.get().getImageData());
//?        return images;
//?    }
}
