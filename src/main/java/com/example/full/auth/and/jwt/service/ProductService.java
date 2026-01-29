package com.example.full.auth.and.jwt.service;

import com.example.full.auth.and.jwt.dto.ProductRequest;
import com.example.full.auth.and.jwt.dto.ProductResponse;
import com.example.full.auth.and.jwt.model.Product;
import com.example.full.auth.and.jwt.model.User;
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
     * Get all products - available to all authenticated users (both sellers and admins can read all products)
     */
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll().stream()
                .map(this::convertToProductResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get products of current logged-in user (seller)
     */
    public List<ProductResponse> getMyProducts() {
        // 1) نجيب المستخدم الحالي من الـSecurityContext
        User currentUser = getCurrentUser();

        // 2) نجيب المنتجات اللي seller بتاعها = currentUser
        return productRepository.findBySeller(currentUser).stream()
                // 3) نحول كل Product إلى ProductResponse (عندك الميثود convertToProductResponse)
                .map(this::convertToProductResponse)
                .collect(Collectors.toList());
    }


    /**
     * Get product by ID - available to all authenticated users (they can see all products)
     */
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
        
        return convertToProductResponse(product);
    }

    /**
     * Create a new product - admin and seller can create their own products
     */
    public ProductResponse createProduct(ProductRequest request) {
        User currentUser = getCurrentUser();
        
        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .stock(request.getStock())
                .imageUrl(request.getImageUrl())
                .seller(currentUser) // Set the current user as the seller
                .build();
        
        Product savedProduct = productRepository.save(product);
        return convertToProductResponse(savedProduct);
    }

    /**
     * Update a product - admin can update all products, sellers can update only their own products
     */
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
        
        User currentUser = getCurrentUser();
        boolean isAdmin = isAdminUser(currentUser);
        
        // Only admin can update all products, sellers can only update their own products
        if (!isAdmin && !product.getSeller().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("Access denied. You can only update your own products.");
        }
        
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setImageUrl(request.getImageUrl());
        
        Product updatedProduct = productRepository.save(product);
        return convertToProductResponse(updatedProduct);
    }

    /**
     * Delete a product - admin can delete all products, sellers can delete only their own products
     */
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Product not found with id: " + id);
        }
        
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
        
        User currentUser = getCurrentUser();
        boolean isAdmin = isAdminUser(currentUser);
        
        // Only admin can delete all products, sellers can only delete their own products
        if (!isAdmin && !product.getSeller().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("Access denied. You can only delete your own products.");
        }
        
        productRepository.deleteById(id);
    }

    /**
     * Get current authenticated user
     */
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal() instanceof String) {
            throw new AccessDeniedException("Authentication required");
        }
        
        return (User) authentication.getPrincipal();
    }
    
    /**
     * Check if current user has admin role
     */
    private boolean isAdminUser(User user) {
        return user != null && "ADMIN".equals(user.getRole());
    }
    
    /**
     * Check if current user has admin role
     */
    private void checkAdminPermission() {
        User currentUser = getCurrentUser();
        if (!isAdminUser(currentUser)) {
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
                .sellerId(product.getSeller().getId())
                .sellerName(product.getSeller().getFullName())
                .build();
    }
}