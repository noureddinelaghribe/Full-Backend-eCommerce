package com.example.full.auth.and.jwt.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressResponse {

    private Long id;

    private Long userId;

    private String street;

    private String city;

    private String state;

    private String country;

    private Boolean isDefault;

    public String getFullAddress() {
        return street + ", " + city + ", " + state + ", " + country;
    }

}