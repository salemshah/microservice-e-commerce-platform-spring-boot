package com.ecommerce.product.controller;

import com.ecommerce.product.dto.ProductImageDTO;
import com.ecommerce.product.service.FileStorageService;
import com.ecommerce.product.service.ProductImageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * ProductImageController
 *
 * Handles uploading, listing, deleting, and managing product images.
 */
@RestController
@RequestMapping("/api/products/{productId}/images")
@RequiredArgsConstructor
@Slf4j
public class ProductImageController {

    private final ProductImageService productImageService;
    private final FileStorageService fileStorageService;

    // ------------------------------------------------------------
    // UPLOAD ONE OR MULTIPLE IMAGES
    // ------------------------------------------------------------
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<ProductImageDTO>> uploadImages(
            @PathVariable Long productId,
            @RequestParam("files") List<MultipartFile> files
    ) throws IOException {
        log.info("API: Upload {} image(s) for product ID={}", files.size(), productId);

        // 1️⃣ Upload each file to storage
        List<ProductImageDTO> imageDTOs = files.stream().map(file -> {
            try {
                String imageUrl = fileStorageService.uploadFile(file, "products/" + productId);
                return ProductImageDTO.builder()
                        .imageUrl(imageUrl)
                        .isPrimary(false)
                        .position(0)
                        .build();
            } catch (IOException e) {
                throw new RuntimeException("Failed to upload file: " + file.getOriginalFilename(), e);
            }
        }).toList();

        // 2️⃣ Save image records in DB
        List<ProductImageDTO> savedImages = productImageService.addImagesToProduct(productId, imageDTOs);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedImages);
    }

    // ------------------------------------------------------------
    // GET ALL IMAGES FOR PRODUCT
    // ------------------------------------------------------------
    @GetMapping
    public ResponseEntity<List<ProductImageDTO>> getProductImages(@PathVariable Long productId) {
        log.debug("API: Get all images for product ID={}", productId);
        List<ProductImageDTO> images = productImageService.getImagesByProductId(productId);
        return ResponseEntity.ok(images);
    }

    // ------------------------------------------------------------
    // GET SINGLE IMAGE BY ID
    // ------------------------------------------------------------
    @GetMapping("/{imageId}")
    public ResponseEntity<ProductImageDTO> getImageById(@PathVariable Long imageId) {
        log.debug("API: Get product image ID={}", imageId);
        ProductImageDTO image = productImageService.getImageById(imageId);
        return ResponseEntity.ok(image);
    }

    // ------------------------------------------------------------
    // SET PRIMARY IMAGE
    // ------------------------------------------------------------
    @PutMapping("/{imageId}/primary")
    public ResponseEntity<Void> setPrimaryImage(
            @PathVariable Long productId,
            @PathVariable Long imageId
    ) {
        log.info("API: Set primary image ID={} for product ID={}", imageId, productId);
        productImageService.setPrimaryImage(productId, imageId);
        return ResponseEntity.noContent().build();
    }

    // ------------------------------------------------------------
    // UPDATE IMAGE DETAILS (alt text, position)
    // ------------------------------------------------------------
    @PutMapping("/{imageId}")
    public ResponseEntity<ProductImageDTO> updateImage(
            @PathVariable Long imageId,
            @Valid @RequestBody ProductImageDTO dto
    ) {
        log.info("API: Update image ID={}", imageId);
        ProductImageDTO updated = productImageService.updateImage(imageId, dto);
        return ResponseEntity.ok(updated);
    }

    // ------------------------------------------------------------
    // DELETE IMAGE
    // ------------------------------------------------------------
    @DeleteMapping("/{imageId}")
    public ResponseEntity<Void> deleteImage(
            @PathVariable Long productId,
            @PathVariable Long imageId
    ) throws IOException {
        log.warn("API: Delete image ID={} for product ID={}", imageId, productId);

        // 1️⃣ Get image data to delete physical file too
        ProductImageDTO image = productImageService.getImageById(imageId);

        // 2️⃣ Delete file from storage
        fileStorageService.deleteFile(image.getImageUrl());

        // 3️⃣ Remove from database
        productImageService.deleteImage(imageId);

        return ResponseEntity.noContent().build();
    }
}
