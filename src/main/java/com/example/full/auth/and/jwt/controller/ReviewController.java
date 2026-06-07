package com.example.full.auth.and.jwt.controller;

import com.example.full.auth.and.jwt.dto.ReviewRequest;
import com.example.full.auth.and.jwt.dto.ReviewResponse;
import com.example.full.auth.and.jwt.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    // ✅ إضافة تقييم
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ReviewResponse> addReview(
            @Valid @RequestBody ReviewRequest request) {

        return ResponseEntity.ok(
                reviewService.addReview(request));
    }

    // ✅ جلب تقييمات منتج (مع Pagination)
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



}