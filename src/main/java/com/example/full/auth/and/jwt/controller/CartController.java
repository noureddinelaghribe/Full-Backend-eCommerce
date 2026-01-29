package com.example.full.auth.and.jwt.controller;

import com.example.full.auth.and.jwt.dto.CartResponse;
import com.example.full.auth.and.jwt.dto.ProductResponse;
import com.example.full.auth.and.jwt.service.CartService;
import com.example.full.auth.and.jwt.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

//    @GetMapping
//    public ResponseEntity<List<ProductResponse>> getMyCartProducts() {
//        List<CartResponse> cart = cartService.getMyCartProducts();
//        return ResponseEntity.ok(cart);
//    }

    @GetMapping
    public ResponseEntity<List<CartResponse>> getMyCart() {
        return ResponseEntity.ok(cartService.getMyCartProducts());
    }

}
