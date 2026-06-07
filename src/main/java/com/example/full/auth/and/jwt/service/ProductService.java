package com.example.full.auth.and.jwt.service;

import com.example.full.auth.and.jwt.dto.*;
import com.example.full.auth.and.jwt.model.*;
import com.example.full.auth.and.jwt.specification.ProductSpecification;
import com.example.full.auth.and.jwt.repository.CategoryRepository;
import com.example.full.auth.and.jwt.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final UserService userService;  // injected
    private final CategoryRepository categoryRepository;
    private final CloudinaryService cloudinaryService;


    /**
     * Get all products - available to all authenticated users (both sellers and admins can read all products)
     */
//    public List<ProductResponse> getAllProducts() {
//        return productRepository.findAll().stream()
//                .map(this::convertToProductResponse)
//                .collect(Collectors.toList());
//    }


    public Page<ProductResponse> getAllProducts(Pageable pageable) {
        return productRepository.findByDeletedFalseAndStatus(ProductStatus.ACTIVE, pageable)
                .map(this::convertToProductResponse);
    }

    public Page<ProductResponse> getAllProductsAdmin(ProductStatus status, Pageable pageable) {

        User currentUser = getCurrentUser();

        if (!isAdminUser(currentUser)) {
            throw new RuntimeException("Access denied");
        }

        Page<Product> products = productRepository.findByStatus(status, pageable);

        return products.map(this::convertToProductResponse);
    }


    /**
     * Get products of current logged-in user (seller)
     */
    public Page<ProductResponse> getMyProducts(Pageable pageable) {
        // 1) نجيب المستخدم الحالي من الـSecurityContext
        User currentUser = getCurrentUser();

        // 2) نجيب المنتجات اللي seller بتاعها = currentUser
        return productRepository.findByDeletedFalseAndSeller( currentUser, pageable)//.stream()
                // 3) نحول كل Product إلى ProductResponse (عندك الميثود convertToProductResponse)
                .map(this::convertToProductResponse);
                //.collect(Collectors.toList());
    }

    public Page<ProductResponse> getMyProductsAdmin(Pageable pageable) {
        // 1) نجيب المستخدم الحالي من الـSecurityContext
        User currentUser = getCurrentUser();

        if (!isAdminUser(currentUser)) {
            throw new RuntimeException("Access denied");
        }

        // 2) نجيب المنتجات اللي seller بتاعها = currentUser
        return productRepository.findBySeller( currentUser, pageable)//.stream()
                // 3) نحول كل Product إلى ProductResponse (عندك الميثود convertToProductResponse)
                .map(this::convertToProductResponse);
        //.collect(Collectors.toList());
    }


    public Page<ProductResponse> getSellerProducts( Long id, Pageable pageable) {
        // 1) نجيب المستخدم الحالي من الـSecurityContext
        //UserResponse userResponse = userService.getUserEntityById(id);

        User user = userService.getUserEntityById(id);

        // 2) نجيب المنتجات اللي seller بتاعها = currentUser
        return productRepository.findByDeletedFalseAndStatusAndSeller(ProductStatus.ACTIVE, user, pageable)//.stream()
                // 3) نحول كل Product إلى ProductResponse (عندك الميثود convertToProductResponse)
                .map(this::convertToProductResponse);
                //.collect(Collectors.toList());
    }


    public Page<ProductResponse> getSellerProductsADmin(ProductStatus status, Long id, Pageable pageable) {
        // 1) نجيب المستخدم الحالي من الـSecurityContext
        //UserResponse userResponse = userService.getUserEntityById(id);

        User user = userService.getUserEntityById(id);

        // 2) نجيب المنتجات اللي seller بتاعها = currentUser
        return productRepository.findByStatusAndSeller(status, user, pageable)//.stream()
                // 3) نحول كل Product إلى ProductResponse (عندك الميثود convertToProductResponse)
                .map(this::convertToProductResponse);
        //.collect(Collectors.toList());
    }


    public Page<ProductResponse> getCategoryProducts(Long id, Pageable pageable) {

        Category category = categoryRepository.findById(id).get();

        return productRepository.findByDeletedFalseAndStatusAndCategory(ProductStatus.ACTIVE, category, pageable)//.stream()
                .map(this::convertToProductResponse);
                //.collect(Collectors.toList());
    }


    public Page<ProductResponse> getCategoryProductsAdmin(ProductStatus status,Long id, Pageable pageable) {

        Category category = categoryRepository.findById(id).get();

        return productRepository.findByStatusAndCategory(status, category, pageable)//.stream()
                .map(this::convertToProductResponse);
        //.collect(Collectors.toList());
    }


    /**
     * Get product by ID - available to all authenticated users (they can see all products)
     */
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findByIdAndDeletedFalse(id);
        return convertToProductResponse(product);
    }

    public ProductResponse getProductByIdAdmin(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
        return convertToProductResponse(product);
    }

    /**
     * Create a new product - admin and seller can create their own products
     */
    public ProductResponse createProduct(ProductRequest request, MultipartFile image) {

        User currentUser = getCurrentUser();
        //boolean isSeller = isSellerUser(currentUser);

        // Only admin can update all products, sellers can only update their own products
//        if (!isSeller) {
//            throw new AccessDeniedException("Access denied. You can only seller creat products.");
//        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        Map result = null;
        try {
            result = cloudinaryService.uploadImage(image);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        ImageResponse response = ImageResponse.builder()
                .url(result.get("secure_url").toString())
                .publicId(result.get("public_id").toString())
                .build();
        
        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .stock(request.getStock())
                .imageUrl(response.getPublicId())
                .seller(currentUser)
                .category(category)
                .status(ProductStatus.PENDING)
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

    public ProductResponse updateProductStatus(Long id, ProductStatusRequest request) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));

        User currentUser = getCurrentUser();
        boolean isAdmin = isAdminUser(currentUser);

        // Only admin can update all products, sellers can only update their own products
        if (!isAdmin && !product.getSeller().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("Access denied. You can only update your own products.");
        }

        //product.setStatus(request.getStatus());
        ProductStatus newStatus = request.getStatus();
        // قاعدة بسيطة: البائع = ACTIVE أو INACTIVE فقط؛ الأدمن = أي حالة.
        if (!isAdmin) {
            if (newStatus != ProductStatus.ACTIVE && newStatus != ProductStatus.INACTIVE) {
                throw new AccessDeniedException("Sellers may only set status to ACTIVE or INACTIVE.");
            }
        }

        product.setStatus(newStatus);

        Product updatedProduct = productRepository.save(product);
        return convertToProductResponse(updatedProduct);

    }



        /**
         * Delete a product - admin can delete all products, sellers can delete only their own products
         * Soft Delete
         */
