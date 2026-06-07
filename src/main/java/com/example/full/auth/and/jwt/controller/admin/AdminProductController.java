package com.example.full.auth.and.jwt.controller.admin;


import com.example.full.auth.and.jwt.dto.ProductRequest;
import com.example.full.auth.and.jwt.dto.ProductResponse;
import com.example.full.auth.and.jwt.dto.ProductStatusRequest;
import com.example.full.auth.and.jwt.model.ProductStatus;
import com.example.full.auth.and.jwt.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/products")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")  // class-level: all endpoints admin-only
public class AdminProductController {

    private final ProductService productService;


    @GetMapping
    public ResponseEntity<Page<ProductResponse>> getAllProducts(
            @RequestParam ProductStatus status,
            @PageableDefault(page = 0, size = 15, sort = "id", direction = Sort.Direction.ASC)
            Pageable pageable
    ) {
        Page<ProductResponse> products = productService.getAllProductsAdmin( status, pageable);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/my")
    public ResponseEntity<Page<ProductResponse>> getMyProducts(
            @PageableDefault(page = 0, size = 5, sort = "id", direction = Sort.Direction.ASC)
            Pageable pageable
    ) {
        Page<ProductResponse> products = productService.getMyProductsAdmin(pageable);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/seller/{id}")
    public ResponseEntity<Page<ProductResponse>> getSellerProducts(
            @RequestParam ProductStatus status,
            @PathVariable("id") Long id,
            @PageableDefault(page = 0, size = 5, sort = "id", direction = Sort.Direction.ASC)
            Pageable pageable
    ) {
        Page<ProductResponse> products = productService.getSellerProductsADmin(status, id, pageable);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/category/{id}")
    public ResponseEntity<Page<ProductResponse>> getCategoryProducts(
            @RequestParam ProductStatus status,
            @PathVariable("id") Long id,
            @PageableDefault(page = 0, size = 5, sort = "id", direction = Sort.Direction.ASC)
            Pageable pageable
    ) {
        Page<ProductResponse> products = productService.getCategoryProductsAdmin(status, id, pageable);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable Long id) {
        try {
            ProductResponse product = productService.getProductByIdAdmin(id);
            return ResponseEntity.ok(product);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createProduct(
            @RequestParam("product") String productJson,
            @RequestPart("image") MultipartFile image
    ) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            ProductRequest request = objectMapper.readValue(productJson, ProductRequest.class);

            ProductResponse product = productService.createProduct(request, image);
            return ResponseEntity.ok(product);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest request) {
        try {
            ProductResponse updatedProduct = productService.updateProduct(id, request);
            return ResponseEntity.ok(updatedProduct);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PutMapping("/status/{id}")
    public ResponseEntity<?> updateProductStatus(
            @PathVariable Long id,
            @Valid @RequestBody ProductStatusRequest request) {
        try {
            ProductResponse updatedProduct = productService.updateProductStatus(id, request);
            return ResponseEntity.ok(updatedProduct);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }


    @PutMapping("/delete/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        try {
            productService.deleteProduct(id);
            return ResponseEntity.ok(Map.of("message", "Product deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
        }
    }




















}
