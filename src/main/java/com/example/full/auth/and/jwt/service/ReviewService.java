package com.example.full.auth.and.jwt.service;

import com.example.full.auth.and.jwt.dto.*;
import com.example.full.auth.and.jwt.model.OrderStatus;
import com.example.full.auth.and.jwt.model.Product;
import com.example.full.auth.and.jwt.model.Review;
import com.example.full.auth.and.jwt.model.User;
import com.example.full.auth.and.jwt.repository.OrderItemRepository;
import com.example.full.auth.and.jwt.repository.OrderRepository;
import com.example.full.auth.and.jwt.repository.ProductRepository;
import com.example.full.auth.and.jwt.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    public ReviewResponse addReview(ReviewRequest req) {

        User user = getCurrentUser();

        // ✅ 1. هل اشترى المنتج؟ (إلزامي)
//        if (!orderRepository.existsByUserIdAndProductId(user.getId(), req.getProductId())) {
//            throw new RuntimeException("يجب شراء المنتج أولاً");
//        }
        if (!orderItemRepository.existsByOrder_User_IdAndProduct_IdAndStatus(
                user.getId(), req.getProductId(), OrderStatus.DELIVERED)) {
            throw new RuntimeException("يجب استلام المنتج أولاً قبل التقييم");
        }


        // ✅ 2. هل قيّم مسبقاً؟
        if (reviewRepository.existsByUserIdAndProductId(user.getId(), req.getProductId())) {
            throw new RuntimeException("لقد قيّمت هذا المنتج مسبقاً");
        }

        Product product = productRepository.findById(req.getProductId())
                .orElseThrow(() -> new RuntimeException("المنتج غير موجود"));

        Review review = new Review();
        review.setRating(req.getRating());
        review.setComment(req.getComment());
        review.setUser(user);
        review.setProduct(product);
        reviewRepository.save(review);

        return ReviewResponse.builder()
                .id(review.getId())
                .username(user.getFullName())
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .build();
    }



    public Page<ReviewResponse> getProductReviews( Long productId, Pageable pageable) {

        // 2) نجيب المنتجات اللي seller بتاعها = currentUser
        return reviewRepository.findByProductId( productId, pageable)//.stream()
                // 3) نحول كل Product إلى ProductResponse (عندك الميثود convertToProductResponse)
                .map(this::convertToReviewResponse);
        //.collect(Collectors.toList());
    }


    public Double findAverageRating(Long productId) {
        return reviewRepository.findAverageRating( productId);
    }


    public Long countReview(Long productId) {
        return reviewRepository.countByProductId( productId);
    }

    public ReviewResponse updateReview(Long id, ReviewRequest request) {

        Review rewiew = reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        " غير موجود - Rewiew not found with id: " + id
                ));

        rewiew.setRating(request.getRating());
        rewiew.setComment(request.getComment());
        Review reviewupdated = reviewRepository.save(rewiew);

        return convertToReviewResponse(reviewupdated);

    }


    public void deleteReview(Long id) {

        reviewRepository.findById(id).orElseThrow(() -> new RuntimeException("Review not found with id: " + id));
        reviewRepository.deleteById(id);

    }


    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal() instanceof String) {
            throw new AccessDeniedException("Authentication required");
        }
        return (User) authentication.getPrincipal();
    }

    private ReviewResponse convertToReviewResponse(Review review) {
        return ReviewResponse.builder()
                .id(review.getId())
                .username(review.getUser().getFullName())
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .build();
    }


}