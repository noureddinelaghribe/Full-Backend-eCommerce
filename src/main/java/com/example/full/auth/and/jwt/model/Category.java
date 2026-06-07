package com.example.full.auth.and.jwt.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "categories")
@Data // 🔥 من Lombok: ينشئ getters / setters / toString / equals / hashCode تلقائياً
@NoArgsConstructor // 🏭 ينشئ Constructor فارغ ()
@AllArgsConstructor // 🏭 ينشئ Constructor بجميع المعاملات
@Builder
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;   // مثل: "Electronics", "Clothes"

    private String description;

    // علاقة Category مع Products (اختياري - تُفعَّل عند الحاجة)
//    @JsonIgnore
//    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<Product> products = new ArrayList<>();

}
