package com.example.full.auth.and.jwt.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 🧾 كيان المنتج (Product)
 *
 * الوصف:
 * - يمثل منتج واحد داخل النظام (مثل منتج في متجر إلكتروني)
 * - يحتوي على بيانات أساسية مثل الاسم، الوصف، السعر، المخزون، وصورة للمنتج
 * - مرتبط ببائع واحد (User) عن طريق الحقل {@code seller}
 *
 * الحقول الرئيسية:
 * - id: المعرف الفريد للمنتج
 * - name: اسم المنتج
 * - description: وصف تفصيلي للمنتج
 * - price: سعر المنتج
 * - stock: الكمية المتوفرة في المخزون
 * - imageUrl: رابط صورة المنتج
 * - seller: المستخدم (البائع) الذي أضاف هذا المنتج
 */
@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private Integer stock;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    private boolean deleted = false;

}