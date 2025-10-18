package com.ecommerce.product.mapper;

import com.ecommerce.product.dto.ProductAttributeDTO;
import com.ecommerce.product.entity.ProductAttribute;
import org.mapstruct.*;

/**
 * Handles mapping for ProductAttribute ↔ ProductAttributeDTO.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProductAttributeMapper {

    ProductAttributeDTO toProductAttributeDTO(ProductAttribute attribute);

    @InheritInverseConfiguration
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    ProductAttribute toProductAttribute(ProductAttributeDTO dto);
}
