package com.example.full.auth.and.jwt.controller.admin;


import com.example.full.auth.and.jwt.dto.CategoryRequest;
import com.example.full.auth.and.jwt.dto.CategoryResponse;
import com.example.full.auth.and.jwt.dto.ReviewRequest;
import com.example.full.auth.and.jwt.dto.ReviewResponse;
import com.example.full.auth.and.jwt.model.Category;
import com.example.full.auth.and.jwt.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/reviews")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")  // class-level: all endpoints admin-only
public class AdminReviewController {

    private final ReviewService reviewService;


    @PostMapping
    public ResponseEntity<ReviewResponse> addReview(
            @Valid @RequestBody ReviewRequest request) {

        return ResponseEntity.ok(
                reviewService.addReview(request));
    }

    @GetMapping("/{productId}")
    public ResponseEntity<Page<ReviewResponse>> getReviews(
            @PathVariable Long productId,
            @PageableDefault(size = 10) Pageable pageable) {

        return ResponseEntity.ok(
                reviewService.getProductReviews(productId, pageable));
    }

    @GetMapping("avg/{productId}")
    public ResponseEntity<Double> AverageRating(
            @PathVariable Long productId) {

        return ResponseEntity.ok(
                reviewService.findAverageRating(productId));
    }

    @GetMapping("count/{productId}")
    public ResponseEntity<Long> countReview(
            @PathVariable Long productId) {

        return ResponseEntity.ok(
                reviewService.countReview(productId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateReview(
            @PathVariable Long id,
            @Valid @RequestBody ReviewRequest request) {
        try {
            ReviewResponse updatedReview = reviewService.updateReview(id, request);
            return ResponseEntity.ok(updatedReview);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<?> deleteReview(@PathVariable Long id){
        try {
            reviewService.deleteReview(id);
            return ResponseEntity.ok(Map.of("message", "Review deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
        }
    }
























}
