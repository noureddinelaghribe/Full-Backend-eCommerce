package com.example.full.auth.and.jwt.dto;

import com.example.full.auth.and.jwt.model.ProductStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for product response
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponse {

    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stock;
    private String imageUrl;
    private Long sellerId;
    private ProductStatus status;
    private String sellerName;
    private Long categoryId;
    private String categoryName;
    private boolean deleted;

}