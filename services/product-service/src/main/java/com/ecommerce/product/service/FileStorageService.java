package com.ecommerce.product.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * FileStorageService
 *
 * Abstracts the file storage mechanism (local, S3, MinIO, etc.).
 * Handles upload, retrieval, and deletion of files.
 */
public interface FileStorageService {

    /**
     * Upload a file and return its accessible URL.
     *
     * @param file the multipart file to upload
     * @param directory optional directory path (e.g., "products/images")
     * @return public URL or relative path to the uploaded file
     * @throws IOException if upload fails
     */
    String uploadFile(MultipartFile file, String directory) throws IOException;

    /**
     * Delete a file by its URL or path.
     *
     * @param fileUrl path or URL of the file to delete
     * @throws IOException if deletion fails
     */
    void deleteFile(String fileUrl) throws IOException;

    /**
     * Get a file’s absolute path on the server (if locally stored).
     *
     * @param fileUrl file path or name
     * @return absolute system path
     */
    String getFilePath(String fileUrl);
}
