package com.ecommerce.product.service.impl;

import com.ecommerce.product.dto.ProductSummaryDTO;
import com.ecommerce.product.entity.Product;
import com.ecommerce.product.entity.ProductStatus;
import com.ecommerce.product.repository.ProductRepository;
import com.ecommerce.product.service.ProductSearchService;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ProductSearchServiceImpl implements ProductSearchService {

    private final ProductRepository productRepository;

    @Override
    public Page<ProductSummaryDTO> searchProducts(
            String keyword,
            Long categoryId,
            String brand,
            String status,
            BigDecimal priceMin,
            BigDecimal priceMax,
            Pageable pageable) {

        log.debug("Searching products with filters: keyword={}, category={}, brand={}, status={}, priceRange=[{}, {}]",
                keyword, categoryId, brand, status, priceMin, priceMax);

        Specification<Product> spec = Specification.unrestricted();

        // Filter: keyword in name or description
        if (keyword != null && !keyword.isBlank()) {
            spec = spec.and((root, query, cb) -> cb.or(
                    cb.like(cb.lower(root.get("name")), "%" + keyword.toLowerCase() + "%"),
                    cb.like(cb.lower(root.get("description")), "%" + keyword.toLowerCase() + "%")
            ));
        }


        if (categoryId != null) {
            spec = spec.and((root, query, cb) -> {
                Join<Object, Object> productCategoryJoin = root.join("productCategories", JoinType.INNER);
                Join<Object, Object> categoryJoin = productCategoryJoin.join("category", JoinType.INNER);
                return cb.equal(categoryJoin.get("id"), categoryId);
            });
        }

        // Filter: brand
        if (brand != null && !brand.isBlank()) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(cb.lower(root.get("brand")), brand.toLowerCase()));
        }

        // Filter: status
        if (status != null && !status.isBlank()) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("status"), ProductStatus.valueOf(status.toUpperCase())));
        }

        // Filter: price range
        if (priceMin != null) {
            spec = spec.and((root, query, cb) ->
                    cb.greaterThanOrEqualTo(root.get("price"), priceMin));
        }

        if (priceMax != null) {
            spec = spec.and((root, query, cb) ->
                    cb.lessThanOrEqualTo(root.get("price"), priceMax));
        }

        // Execute query
        Page<Product> resultPage = productRepository.findAll(spec, pageable);

        // Map results
        return resultPage.map(product -> ProductSummaryDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .slug(product.getSlug())
                .price(product.getPrice())
                .discountPrice(product.getDiscountPrice())
                .currency(product.getCurrency())
                .brand(product.getBrand())
                .thumbnailUrl(product.getImages().stream()
                        .filter(img -> img.isPrimary())
                        .findFirst()
                        .map(img -> img.getImageUrl())
                        .orElse(null))
                .build());
    }
}
