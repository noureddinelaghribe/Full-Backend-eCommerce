package com.example.full.auth.and.jwt.controller.admin;


import com.example.full.auth.and.jwt.dto.OrderResponse;
import com.example.full.auth.and.jwt.dto.OrderStatusRequest;
import com.example.full.auth.and.jwt.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/orders")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")  // class-level: all endpoints admin-only
public class AdminOrderController {

    private final OrderService orderService;


    @GetMapping("/All")
    public ResponseEntity<Page<OrderResponse>> getAllOrders(
            @PageableDefault(page = 0, size = 5, sort = "id", direction = Sort.Direction.ASC)
            Pageable pageable
    ) {
        return ResponseEntity.ok(orderService.getAllOrders(pageable));
    }

    @PatchMapping("/items/{itemId}/status")
    public ResponseEntity<?> updateOrderItemStatus(
            @PathVariable Long itemId,
            @Valid @RequestBody OrderStatusRequest request) {
        try {
            OrderResponse response = orderService.updateOrderItemStatus(itemId, request);
            return ResponseEntity.ok(response);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<Page<OrderResponse>> getUserOrders(
            @PathVariable Long id,
            @PageableDefault(page = 0, size = 5, sort = "id", direction = Sort.Direction.ASC)
            Pageable pageable
    ) {
        return ResponseEntity.ok(orderService.getUserOrders(id, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getMyOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getMyOrderById(id));
    }

    @PutMapping("/status/{id}")
    public ResponseEntity<?> updateOrderStatus(
            @PathVariable Long id,
            @Valid @RequestBody OrderStatusRequest request) {
        try {
            OrderResponse orderProduct = orderService.updateOrderStatus(id, request);
            return ResponseEntity.ok(orderProduct);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }




















}
