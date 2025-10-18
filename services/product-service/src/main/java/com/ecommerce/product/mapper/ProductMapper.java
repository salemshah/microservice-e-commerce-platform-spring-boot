package com.ecommerce.product.mapper;

import com.ecommerce.product.dto.*;
import com.ecommerce.product.entity.*;
import org.mapstruct.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Maps between Product entities and DTOs.
 * Handles nested relationships via helper methods.
 */
@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {
                CategoryMapper.class,
                ProductImageMapper.class,
                ProductAttributeMapper.class,
                ReviewMapper.class
        }
)
public interface ProductMapper {

    // ----------------------------
    // ENTITY → RESPONSE
    // ----------------------------
    @Mapping(target = "categories", expression = "java(mapCategories(product))")
    ProductResponse toProductResponse(Product product);

    List<ProductResponse> toProductResponseList(List<Product> products);

    // ----------------------------
    // REQUEST → ENTITY
    // ----------------------------
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "productCategories", ignore = true) // handled in service
    @Mapping(target = "images", ignore = true)
    @Mapping(target = "attributes", ignore = true)
    @Mapping(target = "reviews", ignore = true)
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "status", expression = "java(ProductStatus.valueOf(request.getStatus()))")
    Product toProduct(ProductRequest request);


    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateProductFromRequest(ProductRequest request, @MappingTarget Product product);

    // ----------------------------
    // CUSTOM CATEGORY MAPPING
    // ----------------------------
    default List<CategoryDTO> mapCategories(Product product) {
        if (product.getProductCategories() == null) return List.of();

        return product.getProductCategories().stream()
                .map(pc -> CategoryDTO.builder()
                        .id(pc.getCategory().getId())
                        .name(pc.getCategory().getName())
                        .slug(pc.getCategory().getSlug())
                        .build())
                .collect(Collectors.toList());
    }
}
