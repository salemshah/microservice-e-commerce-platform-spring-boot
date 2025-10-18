package com.ecommerce.product.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductAttributeDTO {

    private Long id;

    @NotBlank(message = "Attribute name is required.")
    @Size(max = 100, message = "Attribute name cannot exceed 100 characters.")
    private String attributeName;

    @NotBlank(message = "Attribute value is required.")
    @Size(max = 100, message = "Attribute value cannot exceed 100 characters.")
    private String attributeValue;
}
