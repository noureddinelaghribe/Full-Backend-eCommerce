package com.example.full.auth.and.jwt.controller.admin;


import com.example.full.auth.and.jwt.dto.CartResponse;
import com.example.full.auth.and.jwt.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/cart")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")  // class-level: all endpoints admin-only
public class AdminCartController {

    private final CartService cartService;


    @GetMapping("/user/{id}")
    public ResponseEntity<Page<CartResponse>> getUserCart(
            Long id,
            @PageableDefault(page = 0, size = 5, sort = "id", direction = Sort.Direction.ASC)
            Pageable pageable
    ) {
        return ResponseEntity.ok(cartService.getUserCartProducts(id, pageable));
    }


}
