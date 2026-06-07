package com.example.full.auth.and.jwt.dto;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ImageResponse {

    private String url;
    private String publicId;
}