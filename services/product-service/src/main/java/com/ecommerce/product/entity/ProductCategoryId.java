package com.ecommerce.product.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ProductCategoryId implements Serializable {

    private Long productId;
    private Long categoryId;
}
