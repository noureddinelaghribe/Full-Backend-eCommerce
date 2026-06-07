package com.example.full.auth.and.jwt.dto;

import java.math.BigDecimal;

public record ProductFilterRequest(
        String name,        // اختياري — لو null أو فارغ لا يُفلتر
        String category,    // اختياري
        BigDecimal minPrice,// اختياري
        BigDecimal maxPrice,// اختياري
        Boolean inStock     // اختياري — true = متوفر فقط
) {}