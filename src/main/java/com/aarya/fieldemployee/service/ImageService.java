package com.aarya.fieldemployee.service;

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class ImageService {

    @Value("${app.upload.dir:${user.home}/fieldemployee/uploads}")
    private String uploadDir;

    @Value("${app.profile.image.width:250}")
    private int profileImageWidth;

    @Value("${app.profile.image.height:250}")
    private int profileImageHeight;

    private static final String[] ALLOWED_EXTENSIONS = {"jpg", "jpeg", "png"};
    private static final long MAX_FILE_SIZE = 250 * 1024; // 250KB in bytes

    public String saveProfileImage(MultipartFile file) throws IOException {
      
        validateImageFile(file);

        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String fileName = UUID.randomUUID().toString() + getFileExtension();
        Path filePath = uploadPath.resolve(fileName);

        // Resize and convert to JPG format
        Thumbnails.of(file.getInputStream())
                .size(profileImageWidth, profileImageHeight)
                .crop(Positions.CENTER)
                .keepAspectRatio(true)
                .outputFormat("jpg")  // Force JPG format
                .outputQuality(1.0)   // No compression
                .toFile(filePath.toFile());

        return fileName;
    }

    private void validateImageFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Please provide an image file");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException(
                "File size must be less than 250KB. Current size: " + 
                String.format("%.2f", file.getSize() / 1024.0) + "KB");
        }

        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        if (extension == null) {
            throw new IllegalArgumentException("File must have an extension");
        }

        boolean isValidExtension = false;
        for (String allowedExt : ALLOWED_EXTENSIONS) {
            if (allowedExt.equalsIgnoreCase(extension)) {
                isValidExtension = true;
                break;
            }
        }

        if (!isValidExtension) {
            throw new IllegalArgumentException("Invalid image format. Allowed formats are: JPG, JPEG, PNG");
        }

        try {
            BufferedImage img = ImageIO.read(file.getInputStream());
            if (img == null) {
                throw new IllegalArgumentException("Invalid image file");
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("Unable to process image file", e);
        }
    }

    private String getFileExtension() {
        return ".jpg"; 
    }

    public void deleteProfileImage(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return;
        }
        Path filePath = Paths.get(uploadDir).resolve(fileName);
        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}