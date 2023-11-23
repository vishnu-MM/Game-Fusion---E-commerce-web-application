package com.example.gamefusion.Services.Implementations;

import com.example.gamefusion.Entity.BrandLogo;
import com.example.gamefusion.Repository.BrandLogoRepository;
import jakarta.persistence.EntityNotFoundException;
import com.example.gamefusion.Entity.Images;
import com.example.gamefusion.Entity.Product;
import com.example.gamefusion.Repository.ImagesRepository;
import com.example.gamefusion.Services.StorageService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import com.example.gamefusion.Configuration.UtilityClasses.ImageUtils;

import static com.example.gamefusion.Configuration.UtilityClasses.ImageUtils.*;

@Service
public class StorageServiceImpl implements StorageService {

    Logger log = LoggerFactory.getLogger(StorageService.class);
    private final ImagesRepository imagesRepository;
    private final BrandLogoRepository brandLogoRepository;
    @Autowired
    public StorageServiceImpl(ImagesRepository imagesRepository,
                              BrandLogoRepository brandLogoRepository) {
        this.imagesRepository = imagesRepository;
        this.brandLogoRepository = brandLogoRepository;
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
        else {
            throw new EntityNotFoundException("Image Not Fount");
        }
    }

    @Override
    public boolean deleteImage(Long id){
        Optional<Images> imageData = imagesRepository.findById(id);
        if (imageData.isPresent()) {
            String filePath = imageData.get().getFilePath();
            File file = new File(filePath);
            return file.delete();
        } else {
            throw new EntityNotFoundException("Image Not Found");
        }
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
