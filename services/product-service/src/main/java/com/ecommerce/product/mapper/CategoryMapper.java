package com.ecommerce.product.mapper;

import com.ecommerce.product.dto.CategoryDTO;
import com.ecommerce.product.entity.Category;
import org.mapstruct.*;

/**
 * Handles conversion between Category and CategoryDTO.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CategoryMapper {

    @Mapping(target = "parentId", source = "parent.id")
    CategoryDTO toCategoryDTO(Category category);

    @InheritInverseConfiguration
    @Mapping(target = "products", ignore = true)
    @Mapping(target = "parent", ignore = true)
    @Mapping(target = "subcategories", ignore = true)
    Category toCategory(CategoryDTO dto);
}
