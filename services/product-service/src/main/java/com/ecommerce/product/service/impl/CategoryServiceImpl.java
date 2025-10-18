package com.ecommerce.product.service.impl;

import com.ecommerce.product.dto.CategoryDTO;
import com.ecommerce.product.entity.Category;
import com.ecommerce.product.exception.NotFoundException;
import com.ecommerce.product.mapper.CategoryMapper;
import com.ecommerce.product.repository.CategoryRepository;
import com.ecommerce.product.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of CategoryService.
 * Handles CRUD operations and category hierarchy logic.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    // ---------------------------------
    // CREATE CATEGORY
    // ---------------------------------
    @Override
    public CategoryDTO createCategory(CategoryDTO dto) {
        log.info("Creating new category: {}", dto.getName());

        Category category = categoryMapper.toCategory(dto);

        // Handle parent category if provided
        if (dto.getParentId() != null) {
            Category parent = categoryRepository.findById(dto.getParentId())
                    .orElseThrow(() -> new NotFoundException("Parent category not found with ID: " + dto.getParentId()));
            category.setParent(parent);
        }

        category.setCreatedAt(LocalDateTime.now());
        category.setUpdatedAt(LocalDateTime.now());

        Category saved = categoryRepository.save(category);
        log.info("Category created successfully: id={}, name={}", saved.getId(), saved.getName());

        return categoryMapper.toCategoryDTO(saved);
    }

    // ---------------------------------
    // GET ALL CATEGORIES
    // ---------------------------------
    @Override
    @Transactional(readOnly = true)
    public List<CategoryDTO> getAllCategories() {
        log.debug("Fetching all categories");
        return categoryRepository.findAll().stream()
                .map(categoryMapper::toCategoryDTO)
                .collect(Collectors.toList());
    }

    // ---------------------------------
    // GET CATEGORY BY ID
    // ---------------------------------
    @Override
    @Transactional(readOnly = true)
    public CategoryDTO getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category not found with ID: " + id));
        return categoryMapper.toCategoryDTO(category);
    }

    // ---------------------------------
    // GET ROOT CATEGORIES (no parent)
    // ---------------------------------
    @Override
    @Transactional(readOnly = true)
    public List<CategoryDTO> getRootCategories() {
        log.debug("Fetching root categories");
        return categoryRepository.findByParentIsNull().stream()
                .map(categoryMapper::toCategoryDTO)
                .collect(Collectors.toList());
    }

    // ---------------------------------
    // GET SUBCATEGORIES (children)
    // ---------------------------------
    @Override
    @Transactional(readOnly = true)
    public List<CategoryDTO> getSubcategories(Long parentId) {
        log.debug("Fetching subcategories for parent ID={}", parentId);
        if (!categoryRepository.existsById(parentId)) {
            throw new NotFoundException("Parent category not found with ID: " + parentId);
        }

        return categoryRepository.findByParentId(parentId).stream()
                .map(categoryMapper::toCategoryDTO)
                .collect(Collectors.toList());
    }

    // ---------------------------------
    // UPDATE CATEGORY
    // ---------------------------------
    @Override
    public CategoryDTO updateCategory(Long id, CategoryDTO dto) {
        log.info("Updating category id={}", id);

        Category existing = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category not found with ID: " + id));

        existing.setName(dto.getName());
        existing.setSlug(dto.getSlug());
        existing.setDescription(dto.getDescription());
        existing.setUpdatedAt(LocalDateTime.now());

        if (dto.getParentId() != null) {
            Category parent = categoryRepository.findById(dto.getParentId())
                    .orElseThrow(() -> new NotFoundException("Parent category not found with ID: " + dto.getParentId()));
            existing.setParent(parent);
        } else {
            existing.setParent(null);
        }

        Category updated = categoryRepository.save(existing);
        log.info("Category updated successfully: id={}", updated.getId());

        return categoryMapper.toCategoryDTO(updated);
    }

    // ---------------------------------
    // DELETE CATEGORY
    // ---------------------------------
    @Override
    public void deleteCategory(Long id) {
        log.warn("Deleting category id={}", id);
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category not found with ID: " + id));

        // Optionally, handle orphaned subcategories
        if (category.getSubcategories() != null && !category.getSubcategories().isEmpty()) {
            category.getSubcategories().forEach(sub -> sub.setParent(null));
        }

        categoryRepository.delete(category);
    }

    // ---------------------------------
    // EXISTS BY ID
    // ---------------------------------
    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return categoryRepository.existsById(id);
    }

    // ---------------------------------
    // GET CATEGORIES BY PRODUCT ID
    // ---------------------------------
    @Override
    @Transactional(readOnly = true)
    public List<CategoryDTO> getCategoriesByProductId(Long productId) {
        log.debug("Fetching categories for product ID={}", productId);
        List<Category> categories = categoryRepository.findByParentId(productId);
        return categories.stream()
                .map(categoryMapper::toCategoryDTO)
                .collect(Collectors.toList());
    }
}
