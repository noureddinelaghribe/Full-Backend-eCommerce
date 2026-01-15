package com.example.full.auth.and.jwt.service;

import com.example.full.auth.and.jwt.dto.ProductRequest;
import com.example.full.auth.and.jwt.dto.ProductResponse;
import com.example.full.auth.and.jwt.model.Product;
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
public class ProductService {

    private final ProductRepository productRepository;

    /**
     * Get all products - available to all authenticated users
     */
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll().stream()
                .map(this::convertToProductResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get product by ID - available to all authenticated users
     */
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
        
        return convertToProductResponse(product);
    }

    /**
     * Create a new product - admin only
     */
    public ProductResponse createProduct(ProductRequest request) {
        checkAdminPermission();
        
        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .stock(request.getStock())
                .imageUrl(request.getImageUrl())
                .build();
        
        Product savedProduct = productRepository.save(product);
        return convertToProductResponse(savedProduct);
    }

    /**
     * Update a product - admin only
     */
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        checkAdminPermission();
        
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
        
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setImageUrl(request.getImageUrl());
        
        Product updatedProduct = productRepository.save(product);
        return convertToProductResponse(updatedProduct);
    }

    /**
     * Delete a product - admin only
     */
    public void deleteProduct(Long id) {
        checkAdminPermission();
        
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Product not found with id: " + id);
        }
        
        productRepository.deleteById(id);
    }

    /**
     * Check if current user has admin role
     */
    private void checkAdminPermission() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("Authentication required");
        }
        
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));
        
        if (!isAdmin) {
            throw new AccessDeniedException("Access denied. Only administrators can perform this operation.");
        }
    }

    /**
     * Convert Product entity to ProductResponse DTO
     */
    private ProductResponse convertToProductResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stock(product.getStock())
                .imageUrl(product.getImageUrl())
                .build();
    }
}