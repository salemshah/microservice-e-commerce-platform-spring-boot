package com.ecommerce.product.service;

import com.ecommerce.product.dto.ProductSummaryDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

/**
 * ProductSearchService
 *
 * Handles product listing, filtering, and pagination.
 * Uses flexible dynamic search criteria (keyword, category, brand, price range, etc.).
 */
public interface ProductSearchService {

    /**
     * Search products with optional filters.
     *
     * @param keyword search text (matches name or description)
     * @param categoryId filter by category
     * @param brand filter by brand
     * @param status product status (ACTIVE, INACTIVE, etc.)
     * @param priceMin minimum price
     * @param priceMax maximum price
     * @param pageable pagination + sorting info
     * @return paginated list of ProductSummaryDTO
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
}
