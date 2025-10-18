package com.ecommerce.product.service;

import com.ecommerce.product.dto.CategoryDTO;

import java.util.List;

/**
 * CategoryService
 *
 * Provides operations for managing product categories.
 * Handles CRUD and parent-child relationships.
 */
public interface CategoryService {

    /**
     * Create a new category.
     *
     * @param dto the category data
     * @return the created category
     */
    CategoryDTO createCategory(CategoryDTO dto);

    /**
     * Retrieve all categories.
     * Typically used for building category trees or dropdown lists.
     */
    List<CategoryDTO> getAllCategories();

    /**
     * Retrieve a single category by ID.
     *
     * @param id category ID
     * @return CategoryDTO
     * @throws com.ecommerce.product.exception.NotFoundException if not found
     */
    CategoryDTO getCategoryById(Long id);

    /**
     * Retrieve top-level (root) categories.
     * Parent ID = null
     */
    List<CategoryDTO> getRootCategories();

    /**
     * Retrieve subcategories under a given parent category.
     *
     * @param parentId parent category ID
     * @return list of subcategories
     */
    List<CategoryDTO> getSubcategories(Long parentId);

    /**
     * Update a category by ID.
     *
     * @param id  category ID
     * @param dto new data
     * @return updated CategoryDTO
     */
    CategoryDTO updateCategory(Long id, CategoryDTO dto);

    /**
     * Delete a category by ID.
     *
     * @param id category ID
     */
    void deleteCategory(Long id);

    /**
     * Check if a category exists by ID.
     */
    boolean existsById(Long id);

    /**
     * Get all categories assigned to a product.
     * (Optional helper if needed in product service)
     */
    List<CategoryDTO> getCategoriesByProductId(Long productId);
}
