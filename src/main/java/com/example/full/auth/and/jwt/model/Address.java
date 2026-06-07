package com.example.full.auth.and.jwt.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
@Entity
@Table(name = "Addresses")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private User user; // صاحب العنوان

    @Column(name = "street", columnDefinition = "TEXT")
    private String street;

    @Column(name = "city", columnDefinition = "TEXT")
    private String city;

    @Column(name = "state", columnDefinition = "TEXT")
    private String state;

    @Column(name = "country", columnDefinition = "TEXT")
    private String country;

    @Column(name = "isDefault", nullable = false)
    private Boolean isDefault;

}
