package com.ecommerce.product.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductImageDTO {

    private Long id;

    @NotBlank(message = "Image URL is required.")
    @Size(max = 500, message = "Image URL cannot exceed 500 characters.")
    private String imageUrl;

    @Size(max = 255, message = "Alt text cannot exceed 255 characters.")
    private String altText;

    private boolean isPrimary;

    @PositiveOrZero(message = "Position cannot be negative.")
    private Integer position;
}
