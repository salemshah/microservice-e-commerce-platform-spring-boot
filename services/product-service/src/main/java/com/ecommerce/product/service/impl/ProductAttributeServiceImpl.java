package com.ecommerce.product.service.impl;

import com.ecommerce.product.dto.ProductAttributeDTO;
import com.ecommerce.product.entity.Product;
import com.ecommerce.product.entity.ProductAttribute;
import com.ecommerce.product.exception.NotFoundException;
import com.ecommerce.product.mapper.ProductAttributeMapper;
import com.ecommerce.product.repository.ProductAttributeRepository;
import com.ecommerce.product.repository.ProductRepository;
import com.ecommerce.product.service.ProductAttributeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of ProductAttributeService.
 * Manages attribute CRUD and relationships to product.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProductAttributeServiceImpl implements ProductAttributeService {

    private final ProductRepository productRepository;
    private final ProductAttributeRepository productAttributeRepository;
    private final ProductAttributeMapper productAttributeMapper;

    // ---------------------------------
    // ADD ATTRIBUTES TO PRODUCT
    // ---------------------------------
    @Override
    public List<ProductAttributeDTO> addAttributesToProduct(Long productId, List<ProductAttributeDTO> attributes) {
        log.info("Adding {} attribute(s) to product ID={}", attributes.size(), productId);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product not found with ID: " + productId));

        List<ProductAttribute> attributeEntities = attributes.stream()
                .map(productAttributeMapper::toProductAttribute)
                .peek(attr -> attr.setProduct(product))
                .collect(Collectors.toList());

        List<ProductAttribute> saved = productAttributeRepository.saveAll(attributeEntities);

        log.info("Successfully added {} attribute(s) to product {}", saved.size(), productId);
        return saved.stream().map(productAttributeMapper::toProductAttributeDTO).collect(Collectors.toList());
    }

    // ---------------------------------
    // GET ATTRIBUTES BY PRODUCT
    // ---------------------------------
    @Override
    @Transactional(readOnly = true)
    public List<ProductAttributeDTO> getAttributesByProductId(Long productId) {
        log.debug("Fetching attributes for product ID={}", productId);
        if (!productRepository.existsById(productId)) {
            throw new NotFoundException("Product not found with ID: " + productId);
        }
        return productAttributeRepository.findByProductId(productId).stream()
                .map(productAttributeMapper::toProductAttributeDTO)
                .collect(Collectors.toList());
    }

    // ---------------------------------
    // GET ATTRIBUTE BY ID
    // ---------------------------------
    @Override
    @Transactional(readOnly = true)
    public ProductAttributeDTO getAttributeById(Long attributeId) {
        ProductAttribute attribute = productAttributeRepository.findById(attributeId)
                .orElseThrow(() -> new NotFoundException("Product attribute not found with ID: " + attributeId));
        return productAttributeMapper.toProductAttributeDTO(attribute);
    }

    // ---------------------------------
    // UPDATE ATTRIBUTE
    // ---------------------------------
    @Override
    public ProductAttributeDTO updateAttribute(Long attributeId, ProductAttributeDTO dto) {
        log.info("Updating product attribute ID={}", attributeId);

        ProductAttribute attribute = productAttributeRepository.findById(attributeId)
                .orElseThrow(() -> new NotFoundException("Product attribute not found with ID: " + attributeId));

        attribute.setAttributeName(dto.getAttributeName());
        attribute.setAttributeValue(dto.getAttributeValue());

        ProductAttribute updated = productAttributeRepository.save(attribute);
        log.info("Attribute updated successfully: id={}, name={}", updated.getId(), updated.getAttributeName());

        return productAttributeMapper.toProductAttributeDTO(updated);
    }

    // ---------------------------------
    // DELETE ATTRIBUTE
    // ---------------------------------
    @Override
    public void deleteAttribute(Long attributeId) {
        log.warn("Deleting product attribute ID={}", attributeId);
        ProductAttribute attribute = productAttributeRepository.findById(attributeId)
                .orElseThrow(() -> new NotFoundException("Product attribute not found with ID: " + attributeId));

        productAttributeRepository.delete(attribute);
        log.info("Attribute deleted successfully: id={}", attributeId);
    }

    // ---------------------------------
    // DELETE ALL ATTRIBUTES BY PRODUCT
    // ---------------------------------
    @Override
    public void deleteAllAttributesByProductId(Long productId) {
        log.warn("Deleting all attributes for product ID={}", productId);
        if (!productRepository.existsById(productId)) {
            throw new NotFoundException("Product not found with ID: " + productId);
        }
        List<ProductAttribute> attributes = productAttributeRepository.findByProductId(productId);
        productAttributeRepository.deleteAll(attributes);
        log.info("{} attribute(s) deleted for product ID={}", attributes.size(), productId);
    }
}
