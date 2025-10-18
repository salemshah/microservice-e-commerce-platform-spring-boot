package com.ecommerce.product.controller;

import com.ecommerce.product.dto.ProductRequest;
import com.ecommerce.product.dto.ProductResponse;
import com.ecommerce.product.dto.ProductSummaryDTO;
import com.ecommerce.product.event.ProductEvent;
import com.ecommerce.product.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * ProductController
 *
 * Exposes REST endpoints for managing products.
 * Integrates with ProductService, ProductSearchService, and EventPublisherService.
 */
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Slf4j
public class ProductController {

    private final ProductService productService;
    private final ProductSearchService productSearchService;
    private final EventPublisherService eventPublisherService;

    // ------------------------------------------------------
    // CREATE PRODUCT
    // ------------------------------------------------------
    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductRequest request) {
        log.info("API: Create new product - {}", request.getName());
        ProductResponse created = productService.createProduct(request);

        // Publish event
        eventPublisherService.publishProductEvent(ProductEvent.builder()
                .productId(created.getId())
                .eventType("PRODUCT_CREATED")
                .source("product-service")
                .payload(created)
                .build());

        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // ------------------------------------------------------
    // GET PRODUCT BY ID
    // ------------------------------------------------------
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
        log.debug("API: Get product by ID={}", id);
        ProductResponse product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }

    // ------------------------------------------------------
    // GET PRODUCT BY SLUG
    // ------------------------------------------------------
    @GetMapping("/slug/{slug}")
    public ResponseEntity<ProductResponse> getProductBySlug(@PathVariable String slug) {
        log.debug("API: Get product by slug={}", slug);
        ProductResponse product = productService.getProductBySlug(slug);
        return ResponseEntity.ok(product);
    }

    // ------------------------------------------------------
    // UPDATE PRODUCT
    // ------------------------------------------------------
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest request) {

        log.info("API: Update product ID={}", id);
        ProductResponse updated = productService.updateProduct(id, request);

        eventPublisherService.publishProductEvent(ProductEvent.builder()
                .productId(updated.getId())
                .eventType("PRODUCT_UPDATED")
                .source("product-service")
                .payload(updated)
                .build());

        return ResponseEntity.ok(updated);
    }

    // ------------------------------------------------------
    // DELETE PRODUCT
    // ------------------------------------------------------
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        log.warn("API: Delete product ID={}", id);
        productService.deleteProduct(id);

        eventPublisherService.publishProductEvent(ProductEvent.builder()
                .productId(id)
                .eventType("PRODUCT_DELETED")
                .source("product-service")
                .build());

        return ResponseEntity.noContent().build();
    }

    // ------------------------------------------------------
    // SEARCH / FILTER PRODUCTS
    // ------------------------------------------------------
    @GetMapping("/search")
    public ResponseEntity<Page<ProductSummaryDTO>> searchProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) BigDecimal priceMin,
            @RequestParam(required = false) BigDecimal priceMax,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt,desc") String[] sort
    ) {
        log.debug("API: Searching products");

        // Build pageable dynamically
        Sort sorting = Sort.by(Sort.Direction.fromString(sort[1]), sort[0]);
        Pageable pageable = PageRequest.of(page, size, sorting);

        Page<ProductSummaryDTO> result = productSearchService.searchProducts(
                keyword, categoryId, brand, status, priceMin, priceMax, pageable);

        return ResponseEntity.ok(result);
    }

    // ------------------------------------------------------
    // CHECK IF SKU EXISTS
    // ------------------------------------------------------
    @GetMapping("/exists/sku/{sku}")
    public ResponseEntity<Boolean> checkSkuExists(@PathVariable String sku) {
        boolean exists = productService.existsBySku(sku);
        return ResponseEntity.ok(exists);
    }
}
