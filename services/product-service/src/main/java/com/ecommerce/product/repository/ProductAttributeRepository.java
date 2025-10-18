package com.ecommerce.product.repository;

import com.ecommerce.product.entity.ProductAttribute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductAttributeRepository extends JpaRepository<ProductAttribute, Long> {

    List<ProductAttribute> findByProductId(Long productId);

    List<ProductAttribute> findByAttributeNameIgnoreCase(String attributeName);
}
