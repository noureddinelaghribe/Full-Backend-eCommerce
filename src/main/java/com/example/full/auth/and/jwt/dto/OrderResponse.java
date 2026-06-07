package com.example.full.auth.and.jwt.dto;

import com.example.full.auth.and.jwt.model.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO لاستجابة بيانات الطلب (Order Response)
 * يحتوي على بيانات الطلب بشكل مختصر وآمن بدون دورة لانهائية في JSON
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponse {

    private Long id;
    private BigDecimal totalAmount;
    private String shippingAddress;
    private OrderStatus status;
    private LocalDateTime createdAt;

    // بيانات المستخدم المختصرة
    private Long userId;
    private String userFullName;
    private String userEmail;

    // عناصر الطلب
    private List<OrderItemResponse> items;
}
