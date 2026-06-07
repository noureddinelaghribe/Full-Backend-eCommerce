package com.example.full.auth.and.jwt.model;

public enum OrderStatus {
    PENDING,
    CONFIRMED,
    SHIPPED,
    DELIVERED,
    CANCELLED
}

/*

PENDING
 ├──> CONFIRMED
 │      ├──> SHIPPED
 │      │      └──> DELIVERED (final)
 │      └──> CANCELLED (final)
 └──> CANCELLED (final)

*/