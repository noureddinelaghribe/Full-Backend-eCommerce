package com.example.full.auth.and.jwt.dto;

import com.example.full.auth.and.jwt.model.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO لعنصر واحد داخل الطلب (OrderItem)
 * يحتوي على بيانات المنتج بشكل مختصر بدون دورة لانهائية
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemResponse {

    private Long id;
    private Integer quantity;

    // بيانات المنتج المختصرة فقط
    private Long productId;
    private String productName;
    private BigDecimal productPrice;
    private String productImageUrl;
    private Long categoryId;
    private String categoryName;

    private OrderStatus status;

}
