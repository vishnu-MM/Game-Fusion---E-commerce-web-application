package com.example.gamefusion.Services.Implementations;

import com.example.gamefusion.Configuration.ExceptionHandlerConfig.EntityNotFound;
import com.example.gamefusion.Configuration.UtilityClasses.ImageUtils;
import com.example.gamefusion.Repository.BrandLogoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.gamefusion.Repository.ImagesRepository;
import org.springframework.web.multipart.MultipartFile;
import com.example.gamefusion.Services.StorageService;
import org.springframework.core.io.ClassPathResource;
import com.example.gamefusion.Entity.BrandLogo;
import org.springframework.stereotype.Service;
import com.example.gamefusion.Entity.Product;
import org.springframework.core.io.Resource;
import com.example.gamefusion.Entity.Images;
import jakarta.transaction.Transactional;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Optional;
import org.slf4j.Logger;
import java.util.List;

import static com.example.gamefusion.Configuration.UtilityClasses.ImageUtils.*;

@Service
public class StorageServiceImpl implements StorageService {

    private final ImagesRepository imagesRepository;
    private final BrandLogoRepository brandLogoRepository;
    Logger log = LoggerFactory.getLogger(StorageService.class);
    @Autowired
    public StorageServiceImpl(ImagesRepository imagesRepository,
                              BrandLogoRepository brandLogoRepository) {
        this.imagesRepository = imagesRepository;
        this.brandLogoRepository = brandLogoRepository;
    }

//    @Override
//    @Transactional
//    public List<String> uploadImagesToFileSystem(List<MultipartFile> files, Product product) {
//        String FOLDER_PATH = "static/Images/";
//        Resource resource = new ClassPathResource(FOLDER_PATH);
//
//        List<String> filePaths = new ArrayList<>();
//
//        try {
//            String absolutePath = resource.getFile().getAbsolutePath();
//            for (MultipartFile file : files) {
//                String filePath = absolutePath + File.separator + file.getOriginalFilename();
//
//                Images img = Images.builder()
//                        .name(file.getOriginalFilename())
//                        .type(file.getContentType())
//                        .filePath(filePath)
//                        .build();
//                img.setProduct(product);
//                imagesRepository.save(img);
//                file.transferTo(new File(filePath));
//                filePaths.add("File uploaded successfully: " + filePath);
//            }
//        } catch (IOException e) {
//            filePaths.add("Error uploading the file: " + e.getMessage());
//            log.error(e.getMessage());
//        } catch (Exception ex) {
//            filePaths.add("An error occurred: " + ex.getMessage());
//            log.error(ex.getMessage());
//        }
//
//        return filePaths;
//    }
@Override
@Transactional
public List<String> uploadImagesToFileSystem(List<MultipartFile> files, Product product) {
    String FOLDER_PATH = "~/Game-Fusion---E-commerce-web-application/src/main/resources/static/Images/";
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
        } catch ( Exception ex) {
            filePaths.add("Error uploading the file: " + ex.getMessage());
            log.error(ex.getMessage());
        }

    }
    return filePaths;
}

    @Override
    public byte[] downloadImageFromFileSystem(Long id) throws IOException {
        Optional<Images> imageData = imagesRepository.findById(id);
        if (imageData.isPresent()){
            String filePath = imageData.get().getFilePath();
            return Files.readAllBytes(new File(filePath).toPath());
        }
        else
            throw new EntityNotFound("Image Not Fount");
    }

    @Override
    public boolean deleteImage(Long id){
        Optional<Images> imageData = imagesRepository.findById(id);
        if (imageData.isPresent()) {
            String filePath = imageData.get().getFilePath();
            File file = new File(filePath);
            return file.delete();
        }
        else
            throw new EntityNotFound("Image Not Found");
    }

    @Override
   public BrandLogo uploadImage(MultipartFile file) throws IOException {
        BrandLogo brandLogo = new BrandLogo();
        brandLogo.setName(file.getOriginalFilename());
        brandLogo.setType(file.getContentType());
        brandLogo.setImageData(compressImage(file.getBytes()));
        return brandLogoRepository.save(brandLogo);
   }

   @Override
    public byte[] downloadImage(Long id) throws IOException {
        Optional<BrandLogo> brandLogo = brandLogoRepository.findById(id);
        if (brandLogo.isEmpty()) return null;
        return ImageUtils.decompressImage(brandLogo.get().getImageData());
    }
}
