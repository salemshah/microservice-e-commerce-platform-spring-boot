package com.ecommerce.product.service;

import com.ecommerce.product.dto.ReviewDTO;

import java.util.List;

/**
 * ReviewService
 *
 * Provides operations for managing product reviews.
 */
public interface ReviewService {

    /**
     * Add a new review for a product.
     *
     * @param productId the product ID
     * @param review the review data
     * @return created ReviewDTO
     */
    ReviewDTO addReview(Long productId, ReviewDTO review);

    /**
     * Retrieve all reviews for a given product.
     *
     * @param productId product ID
     * @return list of reviews
     */
    List<ReviewDTO> getReviewsByProductId(Long productId);

    /**
     * Retrieve all reviews made by a specific user.
     *
     * @param userId user ID
     * @return list of reviews
     */
    List<ReviewDTO> getReviewsByUserId(String userId);

    /**
     * Update an existing review (only by its owner or admin).
     *
     * @param reviewId review ID
     * @param dto new review data
     * @return updated ReviewDTO
     */
    ReviewDTO updateReview(Long reviewId, ReviewDTO dto);

    /**
     * Delete a review by its ID.
     *
     * @param reviewId review ID
     */
    void deleteReview(Long reviewId);

    /**
     * Check if a user has already reviewed a product.
     *
     * @param productId product ID
     * @param userId user ID
     * @return true if review exists
     */
    boolean hasUserReviewedProduct(Long productId, String userId);
}
