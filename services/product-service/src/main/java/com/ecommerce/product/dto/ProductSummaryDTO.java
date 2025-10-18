package com.ecommerce.product.dto;

import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductSummaryDTO {

    private Long id;
    private String name;
    private String slug;
    private BigDecimal price;
    private BigDecimal discountPrice;
    private String currency;
    private String brand;
    private String thumbnailUrl;
}
