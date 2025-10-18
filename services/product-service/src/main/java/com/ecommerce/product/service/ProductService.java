package com.ecommerce.product.service;

import com.ecommerce.product.dto.ProductRequest;
import com.ecommerce.product.dto.ProductResponse;
import com.ecommerce.product.dto.ProductSummaryDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * ProductService
 *
 * Defines the business operations for managing products.
 * - DTOs are used at the boundary (no entities leak out)
 * - Pagination for listings
 * - Optional filters for basic search
 */
public interface ProductService {

    /**
     * Create a new product.
     *
     * @param request validated ProductRequest DTO
     * @return created ProductResponse
     */
    ProductResponse createProduct(ProductRequest request);

    /**
     * Get a product by ID.
     *
     * @param id product ID
     * @return ProductResponse
     * @throws com.ecommerce.product.exception.NotFoundException if not found
     */
    ProductResponse getProductById(Long id);

    /**
     * Get a product by slug (SEO-friendly identifier).
     */
    ProductResponse getProductBySlug(String slug);

    /**
     * Get a product by SKU.
     */
    ProductResponse getProductBySku(String sku);

    /**
     * Update a product (full update semantics).
     *
     * @param id product ID
     * @param request validated ProductRequest DTO
     * @return updated ProductResponse
     * @throws com.ecommerce.product.exception.NotFoundException if not found
     */
    ProductResponse updateProduct(Long id, ProductRequest request);

    /**
     * Delete a product by ID.
     *
     * @param id product ID
     */
    void deleteProduct(Long id);

    /**
     * List/search products with common filters.
     * Any filter may be null to ignore it.
     *
     * @param keyword     search in name/description (optional)
     * @param categoryId  filter by category (optional)
     * @param brand       filter by brand (optional)
     * @param status      filter by status (ACTIVE, INACTIVE, etc.) (optional)
     * @param priceMin    minimum price (optional)
     * @param priceMax    maximum price (optional)
     * @param pageable    pagination + sorting
     * @return paged list of lightweight summaries
     */
    Page<ProductSummaryDTO> searchProducts(
            String keyword,
            Long categoryId,
            String brand,
            String status,
            BigDecimal priceMin,
            BigDecimal priceMax,
            Pageable pageable
    );

    /**
     * Check if a SKU already exists (useful for validation at controller/service level).
     */
    boolean existsBySku(String sku);

    /**
     * Optionally expose a method to resolve an ID by slug/SKU if you need quick lookups.
     */
    Optional<Long> findIdBySlug(String slug);
}
