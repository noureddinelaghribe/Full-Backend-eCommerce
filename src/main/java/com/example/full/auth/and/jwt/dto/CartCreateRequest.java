package com.example.full.auth.and.jwt.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartCreateRequest {

    @NotNull(message = "Product ID is required")
    @Min(value = 1, message = "Product ID must be valid")
    private Long productId; // Renamed from 'product' for clarity

}