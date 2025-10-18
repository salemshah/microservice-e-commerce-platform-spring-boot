package com.ecommerce.product.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for returning detailed product information to clients.
 * Designed for clean, API-friendly serialization.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {

    private Long id;

    private String sku;

    private String name;

    private String slug;

    private String description;

    private String shortDescription;

    private BigDecimal price;

    private BigDecimal discountPrice;

    private String currency;

    private Integer stockQuantity;

    private String status;

    private String brand;

    private BigDecimal weight;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    // Nested data
    private List<CategoryDTO> categories;

    private List<ProductImageDTO> images;

    private List<ProductAttributeDTO> attributes;

    private List<ReviewDTO> reviews;
}
