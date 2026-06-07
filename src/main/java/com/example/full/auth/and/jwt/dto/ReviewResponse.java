package com.example.full.auth.and.jwt.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class ReviewResponse {

    private Long id;
    private String username;
    private int rating;
    private String comment;
    private LocalDateTime createdAt;  // ← الدليل نسيها!
}