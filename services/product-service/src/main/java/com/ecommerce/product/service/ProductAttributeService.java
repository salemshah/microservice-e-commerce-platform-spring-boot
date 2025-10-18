package com.ecommerce.product.service;

import com.ecommerce.product.dto.ProductAttributeDTO;

import java.util.List;

/**
 * ProductAttributeService
 *
 * Provides operations for managing product attributes (e.g., color, size).
 */
public interface ProductAttributeService {

    /**
     * Add one or more attributes to a product.
     *
     * @param productId  the product ID
     * @param attributes list of ProductAttributeDTOs
     * @return list of created ProductAttributeDTOs
     */
    List<ProductAttributeDTO> addAttributesToProduct(Long productId, List<ProductAttributeDTO> attributes);

    /**
     * Get all attributes for a specific product.
     *
     * @param productId the product ID
     * @return list of ProductAttributeDTOs
     */
    List<ProductAttributeDTO> getAttributesByProductId(Long productId);

    /**
     * Get a single attribute by ID.
     *
     * @param attributeId attribute ID
     * @return ProductAttributeDTO
     * @throws com.ecommerce.product.exception.NotFoundException if not found
     */
    ProductAttributeDTO getAttributeById(Long attributeId);

    /**
     * Update an existing attribute.
     *
     * @param attributeId attribute ID
     * @param dto updated data
     * @return updated ProductAttributeDTO
     */
    ProductAttributeDTO updateAttribute(Long attributeId, ProductAttributeDTO dto);

    /**
     * Delete an attribute by its ID.
     *
     * @param attributeId attribute ID
     */
    void deleteAttribute(Long attributeId);

    /**
     * Delete all attributes for a given product.
     *
     * @param productId product ID
     */
    void deleteAllAttributesByProductId(Long productId);
}
