package com.example.full.auth.and.jwt.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdatePaymentStatusRequest {

    @NotBlank(message = "Payment status is required")
    private String paymentStatus; // مثال قيم: PENDING, PAID, CANCELED
}