package com.example.full.auth.and.jwt.controller;

import com.example.full.auth.and.jwt.dto.OrderResponse;
import com.example.full.auth.and.jwt.dto.OrderStatusRequest;
import com.example.full.auth.and.jwt.dto.ProductResponse;
import com.example.full.auth.and.jwt.dto.ProductStatusRequest;
import com.example.full.auth.and.jwt.model.Order;
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

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // POST /api/orders?shippingAddress=...
    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
    //        @RequestParam String shippingAddress
    ) {
        OrderResponse order = orderService.createOrder(/*shippingAddress*/);
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }


    @GetMapping("/All")
    //@PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Page<OrderResponse>> getAllOrders(
            @PageableDefault(page = 0, size = 5, sort = "id", direction = Sort.Direction.ASC)
            Pageable pageable
    ) {
        return ResponseEntity.ok(orderService.getAllOrders(pageable));
    }


    @PatchMapping("/items/{itemId}/status")
    //@PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_SELLER')")
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



    // GET /api/orders/my
    @GetMapping("/my")
    public ResponseEntity<Page<OrderResponse>> getMyOrders(
            @PageableDefault(page = 0, size = 5, sort = "id", direction = Sort.Direction.ASC)
            Pageable pageable
    ) {
        return ResponseEntity.ok(orderService.getMyOrders(pageable));
    }

//    @GetMapping("/user/{id}")
//    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
//    public ResponseEntity<Page<OrderResponse>> getUserOrders(
//            @PathVariable Long id,
//            @PageableDefault(page = 0, size = 5, sort = "id", direction = Sort.Direction.ASC)
//            Pageable pageable
//    ) {
//        return ResponseEntity.ok(orderService.getUserOrders(id, pageable));
//    }

    // GET /api/orders/{id}
//    @GetMapping("/{id}")
//    public ResponseEntity<OrderResponse> getMyOrderById(@PathVariable Long id) {
//        return ResponseEntity.ok(orderService.getMyOrderById(id));
//    }

    // PATCH /api/orders/{id}/cancel
//    @PatchMapping("/{id}/cancel")
//    public ResponseEntity<OrderResponse> cancelOrder(@PathVariable Long id) {
//        return ResponseEntity.ok(orderService.cancelMyOrder(id));
//    }

    @PutMapping("/status/{id}")
    //@PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SELLER')")
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