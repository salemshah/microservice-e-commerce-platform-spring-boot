package com.ecommerce.product.controller;

import com.ecommerce.product.dto.ReviewDTO;
import com.ecommerce.product.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ReviewController
 *
 * Handles REST operations for managing product reviews.
 * Supports adding, listing, updating, and deleting reviews.
 */
@RestController
@RequestMapping("/api/products/{productId}/reviews")
@RequiredArgsConstructor
@Slf4j
public class ReviewController {

    private final ReviewService reviewService;

    // ------------------------------------------------------------
    // ADD A NEW REVIEW
    // ------------------------------------------------------------
    @PostMapping
    public ResponseEntity<ReviewDTO> addReview(
            @PathVariable Long productId,
            @Valid @RequestBody ReviewDTO dto
    ) {
        log.info("API: Add review for product ID={} by user={}", productId, dto.getUserId());
        ReviewDTO created = reviewService.addReview(productId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // ------------------------------------------------------------
    // GET ALL REVIEWS FOR A PRODUCT
    // ------------------------------------------------------------
    @GetMapping
    public ResponseEntity<List<ReviewDTO>> getReviewsByProduct(@PathVariable Long productId) {
        log.debug("API: Get all reviews for product ID={}", productId);
        List<ReviewDTO> reviews = reviewService.getReviewsByProductId(productId);
        return ResponseEntity.ok(reviews);
    }

    // ------------------------------------------------------------
    // GET REVIEWS BY USER
    // ------------------------------------------------------------
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ReviewDTO>> getReviewsByUser(@PathVariable String userId) {
        log.debug("API: Get all reviews by user ID={}", userId);
        List<ReviewDTO> reviews = reviewService.getReviewsByUserId(userId);
        return ResponseEntity.ok(reviews);
    }

    // ------------------------------------------------------------
    // UPDATE A REVIEW
    // ------------------------------------------------------------
    @PutMapping("/{reviewId}")
    public ResponseEntity<ReviewDTO> updateReview(
            @PathVariable Long reviewId,
            @Valid @RequestBody ReviewDTO dto
    ) {
        log.info("API: Update review ID={}", reviewId);
        ReviewDTO updated = reviewService.updateReview(reviewId, dto);
        return ResponseEntity.ok(updated);
    }

    // ------------------------------------------------------------
    // DELETE A REVIEW
    // ------------------------------------------------------------
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long reviewId) {
        log.warn("API: Delete review ID={}", reviewId);
        reviewService.deleteReview(reviewId);
        return ResponseEntity.noContent().build();
    }

    // ------------------------------------------------------------
    // CHECK IF USER REVIEWED PRODUCT
    // ------------------------------------------------------------
    @GetMapping("/exists")
    public ResponseEntity<Boolean> hasUserReviewedProduct(
            @PathVariable Long productId,
            @RequestParam String userId
    ) {
        boolean hasReviewed = reviewService.hasUserReviewedProduct(productId, userId);
        return ResponseEntity.ok(hasReviewed);
    }
}
