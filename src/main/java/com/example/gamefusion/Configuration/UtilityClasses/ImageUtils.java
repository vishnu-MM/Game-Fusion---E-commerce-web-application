package com.example.gamefusion.Configuration.UtilityClasses;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ImageUtils {
    public static byte[] compressImage(byte[] imageData) throws IOException {
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageData));
        ImageWriter writer = ImageIO.getImageWritersByFormatName("jpg").next();
        ImageWriteParam param = writer.getDefaultWriteParam();
        param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        param.setCompressionQuality(0.7f);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        writer.setOutput(new MemoryCacheImageOutputStream(outputStream));
        writer.write(null, new IIOImage(image, null, null), param);
        return outputStream.toByteArray();
    }

    public static byte[] decompressImage(byte[] imageData) throws IOException {
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageData));
        ImageWriter writer = ImageIO.getImageWritersByFormatName("jpg").next();
        ImageWriteParam param = writer.getDefaultWriteParam();
        param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        param.setCompressionQuality(1.0f);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        writer.setOutput(new MemoryCacheImageOutputStream(outputStream));
        writer.write(null, new IIOImage(image, null, null), param);
        return outputStream.toByteArray();
    }
}
