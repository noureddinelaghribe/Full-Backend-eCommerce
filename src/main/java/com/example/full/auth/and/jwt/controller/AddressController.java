package com.example.full.auth.and.jwt.controller;


import com.example.full.auth.and.jwt.dto.*;
import com.example.full.auth.and.jwt.service.AddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/address")
@RequiredArgsConstructor
public class AddressController {


    private final AddressService addressService;


    @GetMapping("/my")
    public ResponseEntity<Page<AddressResponse>> getMyAddress(
            @PageableDefault(page = 0, size = 5, sort = "id", direction = Sort.Direction.ASC)
            Pageable pageable
    ) {
        Page<AddressResponse> address = addressService.getMyAddresses(pageable);
        return ResponseEntity.ok(address);
    }


    @GetMapping("/myDefaultAddress")
    public ResponseEntity<AddressResponse> getMyDefaultAddress() {
        return ResponseEntity.ok(addressService.getMyDefaultAddress());
    }


    @GetMapping("/idMyDefaultAddress")
    public ResponseEntity<Long> getIdMyDefaultAddress() {
        return ResponseEntity.ok(addressService.getIdMyDefaultAddress());
    }


    @GetMapping("/{id}")
    public ResponseEntity<?> getAddressById(@PathVariable Long id) {
        try {
            AddressResponse address = addressService.getAddressesById(id);
            return ResponseEntity.ok(address);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
        }
    }


    @PostMapping
    public ResponseEntity<?> createAddress(@Valid @RequestBody AddressRequest request) {
        try {
            AddressResponse address = addressService.createAddresses(request);
            return ResponseEntity.ok(address);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
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


    @PatchMapping("/{id}")
    public ResponseEntity<?> updateAddressDefault(
            @PathVariable Long id,
            @Valid @RequestBody AddressDefaultRequest request) {
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