//    public void deleteProduct(Long id) {
//        if (!productRepository.existsById(id)) {
//            throw new RuntimeException("Product not found with id: " + id);
//        }
//
//        Product product = productRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
//
//        User currentUser = getCurrentUser();
//        boolean isAdmin = isAdminUser(currentUser);
//
//        // Only admin can delete all products, sellers can only delete their own products
//        if (!isAdmin && !product.getSeller().getId().equals(currentUser.getId())) {
//            throw new AccessDeniedException("Access denied. You can only delete your own products.");
//        }
//
//        try {
//            cloudinaryService.deleteImage(product.getImageUrl());
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//
//        productRepository.deleteById(id);
//    }


        public ProductResponse deleteProduct(Long id) {

            Product product = productRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));

            User currentUser = getCurrentUser();
            boolean isAdmin = isAdminUser(currentUser);

            // Only admin can update all products, sellers can only update their own products
            if (!isAdmin && !product.getSeller().getId().equals(currentUser.getId())) {
                throw new AccessDeniedException("Access denied. You can only delete your own products.");
            }

            product.setDeleted(true);

            Product updatedProduct = productRepository.save(product);
            return convertToProductResponse(updatedProduct);

        }


//    public List<ProductResponse> searchProducts(String query) {
//        // لو الـ query فاضية أو null رجّع كل المنتجات (اختياري)
//
//        if (query == null || query.trim().isEmpty()) {
//            return getAllProducts();
//        }
//
//        List<Product> products = productRepository
//                .findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(query, query);
//
//        return products.stream()
//                .map(this::convertToProductResponse)
//                .collect(Collectors.toList());
//    }



    public Page<ProductResponse> searchProducts(ProductFilterRequest filter, Pageable pageable) {
        Specification<Product> spec = ProductSpecification.filter(
                filter.name(),
                filter.category(),
                filter.minPrice(),
                filter.maxPrice(),
                filter.inStock()
        );
        return productRepository.findAll(spec, pageable)
                .map(this::convertToProductResponse);
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
        return user != null && Role.ROLE_ADMIN.equals(user.getRole());
    }


    private boolean isSellerUser(User user) {
        return user != null && Role.ROLE_SELLER.equals(user.getRole());
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
                .categoryId(product.getCategory().getId())
                .categoryName(product.getCategory().getName())
                .status(product.getStatus())
                .build();
    }
}