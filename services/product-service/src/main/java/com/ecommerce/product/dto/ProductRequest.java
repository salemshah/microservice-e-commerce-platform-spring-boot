package com.ecommerce.product.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.List;

/**
 * DTO for creating or updating a product.
 * Includes field validation and best practices for data integrity.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {

    @NotBlank(message = "SKU is required.")
    @Size(max = 100, message = "SKU cannot exceed 100 characters.")
    private String sku;

    @NotBlank(message = "Product name is required.")
    @Size(max = 255, message = "Product name cannot exceed 255 characters.")
    private String name;

    @NotBlank(message = "Slug is required.")
    @Pattern(regexp = "^[a-z0-9]+(?:-[a-z0-9]+)*$",
            message = "Slug must be lowercase and can only contain letters, numbers, and hyphens.")
    @Size(max = 255, message = "Slug cannot exceed 255 characters.")
    private String slug;

    @NotBlank(message = "Description cannot be empty.")
    private String description;

    @Size(max = 500, message = "Short description cannot exceed 500 characters.")
    private String shortDescription;

    @NotNull(message = "Price is required.")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than zero.")
    @Digits(integer = 8, fraction = 2, message = "Price format is invalid.")
    private BigDecimal price;

    @DecimalMin(value = "0.0", inclusive = true, message = "Discount price cannot be negative.")
    @Digits(integer = 8, fraction = 2, message = "Discount price format is invalid.")
    private BigDecimal discountPrice;

    @NotBlank(message = "Currency is required.")
    @Pattern(regexp = "^[A-Z]{3}$", message = "Currency must be a 3-letter ISO code (e.g., USD, EUR).")
    private String currency;

    @NotNull(message = "Stock quantity is required.")
    @PositiveOrZero(message = "Stock quantity cannot be negative.")
    private Integer stockQuantity;

    @NotBlank(message = "Status is required.")
    @Pattern(regexp = "ACTIVE|INACTIVE|OUT_OF_STOCK|DISCONTINUED",
            message = "Status must be one of: ACTIVE, INACTIVE, OUT_OF_STOCK, DISCONTINUED.")
    private String status;

    @Size(max = 100, message = "Brand name cannot exceed 100 characters.")
    private String brand;

    @DecimalMin(value = "0.0", inclusive = true, message = "Weight cannot be negative.")
    @Digits(integer = 6, fraction = 2, message = "Weight format is invalid.")
    private BigDecimal weight;

    // Relationships
    @NotEmpty(message = "At least one category is required.")
    private List<Long> categoryIds;

    @Size(max = 10, message = "A product can have up to 10 images.")
    private List<@Valid ProductImageDTO> images;

    @Size(max = 20, message = "A product can have up to 20 attributes.")
    private List<@Valid ProductAttributeDTO> attributes;
}
