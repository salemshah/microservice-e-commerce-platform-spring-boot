package com.ecommerce.product.controller;

import com.ecommerce.product.dto.CategoryDTO;
import com.ecommerce.product.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * CategoryController
 *
 * Exposes REST endpoints for managing product categories.
 * Handles CRUD operations and category hierarchy (parent/subcategories).
 */
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Slf4j
public class CategoryController {

    private final CategoryService categoryService;

    // ------------------------------------------------------
    // CREATE CATEGORY
    // ------------------------------------------------------
    @PostMapping
    public ResponseEntity<CategoryDTO> createCategory(@Valid @RequestBody CategoryDTO dto) {
        log.info("API: Create category - {}", dto.getName());
        CategoryDTO created = categoryService.createCategory(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // ------------------------------------------------------
    // GET ALL CATEGORIES
    // ------------------------------------------------------
    @GetMapping
    public ResponseEntity<List<CategoryDTO>> getAllCategories() {
        log.debug("API: Get all categories");
        List<CategoryDTO> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    // ------------------------------------------------------
    // GET ROOT CATEGORIES (no parent)
    // ------------------------------------------------------
    @GetMapping("/roots")
    public ResponseEntity<List<CategoryDTO>> getRootCategories() {
        log.debug("API: Get root categories");
        List<CategoryDTO> rootCategories = categoryService.getRootCategories();
        return ResponseEntity.ok(rootCategories);
    }

    // ------------------------------------------------------
    // GET SUBCATEGORIES
    // ------------------------------------------------------
    @GetMapping("/{parentId}/subcategories")
    public ResponseEntity<List<CategoryDTO>> getSubcategories(@PathVariable Long parentId) {
        log.debug("API: Get subcategories for parentId={}", parentId);
        List<CategoryDTO> subcategories = categoryService.getSubcategories(parentId);
        return ResponseEntity.ok(subcategories);
    }

    // ------------------------------------------------------
    // GET CATEGORY BY ID
    // ------------------------------------------------------
    @GetMapping("/{id}")
    public ResponseEntity<CategoryDTO> getCategoryById(@PathVariable Long id) {
        log.debug("API: Get category by ID={}", id);
        CategoryDTO category = categoryService.getCategoryById(id);
        return ResponseEntity.ok(category);
    }

    // ------------------------------------------------------
    // UPDATE CATEGORY
    // ------------------------------------------------------
    @PutMapping("/{id}")
    public ResponseEntity<CategoryDTO> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CategoryDTO dto) {

        log.info("API: Update category ID={}", id);
        CategoryDTO updated = categoryService.updateCategory(id, dto);
        return ResponseEntity.ok(updated);
    }

    // ------------------------------------------------------
    // DELETE CATEGORY
    // ------------------------------------------------------
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        log.warn("API: Delete category ID={}", id);
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

    // ------------------------------------------------------
    // CHECK CATEGORY EXISTS
    // ------------------------------------------------------
    @GetMapping("/{id}/exists")
    public ResponseEntity<Boolean> categoryExists(@PathVariable Long id) {
        boolean exists = categoryService.existsById(id);
        return ResponseEntity.ok(exists);
    }

    // ------------------------------------------------------
    // GET CATEGORIES BY PRODUCT ID
    // ------------------------------------------------------
    @GetMapping("/product/{productId}")
    public ResponseEntity<List<CategoryDTO>> getCategoriesByProduct(@PathVariable Long productId) {
        log.debug("API: Get categories for product ID={}", productId);
        List<CategoryDTO> categories = categoryService.getCategoriesByProductId(productId);
        return ResponseEntity.ok(categories);
    }
}
