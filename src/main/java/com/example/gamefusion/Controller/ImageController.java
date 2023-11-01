package com.example.gamefusion.Controller;

import com.example.gamefusion.Services.ImagesService;
import com.example.gamefusion.Services.StorageService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
public class ImageController {
    private final StorageService storageService;
    private final ImagesService imagesService;
    @Autowired
    public ImageController(StorageService storageService, ImagesService imagesService) {
        this.storageService = storageService;
        this.imagesService = imagesService;
    }

    @GetMapping("/images/{imageId}")
    public ResponseEntity<byte[]> getImage(@PathVariable("imageId") Long imageId ) {
        try {
            byte[] imageBytes = storageService.downloadImageFromFileSystem(imageId);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);
            return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
        } catch (EntityNotFoundException | IOException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

}
