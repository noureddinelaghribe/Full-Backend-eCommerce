package com.example.full.auth.and.jwt.repository;

import com.example.full.auth.and.jwt.model.OrderItem;
import com.example.full.auth.and.jwt.model.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {


    // للتحقق من ownership عند التحديث: seller لا يعدل إلا item يخصه
    Optional<OrderItem> findByIdAndProductSellerId(Long itemId, Long sellerId);
    // لعرض كل العناصر الخاصة ببائع معين
    Page<OrderItem> findByProductSellerId(Long sellerId, Pageable pageable);

    boolean existsByOrder_User_IdAndProduct_IdAndStatus(
            Long userId,
            Long productId,
            OrderStatus status
    );

}