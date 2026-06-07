package com.example.full.auth.and.jwt.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 🛒 كيان السلة (Cart)
 *
 * الوصف:
 * - يمثل عنصر واحد داخل سلة المستخدم
 * - يربط بين المستخدم (User) والمنتج (Product)
 * - يحتوي على كمية المنتج داخل السلة
 *
 * الحقول الرئيسية:
 * - id: المعرف الفريد لعنصر السلة
 * - quantity: الكمية المطلوبة من المنتج
 * - user: المستخدم صاحب هذه السلة
 * - product: المنتج المضاف إلى السلة
 */
@Entity
@Table(name = "carts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer quantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

}