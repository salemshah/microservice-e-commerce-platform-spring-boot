package com.ecommerce.product.mapper;

import com.ecommerce.product.dto.ProductImageDTO;
import com.ecommerce.product.entity.ProductImage;
import org.mapstruct.*;

/**
 * Handles mapping for ProductImage ↔ ProductImageDTO.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProductImageMapper {

    ProductImageDTO toProductImageDTO(ProductImage image);

    @InheritInverseConfiguration
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    ProductImage toProductImage(ProductImageDTO dto);
}
