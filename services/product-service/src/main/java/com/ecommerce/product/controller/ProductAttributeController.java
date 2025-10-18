package com.ecommerce.product.controller;

import com.ecommerce.product.dto.ProductAttributeDTO;
import com.ecommerce.product.service.ProductAttributeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ProductAttributeController
 * Handles CRUD operations for product attributes
 * such as color, size, material, etc.
 */
@RestController
@RequestMapping("/api/products/{productId}/attributes")
@RequiredArgsConstructor
@Slf4j
public class ProductAttributeController {

    private final ProductAttributeService productAttributeService;

    // ------------------------------------------------------------
    // ADD ATTRIBUTES TO PRODUCT
    // ------------------------------------------------------------
    @PostMapping
    public ResponseEntity<List<ProductAttributeDTO>> addAttributes(
            @PathVariable Long productId,
            @Valid @RequestBody List<ProductAttributeDTO> attributes
    ) {
        log.info("API: Add {} attribute(s) to product ID={}", attributes.size(), productId);
        List<ProductAttributeDTO> createdAttributes =
                productAttributeService.addAttributesToProduct(productId, attributes);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAttributes);
    }

    // ------------------------------------------------------------
    // GET ALL ATTRIBUTES FOR PRODUCT
    // ------------------------------------------------------------
    @GetMapping
    public ResponseEntity<List<ProductAttributeDTO>> getAttributesByProductId(@PathVariable Long productId) {
        log.debug("API: Get attributes for product ID={}", productId);
        List<ProductAttributeDTO> attributes = productAttributeService.getAttributesByProductId(productId);
        return ResponseEntity.ok(attributes);
    }

    // ------------------------------------------------------------
    // UPDATE SINGLE ATTRIBUTE
    // ------------------------------------------------------------
    @PutMapping("/{attributeId}")
    public ResponseEntity<ProductAttributeDTO> updateAttribute(
            @PathVariable Long attributeId,
            @Valid @RequestBody ProductAttributeDTO dto
    ) {
        log.info("API: Update attribute ID={}", attributeId);
        ProductAttributeDTO updated = productAttributeService.updateAttribute(attributeId, dto);
        return ResponseEntity.ok(updated);
    }

    // ------------------------------------------------------------
    // DELETE SINGLE ATTRIBUTE
    // ------------------------------------------------------------
    @DeleteMapping("/{attributeId}")
    public ResponseEntity<Void> deleteAttribute(@PathVariable Long attributeId) {
        log.warn("API: Delete attribute ID={}", attributeId);
        productAttributeService.deleteAttribute(attributeId);
        return ResponseEntity.noContent().build();
    }

    // ------------------------------------------------------------
    // DELETE ALL ATTRIBUTES FOR PRODUCT
    // ------------------------------------------------------------
    @DeleteMapping
    public ResponseEntity<Void> deleteAllAttributes(@PathVariable Long productId) {
        log.warn("API: Delete all attributes for product ID={}", productId);
        productAttributeService.deleteAllAttributesByProductId(productId);
        return ResponseEntity.noContent().build();
    }
}
