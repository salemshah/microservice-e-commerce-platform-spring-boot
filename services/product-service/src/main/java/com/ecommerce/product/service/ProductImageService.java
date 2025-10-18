package com.ecommerce.product.service;

import com.ecommerce.product.dto.ProductImageDTO;

import java.util.List;

/**
 * ProductImageService
 *
 * Provides operations for managing product images.
 * Handles adding, updating, deleting, and retrieving product images.
 */
public interface ProductImageService {

    /**
     * Add one or more images to a product.
     *
     * @param productId the product ID
     * @param images list of image DTOs to add
     * @return list of created ProductImageDTOs
     */
    List<ProductImageDTO> addImagesToProduct(Long productId, List<ProductImageDTO> images);

    /**
     * Get all images for a specific product.
     *
     * @param productId the product ID
     * @return list of ProductImageDTOs
     */
    List<ProductImageDTO> getImagesByProductId(Long productId);

    /**
     * Get a single image by its ID.
     *
     * @param imageId image ID
     * @return ProductImageDTO
     * @throws com.ecommerce.product.exception.NotFoundException if not found
     */
    ProductImageDTO getImageById(Long imageId);

    /**
     * Update an existing image (e.g., change position, mark as primary).
     *
     * @param imageId the image ID
     * @param dto updated image data
     * @return updated ProductImageDTO
     */
    ProductImageDTO updateImage(Long imageId, ProductImageDTO dto);

    /**
     * Delete a product image by its ID.
     *
     * @param imageId image ID
     */
    void deleteImage(Long imageId);

    /**
     * Set one image as the primary image for the product.
     * Automatically unsets other primary images.
     *
     * @param productId the product ID
     * @param imageId   the image ID to set as primary
     */
    void setPrimaryImage(Long productId, Long imageId);

    /**
     * Delete all images belonging to a specific product.
     * Useful when deleting or replacing a product.
     *
     * @param productId the product ID
     */
    void deleteAllImagesByProductId(Long productId);
}
