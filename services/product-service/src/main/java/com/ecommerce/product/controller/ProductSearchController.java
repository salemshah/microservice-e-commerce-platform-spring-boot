package com.ecommerce.product.controller;

import com.ecommerce.product.dto.ProductSummaryDTO;
import com.ecommerce.product.service.ProductSearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/search/products")
@RequiredArgsConstructor
@Slf4j
public class ProductSearchController {

    private final ProductSearchService productSearchService;

    /**
     * Search and filter products with pagination and sorting.
     *
     * Example:
     * GET /api/search/products?keyword=iphone&categoryId=2&brand=Apple&priceMin=500&priceMax=1000&page=0&size=20&sort=price,asc
     */
    @GetMapping
    public ResponseEntity<Page<ProductSummaryDTO>> searchProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) BigDecimal priceMin,
            @RequestParam(required = false) BigDecimal priceMax,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort
    ) {
        log.debug("API: Search products [keyword={}, category={}, brand={}, status={}, priceMin={}, priceMax={}, page={}, size={}, sort={}]",
                keyword, categoryId, brand, status, priceMin, priceMax, page, size, sort);

        Pageable pageable = buildPageRequest(page, size, sort);

        Page<ProductSummaryDTO> results = productSearchService.searchProducts(
                keyword, categoryId, brand, status, priceMin, priceMax, pageable
        );

        return ResponseEntity.ok(results);
    }

    // ----------------------------
    // UTILITIES
    // ----------------------------
    private Pageable buildPageRequest(int page, int size, String sort) {
        String[] sortParams = sort.split(",");
        String sortField = sortParams[0];
        Sort.Direction direction = sortParams.length > 1
                ? Sort.Direction.fromOptionalString(sortParams[1]).orElse(Sort.Direction.ASC)
                : Sort.Direction.ASC;

        return PageRequest.of(page, size, Sort.by(direction, sortField));
    }
}
