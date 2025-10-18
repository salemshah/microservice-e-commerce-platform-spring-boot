package com.ecommerce.product.mapper;

import com.ecommerce.product.dto.ReviewDTO;
import com.ecommerce.product.entity.Review;
import org.mapstruct.*;

/**
 * Handles mapping for Review ↔ ReviewDTO.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ReviewMapper {

    ReviewDTO toReviewDTO(Review review);

    @InheritInverseConfiguration
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    Review toReview(ReviewDTO dto);
}
