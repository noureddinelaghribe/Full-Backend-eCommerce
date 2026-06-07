package com.example.full.auth.and.jwt.dto;

import com.example.full.auth.and.jwt.model.ProductStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductStatusResponse {

    private ProductStatus status ;

}
