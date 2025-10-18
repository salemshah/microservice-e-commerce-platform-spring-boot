package com.ecommerce.product.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "product_category")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder = 0;

    @Column(name = "primary_category", nullable = false)
    private Boolean primaryCategory = false;

    @PrePersist
    void prePersist() {
        if (displayOrder == null) displayOrder = 0;
        if (primaryCategory == null) primaryCategory = false;
    }
}
