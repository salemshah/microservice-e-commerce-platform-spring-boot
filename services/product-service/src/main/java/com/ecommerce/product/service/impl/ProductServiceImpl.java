package com.ecommerce.product.service.impl;

import com.ecommerce.product.dto.*;
import com.ecommerce.product.entity.*;
import com.ecommerce.product.exception.NotFoundException;
import com.ecommerce.product.mapper.*;
import com.ecommerce.product.repository.*;
import com.ecommerce.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of ProductService.
 * Handles full product CRUD operations with mapping, validation, and relationships.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;
    private final ProductImageMapper productImageMapper;
    private final ProductAttributeMapper productAttributeMapper;

    // ----------------------------
    // CREATE PRODUCT
    // ----------------------------
    @Override
    public ProductResponse createProduct(ProductRequest request) {
        log.info("Creating new product: {}", request.getName());

        // Step 1: Validate and fetch categories
        List<Category> categories = getValidatedCategories(request.getCategoryIds());

        // Step 2: Map basic fields
        Product product = productMapper.toProduct(request);

        // Step 3: Map and attach categories through ProductCategory
        Set<ProductCategory> productCategories = categories.stream()
                .map(category -> ProductCategory.builder()
                        .product(product)
                        .category(category)
                        .displayOrder(0) // default order
                        .build())
                .collect(Collectors.toSet());
        product.setProductCategories(productCategories);

        // Step 4: Map and attach images
        if (request.getImages() != null) {
            Set<ProductImage> images = request.getImages().stream()
                    .map(productImageMapper::toProductImage)
                    .peek(img -> img.setProduct(product))
                    .collect(Collectors.toSet());
            product.setImages(images);
        }

        // Step 5: Map and attach attributes
        if (request.getAttributes() != null) {
            Set<ProductAttribute> attributes = request.getAttributes().stream()
                    .map(productAttributeMapper::toProductAttribute)
                    .peek(attr -> attr.setProduct(product))
                    .collect(Collectors.toSet());
            product.setAttributes(attributes);
        }

        // Step 6: Save
        Product saved = productRepository.save(product);

        log.info("Product created successfully: id={}, sku={}", saved.getId(), saved.getSku());
        return productMapper.toProductResponse(saved);
    }

    // ----------------------------
    // GET BY ID
    // ----------------------------
    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product not found with id: " + id));
        return productMapper.toProductResponse(product);
    }

    // ----------------------------
    // GET BY SLUG
    // ----------------------------
    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProductBySlug(String slug) {
        Product product = productRepository.findBySlug(slug)
                .orElseThrow(() -> new NotFoundException("Product not found with slug: " + slug));
        return productMapper.toProductResponse(product);
    }

    // ----------------------------
    // GET BY SKU
    // ----------------------------
    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProductBySku(String sku) {
        Product product = productRepository.findBySku(sku)
                .orElseThrow(() -> new NotFoundException("Product not found with SKU: " + sku));
        return productMapper.toProductResponse(product);
    }

    // ----------------------------
    // UPDATE PRODUCT
    // ----------------------------
    @Override
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        log.info("Updating product id={}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product not found with id: " + id));

        // Step 1: Update base fields
        productMapper.updateProductFromRequest(request, product);

        // Step 2: Update categories
        if (request.getCategoryIds() != null && !request.getCategoryIds().isEmpty()) {
            List<Category> categories = getValidatedCategories(request.getCategoryIds());

            // Clear old relationships and add new ones
            product.getProductCategories().clear();
            categories.forEach(category -> {
                ProductCategory pc = ProductCategory.builder()
                        .product(product)
                        .category(category)
                        .displayOrder(0)
                        .build();
                product.getProductCategories().add(pc);
            });
        }

        // Step 3: Replace images
        if (request.getImages() != null) {
            product.getImages().clear();
            request.getImages().forEach(imgDTO -> {
                ProductImage img = productImageMapper.toProductImage(imgDTO);
                img.setProduct(product);
                product.getImages().add(img);
            });
        }

        // Step 4: Replace attributes
        if (request.getAttributes() != null) {
            product.getAttributes().clear();
            request.getAttributes().forEach(attrDTO -> {
                ProductAttribute attr = productAttributeMapper.toProductAttribute(attrDTO);
                attr.setProduct(product);
                product.getAttributes().add(attr);
            });
        }

        product.setUpdatedAt(java.time.LocalDateTime.now());

        Product updated = productRepository.save(product);
        log.info("Product updated successfully: id={}", updated.getId());
        return productMapper.toProductResponse(updated);
    }

    // ----------------------------
    // DELETE PRODUCT
    // ----------------------------
    @Override
    public void deleteProduct(Long id) {
        log.info("Deleting product id={}", id);
        if (!productRepository.existsById(id)) {
            throw new NotFoundException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
    }

    // ----------------------------
    // SEARCH PRODUCTS
    // ----------------------------
    @Override
    @Transactional(readOnly = true)
    public Page<ProductSummaryDTO> searchProducts(
            String keyword,
            Long categoryId,
            String brand,
            String status,
            BigDecimal priceMin,
            BigDecimal priceMax,
            Pageable pageable) {

        log.debug("Searching products: keyword={}, brand={}, category={}, status={}",
                keyword, brand, categoryId, status);

        // TODO: Implement dynamic filtering (Specification/QueryDSL)
        Page<Product> page = productRepository.findAll(pageable);

        return page.map(product -> ProductSummaryDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .slug(product.getSlug())
                .price(product.getPrice())
                .discountPrice(product.getDiscountPrice())
                .currency(product.getCurrency())
                .brand(product.getBrand())
                .thumbnailUrl(getPrimaryImageUrl(product))
                .build());
    }

    // ----------------------------
    // SKU EXISTENCE CHECK
    // ----------------------------
    @Override
    @Transactional(readOnly = true)
    public boolean existsBySku(String sku) {
        return productRepository.findBySku(sku).isPresent();
    }

    // ----------------------------
    // FIND ID BY SLUG
    // ----------------------------
    @Override
    @Transactional(readOnly = true)
    public Optional<Long> findIdBySlug(String slug) {
        return productRepository.findBySlug(slug).map(Product::getId);
    }

    // ----------------------------
    // PRIVATE UTILITIES
    // ----------------------------
    private List<Category> getValidatedCategories(List<Long> categoryIds) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            throw new IllegalArgumentException("Product must belong to at least one category.");
        }

        List<Category> categories = categoryRepository.findAllById(categoryIds);
        if (categories.size() != categoryIds.size()) {
            throw new NotFoundException("One or more categories not found for IDs: " + categoryIds);
        }
        return categories;
    }

    private String getPrimaryImageUrl(Product product) {
        return product.getImages().stream()
                .filter(ProductImage::isPrimary)
                .map(ProductImage::getImageUrl)
                .findFirst()
                .orElseGet(() -> product.getImages().stream()
                        .findFirst()
                        .map(ProductImage::getImageUrl)
                        .orElse(null));
    }
}
