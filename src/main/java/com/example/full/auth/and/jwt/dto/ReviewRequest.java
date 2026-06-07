package com.example.full.auth.and.jwt.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewRequest {

    @NotNull
    @Min(1) @Max(5)
    private Integer rating;

    @NotBlank
    @Size(max = 1000)
    private String comment;

    @NotNull
    private Long productId;
}
