package com.ecommerce.product.repository;

import com.ecommerce.product.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByProductId(Long productId);

    List<Review> findByUserId(String userId);

    boolean existsByProductIdAndUserId(Long productId, String userId);
}
