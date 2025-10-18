package com.ecommerce.product.service.impl;

import com.ecommerce.product.dto.ReviewDTO;
import com.ecommerce.product.entity.Product;
import com.ecommerce.product.entity.Review;
import com.ecommerce.product.exception.BadRequestException;
import com.ecommerce.product.exception.NotFoundException;
import com.ecommerce.product.mapper.ReviewMapper;
import com.ecommerce.product.repository.ProductRepository;
import com.ecommerce.product.repository.ReviewRepository;
import com.ecommerce.product.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of ReviewService.
 * Handles CRUD operations for product reviews.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final ReviewMapper reviewMapper;

    // ---------------------------------
    // ADD REVIEW
    // ---------------------------------
    @Override
    public ReviewDTO addReview(Long productId, ReviewDTO dto) {
        log.info("Adding review for product ID={} by user={}", productId, dto.getUserId());

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product not found with ID: " + productId));

        if (reviewRepository.existsByProductIdAndUserId(productId, dto.getUserId())) {
            throw new BadRequestException("User has already reviewed this product.");
        }

        Review review = reviewMapper.toReview(dto);
        review.setProduct(product);
        review.setCreatedAt(LocalDateTime.now());
        review.setUpdatedAt(LocalDateTime.now());

        Review saved = reviewRepository.save(review);
        log.info("Review added successfully: id={}, productId={}", saved.getId(), productId);

        return reviewMapper.toReviewDTO(saved);
    }

    // ---------------------------------
    // GET REVIEWS BY PRODUCT
    // ---------------------------------
    @Override
    @Transactional(readOnly = true)
    public List<ReviewDTO> getReviewsByProductId(Long productId) {
        log.debug("Fetching reviews for product ID={}", productId);

        if (!productRepository.existsById(productId)) {
            throw new NotFoundException("Product not found with ID: " + productId);
        }

        return reviewRepository.findByProductId(productId).stream()
                .map(reviewMapper::toReviewDTO)
                .collect(Collectors.toList());
    }

    // ---------------------------------
    // GET REVIEWS BY USER
    // ---------------------------------
    @Override
    @Transactional(readOnly = true)
    public List<ReviewDTO> getReviewsByUserId(String userId) {
        log.debug("Fetching reviews by user ID={}", userId);
        return reviewRepository.findByUserId(userId).stream()
                .map(reviewMapper::toReviewDTO)
                .collect(Collectors.toList());
    }

    // ---------------------------------
    // UPDATE REVIEW
    // ---------------------------------
    @Override
    public ReviewDTO updateReview(Long reviewId, ReviewDTO dto) {
        log.info("Updating review ID={}", reviewId);

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new NotFoundException("Review not found with ID: " + reviewId));

        // Optional: add security check later to ensure only owner/admin can update

        review.setRating(dto.getRating());
        review.setComment(dto.getComment());
        review.setVerifiedPurchase(dto.isVerifiedPurchase());
        review.setUpdatedAt(LocalDateTime.now());

        Review updated = reviewRepository.save(review);
        log.info("Review updated successfully: id={}", updated.getId());

        return reviewMapper.toReviewDTO(updated);
    }

    // ---------------------------------
    // DELETE REVIEW
    // ---------------------------------
    @Override
    public void deleteReview(Long reviewId) {
        log.warn("Deleting review ID={}", reviewId);

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new NotFoundException("Review not found with ID: " + reviewId));

        reviewRepository.delete(review);
    }

    // ---------------------------------
    // HAS USER REVIEWED PRODUCT
    // ---------------------------------
    @Override
    @Transactional(readOnly = true)
    public boolean hasUserReviewedProduct(Long productId, String userId) {
        return reviewRepository.existsByProductIdAndUserId(productId, userId);
    }
}
