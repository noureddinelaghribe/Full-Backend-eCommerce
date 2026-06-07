package com.example.full.auth.and.jwt.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // المستخدم صاحب الطلب
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // المبلغ الكلي
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    // ✅ عنوان الشحن لكل طلب
    @Column(name = "shipping_address", nullable = false, columnDefinition = "TEXT")
    private String shippingAddress;

    // ✅ العناصر (المنتجات) التابعة لهذا الطلب
    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY)
    private List<OrderItem> items;

    // حالة الطلب
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OrderStatus status;

    // وقت إنشاء الطلب
    @Column(nullable = false)
    private LocalDateTime createdAt;
}