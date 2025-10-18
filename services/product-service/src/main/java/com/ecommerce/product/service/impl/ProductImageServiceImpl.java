package com.ecommerce.product.service.impl;

import com.ecommerce.product.dto.ProductImageDTO;
import com.ecommerce.product.entity.Product;
import com.ecommerce.product.entity.ProductImage;
import com.ecommerce.product.exception.NotFoundException;
import com.ecommerce.product.mapper.ProductImageMapper;
import com.ecommerce.product.repository.ProductImageRepository;
import com.ecommerce.product.repository.ProductRepository;
import com.ecommerce.product.service.ProductImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of ProductImageService.
 * Handles CRUD and business logic for product images.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProductImageServiceImpl implements ProductImageService {

    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final ProductImageMapper productImageMapper;

    // -----------------------------------
    // ADD IMAGES TO PRODUCT
    // -----------------------------------
    @Override
    public List<ProductImageDTO> addImagesToProduct(Long productId, List<ProductImageDTO> images) {
        log.info("Adding {} image(s) to product ID={}", images.size(), productId);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product not found with ID: " + productId));

        List<ProductImage> imageEntities = images.stream()
                .map(productImageMapper::toProductImage)
                .peek(img -> img.setProduct(product))
                .collect(Collectors.toList());

        List<ProductImage> savedImages = productImageRepository.saveAll(imageEntities);

        log.info("Successfully added {} image(s) to product {}", savedImages.size(), productId);
        return savedImages.stream().map(productImageMapper::toProductImageDTO).collect(Collectors.toList());
    }

    // -----------------------------------
    // GET IMAGES BY PRODUCT
    // -----------------------------------
    @Override
    @Transactional(readOnly = true)
    public List<ProductImageDTO> getImagesByProductId(Long productId) {
        log.debug("Fetching images for product ID={}", productId);
        if (!productRepository.existsById(productId)) {
            throw new NotFoundException("Product not found with ID: " + productId);
        }
        return productImageRepository.findByProductIdOrderByPositionAsc(productId)
                .stream()
                .map(productImageMapper::toProductImageDTO)
                .collect(Collectors.toList());
    }

    // -----------------------------------
    // GET IMAGE BY ID
    // -----------------------------------
    @Override
    @Transactional(readOnly = true)
    public ProductImageDTO getImageById(Long imageId) {
        ProductImage image = productImageRepository.findById(imageId)
                .orElseThrow(() -> new NotFoundException("Product image not found with ID: " + imageId));
        return productImageMapper.toProductImageDTO(image);
    }

    // -----------------------------------
    // UPDATE IMAGE
    // -----------------------------------
    @Override
    public ProductImageDTO updateImage(Long imageId, ProductImageDTO dto) {
        log.info("Updating image ID={}", imageId);

        ProductImage image = productImageRepository.findById(imageId)
                .orElseThrow(() -> new NotFoundException("Product image not found with ID: " + imageId));

        // Update fields
        image.setImageUrl(dto.getImageUrl());
        image.setAltText(dto.getAltText());
        image.setPrimary(dto.isPrimary());
        image.setPosition(dto.getPosition());

        ProductImage updated = productImageRepository.save(image);

        // Ensure only one primary image per product
        if (dto.isPrimary() && updated.getProduct() != null) {
            setPrimaryImage(updated.getProduct().getId(), updated.getId());
        }

        log.info("Image updated successfully: id={}, productId={}", updated.getId(), updated.getProduct().getId());
        return productImageMapper.toProductImageDTO(updated);
    }

    // -----------------------------------
    // DELETE IMAGE
    // -----------------------------------
    @Override
    public void deleteImage(Long imageId) {
        log.warn("Deleting product image ID={}", imageId);
        ProductImage image = productImageRepository.findById(imageId)
                .orElseThrow(() -> new NotFoundException("Product image not found with ID: " + imageId));

        productImageRepository.delete(image);
    }

    // -----------------------------------
    // SET PRIMARY IMAGE
    // -----------------------------------
    @Override
    public void setPrimaryImage(Long productId, Long imageId) {
        log.info("Setting image ID={} as primary for product ID={}", imageId, productId);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product not found with ID: " + productId));

        // Ensure image belongs to the product
        ProductImage image = productImageRepository.findById(imageId)
                .orElseThrow(() -> new NotFoundException("Product image not found with ID: " + imageId));

        if (!image.getProduct().getId().equals(product.getId())) {
            throw new IllegalArgumentException("Image does not belong to the specified product.");
        }

        // Unset any existing primary images
        List<ProductImage> productImages = productImageRepository.findByProductId(productId);
        for (ProductImage img : productImages) {
            img.setPrimary(img.getId().equals(imageId));
        }

        productImageRepository.saveAll(productImages);
        log.info("Primary image set successfully: productId={}, imageId={}", productId, imageId);
    }

    // -----------------------------------
    // DELETE ALL IMAGES BY PRODUCT ID
    // -----------------------------------
    @Override
    public void deleteAllImagesByProductId(Long productId) {
        log.warn("Deleting all images for product ID={}", productId);
        if (!productRepository.existsById(productId)) {
            throw new NotFoundException("Product not found with ID: " + productId);
        }
        List<ProductImage> images = productImageRepository.findByProductId(productId);
        productImageRepository.deleteAll(images);
        log.info("{} image(s) deleted for product ID={}", images.size(), productId);
    }
}
