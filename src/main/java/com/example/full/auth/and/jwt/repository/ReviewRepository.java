package com.example.full.auth.and.jwt.repository;

import com.example.full.auth.and.jwt.model.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    // هل قيّم هذا المستخدم هذا المنتج مسبقاً؟
    boolean existsByUserIdAndProductId(Long userId, Long productId);

    // هل اشترى هذا المستخدم هذا المنتج؟
    // (نحتاج OrderRepository للتحقق)

    // 🔥 حساب المتوسط مباشرة من DB
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.product.id = :productId")
    Double findAverageRating(/*@Param("productId")*/ Long productId);

    // عدد التقييمات
    long countByProductId(Long productId);

    // جلب التقييمات مع Pagination
    Page<Review> findByProductId(Long productId, Pageable pageable);
}