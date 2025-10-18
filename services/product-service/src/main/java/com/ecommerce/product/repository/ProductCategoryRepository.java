package com.ecommerce.product.repository;

import com.ecommerce.product.entity.ProductCategory;
import com.ecommerce.product.entity.ProductCategoryId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductCategoryRepository extends JpaRepository<ProductCategory, ProductCategoryId> {

    List<ProductCategory> findByProductId(Long productId);

    List<ProductCategory> findByCategoryId(Long categoryId);
}
