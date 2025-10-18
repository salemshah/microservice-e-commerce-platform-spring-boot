package com.ecommerce.product.service.impl;

import com.ecommerce.product.service.FileStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

/**
 * LocalFileStorageServiceImpl
 *
 * Local filesystem implementation of FileStorageService.
 * Files are saved under the configured "upload-dir".
 */
@Service
@Slf4j
public class LocalFileStorageServiceImpl implements FileStorageService {

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    @Value("${file.public-base-url:http://localhost:8080/files/}")
    private String publicBaseUrl;

    @Override
    public String uploadFile(MultipartFile file, String directory) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("Cannot upload empty file.");
        }

        String folderPath = directory != null && !directory.isBlank()
                ? uploadDir + File.separator + directory
                : uploadDir;

        Files.createDirectories(Paths.get(folderPath));

        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null && originalFilename.contains(".")
                ? originalFilename.substring(originalFilename.lastIndexOf("."))
                : "";

        String filename = UUID.randomUUID() + extension;
        Path destination = Paths.get(folderPath, filename);

        Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);

        log.info("File uploaded successfully: {}", destination.toAbsolutePath());

        // Return the public URL
        return publicBaseUrl + (directory != null ? directory + "/" : "") + filename;
    }

    @Override
    public void deleteFile(String fileUrl) throws IOException {
        if (fileUrl == null || fileUrl.isBlank()) {
            log.warn("Skipping deletion: empty file path");
            return;
        }

        String relativePath = fileUrl.replace(publicBaseUrl, "");
        Path filePath = Paths.get(uploadDir, relativePath);

        if (Files.exists(filePath)) {
            Files.delete(filePath);
            log.info("Deleted file: {}", filePath.toAbsolutePath());
        } else {
            log.warn("File not found for deletion: {}", filePath.toAbsolutePath());
        }
    }

    @Override
    public String getFilePath(String fileUrl) {
        String relativePath = fileUrl.replace(publicBaseUrl, "");
        return Paths.get(uploadDir, relativePath).toAbsolutePath().toString();
    }
}
