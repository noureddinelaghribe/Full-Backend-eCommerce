//package com.example.full.auth.and.jwt.model;
//
//import jakarta.persistence.*;
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//import java.util.List;
//
//@Entity
//@Table(name = "checkouts")
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//@Builder
//public class Checkout {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "user_id", nullable = false)
//    private User user;
//
//    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
//    private BigDecimal totalAmount;
//
//    @Column(name = "payment_method", nullable = false, length = 50)
//    private String paymentMethod;
//
//    @Column(name = "payment_status", length = 30)
//    private String paymentStatus = "PENDING";
//
//    @Column(name = "shipping_address", columnDefinition = "TEXT", nullable = false)
//    private String shippingAddress;
//
//    // Status = {PENDING ,CONFIRMED ,SHIPPED ,DELIVERED ,CANCELLED}
//
//    @Column(name = "status", columnDefinition = "TEXT", nullable = false)
//    private String status;
//
//    @Column(name = "created_at", nullable = false, updatable = false)
//    private LocalDateTime createdAt = LocalDateTime.now();
//
//    @Column(name = "updated_at")
//    private LocalDateTime updatedAt = LocalDateTime.now();
//
//    // ✅ العناصر (السلة) المرتبطة بهذا الـ checkout عبر جدول checkouts_carts
//    @ManyToMany
//    @JoinTable(
//            name = "checkouts_carts",
//            joinColumns = @JoinColumn(name = "checkout_id"),
//            inverseJoinColumns = @JoinColumn(name = "cart_id")
//    )
//    private List<Cart> carts;
//
//}
