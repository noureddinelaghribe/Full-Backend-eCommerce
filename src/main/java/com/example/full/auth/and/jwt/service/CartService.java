package com.example.full.auth.and.jwt.service;

import com.example.full.auth.and.jwt.dto.CartResponse;
import com.example.full.auth.and.jwt.dto.ProductResponse;
import com.example.full.auth.and.jwt.model.Cart;
import com.example.full.auth.and.jwt.model.User;
import com.example.full.auth.and.jwt.repository.CartRepository;
import com.example.full.auth.and.jwt.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;


    // ✅ الميثود الأساسية: إرجاع سلة المستخدم الحالي
    public List<CartResponse> getMyCartProducts() {
        // 1) نجيب المستخدم الحالي من الـSecurityContext
        User currentUser = getCurrentUser();

        // 2) نجيب كل عناصر السلة لهذا المستخدم
        return cartRepository.findByUser(currentUser).stream()
                // 3) نحول كل Cart إلى CartResponse
                .map(this::convertToCartResponse)
                .collect(Collectors.toList());
    }

    // ✅ تحويل كيان Cart إلى DTO CartResponse
    private CartResponse convertToCartResponse(Cart cart) {
        return CartResponse.builder()
                .id(cart.getId())
                .quantity(cart.getQuantity())
                .user_id(cart.getUser().getId())
                .product_id(cart.getProduct().getId())
                .build();
    }

    // ✅ نفس فكرة getCurrentUser في ProductService
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || authentication.getPrincipal() instanceof String) {
            throw new AccessDeniedException("Authentication required");
        }
        return (User) authentication.getPrincipal();
    }
}

