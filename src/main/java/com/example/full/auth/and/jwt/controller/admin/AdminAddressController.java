package com.example.full.auth.and.jwt.controller.admin;


import com.example.full.auth.and.jwt.dto.AddressRequest;
import com.example.full.auth.and.jwt.dto.AddressResponse;
import com.example.full.auth.and.jwt.service.AddressService;
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

import java.util.Map;

@RestController
@RequestMapping("/api/admin/address")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")  // class-level: all endpoints admin-only
public class AdminAddressController {

    private final AddressService addressService;


    @GetMapping("/user/{id}")
    public ResponseEntity<Page<AddressResponse>> getUserAddress(
            @PathVariable("id") Long id,
            @PageableDefault(page = 0, size = 5, sort = "id", direction = Sort.Direction.ASC)
            Pageable pageable
    ) {
        Page<AddressResponse> address = addressService.getUserAddresses(id, pageable);
        // لو مالقاش منتجات، رجّع 404 أو قائمة فاضية حسب ما تحب
        if (address == null || address.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // أو .body(Collections.emptyList())
        }
        return ResponseEntity.ok(address);
    }


    @PutMapping("/{id}")
    public ResponseEntity<?> updateAddress(
            @PathVariable Long id,
            @Valid @RequestBody AddressRequest request) {
        try {
            AddressResponse updatedAddress = addressService.updateAddress(id, request);
            return ResponseEntity.ok(updatedAddress);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAddress(@PathVariable Long id) {
        try {
            addressService.deleteAddress(id);
            return ResponseEntity.ok(Map.of("message", "Product deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
        }
    }



















}
