package com.example.full.auth.and.jwt.controller;

import com.example.full.auth.and.jwt.dto.CartCreateRequest;
import com.example.full.auth.and.jwt.dto.CartUpdateRequest;
import com.example.full.auth.and.jwt.dto.CartResponse;
import com.example.full.auth.and.jwt.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 🛒 CartController - واجهة REST لإدارة سلة المشتريات
 *
 * الوصف:
 * - تمكّن المستخدم من إدارة عناصر السلة الخاصة به
 * - جميع المسارات تحت المسار الأساسي /api/cart
 *
 * أهم العمليات:
 * - GET  /api/cart              → جلب عناصر السلة للمستخدم الحالي
 * - POST /api/cart              → إضافة منتج إلى السلة
 * - PUT  /api/cart/{cartId}     → تعديل كمية منتج في السلة
 * - PUT  /api/cart/Increase/{id}→ زيادة كمية منتج
 * - PUT  /api/cart/Decrease/{id}→ إنقاص كمية منتج
 * - DELETE /api/cart/{cartId}   → حذف عنصر من السلة
 * - DELETE /api/cart            → مسح كل السلة للمستخدم الحالي
 */
@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    /**
     * GET /api/cart - Get all cart items for the current user
     */
    @GetMapping
    public ResponseEntity<Page<CartResponse>> getMyCart(
            @PageableDefault(page = 0, size = 5, sort = "id", direction = Sort.Direction.ASC)
            Pageable pageable
    ) {
        return ResponseEntity.ok(cartService.getMyCartProducts(pageable));
    }


//    @GetMapping("/user/{id}")
//    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
//    public ResponseEntity<Page<CartResponse>> getUserCart(
//            Long id,
//            @PageableDefault(page = 0, size = 5, sort = "id", direction = Sort.Direction.ASC)
//            Pageable pageable
//    ) {
//        return ResponseEntity.ok(cartService.getUserCartProducts(id, pageable));
//    }


    /**
     * POST /api/cart - Add a product to cart
     */
//    @PostMapping
//    public ResponseEntity<CartResponse> addToCart(@Valid @RequestBody CartRequest request) {
//        CartResponse cartResponse = cartService.createCart(request);
//        return ResponseEntity.status(HttpStatus.CREATED).body(cartResponse);
//    }

    @PostMapping
    public ResponseEntity<CartResponse> addToCart(@Valid @RequestBody CartCreateRequest request) {
        CartResponse cartResponse = cartService.addToCart(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(cartResponse);
    }

    /**
     * PUT /api/cart/{cartId} - Update cart item quantity
     */
    @PutMapping("/{cartId}")
    public ResponseEntity<CartResponse> updateCartItem(
            @PathVariable Long cartId,
            @Valid @RequestBody CartUpdateRequest request) {
        CartResponse cartResponse = cartService.updateCart(cartId, request);
        return ResponseEntity.ok(cartResponse);
    }

    @PutMapping("/Increase/{cartId}")
    public ResponseEntity<CartResponse> updateProductCartIncrease(
            @PathVariable Long cartId
    ) {
        CartResponse cartResponse = cartService.updateProductCartIncrease(cartId);
        return ResponseEntity.ok(cartResponse);
    }

    @PutMapping("/Decrease/{cartId}")
    public ResponseEntity<CartResponse> updateProductCartDecrease(
            @PathVariable Long cartId
    ) {
        CartResponse cartResponse = cartService.updateProductCartDecrease(cartId);
        return ResponseEntity.ok(cartResponse);
    }

    /**
     * DELETE /api/cart/{cartId} - Remove item from cart
     */
    @DeleteMapping("/{cartId}")
    public ResponseEntity<Void> removeFromCart(@PathVariable Long cartId) {
        cartService.deleteCart(cartId);
        return ResponseEntity.noContent().build();
    }


    /**
     * DELETE /api/cart - Clear all cart items for current user
     */
    @DeleteMapping
    public ResponseEntity<Void> clearCart() {
        cartService.clearCart();
        return ResponseEntity.noContent().build();
    }

}
