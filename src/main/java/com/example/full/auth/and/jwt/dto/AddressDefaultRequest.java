package com.example.full.auth.and.jwt.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressDefaultRequest {

    @NotNull(message = "isDefault is required")
    private Boolean isDefault;

}
