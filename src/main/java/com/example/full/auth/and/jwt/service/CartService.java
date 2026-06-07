package com.example.full.auth.and.jwt.service;

import com.example.full.auth.and.jwt.dto.CartCreateRequest;
import com.example.full.auth.and.jwt.dto.CartUpdateRequest;
import com.example.full.auth.and.jwt.dto.CartResponse;
import com.example.full.auth.and.jwt.dto.ProductResponse;
import com.example.full.auth.and.jwt.model.Cart;
import com.example.full.auth.and.jwt.model.Product;
import com.example.full.auth.and.jwt.model.User;
import com.example.full.auth.and.jwt.repository.CartRepository;
import com.example.full.auth.and.jwt.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final UserService userService;  // injected


    // ✅ الميثود الأساسية: إرجاع سلة المستخدم الحالي
    public Page<CartResponse> getMyCartProducts(Pageable pageable) {
        // 1) نجيب المستخدم الحالي من الـSecurityContext
        User currentUser = getCurrentUser();

        // 2) نجيب كل عناصر السلة لهذا المستخدم
        return cartRepository.findByUser(currentUser, pageable)//.stream()
                // 3) نحول كل Cart إلى CartResponse
                .map(this::convertToCartResponse);
                //.collect(Collectors.toList());
    }


    public Page<CartResponse> getUserCartProducts(Long id, Pageable pageable) {
        // 1) نجيب المستخدم الحالي من الـSecurityContext
        //User currentUser = getCurrentUser();
        User user = userService.getUserEntityById(id);


        // 2) نجيب كل عناصر السلة لهذا المستخدم
        return cartRepository.findByUser(user, pageable)//.stream()
                // 3) نحول كل Cart إلى CartResponse
                .map(this::convertToCartResponse);
        //.collect(Collectors.toList());
    }



//    public CartResponse createCart(CartCreateRequest request) {
//        User currentUser = getCurrentUser();
//
//        Cart cart = Cart.builder()
//                .quantity(1)
//                .user(currentUser)
//                .product(productRepository.findById(request.getProductId().longValue())
//                        .orElseThrow(() -> new RuntimeException("Product not found")))
//                .build();
//
//        Cart savedCart = cartRepository.save(cart);
//        return convertToCartResponse(savedCart);
//    }


    public CartResponse addToCart(CartCreateRequest request) {
        User currentUser = getCurrentUser();
        Product product = productRepository.getById(request.getProductId());

        Optional<Cart> existingCartOpt =
                cartRepository.findByUserAndProduct(currentUser, product);

        Cart cart;
        if (existingCartOpt.isPresent()) {
            cart = existingCartOpt.get();
            int newQuantity = cart.getQuantity() + 1;
            cart.setQuantity(newQuantity);
        } else {
            cart = new Cart();
            cart.setUser(currentUser);
            cart.setProduct(product);
            cart.setQuantity(1);
        }

        Cart savedCart =  cartRepository.save(cart); // هنا يعمل update أو insert حسب الحالة
        return convertToCartResponse(savedCart);
    }


    // ✅ تحديث كمية منتج في السلة
    public CartResponse updateCart(Long cartId, CartUpdateRequest request) {
        User currentUser = getCurrentUser();

        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        // تحقق من أن السلة تخص المستخدم الحالي
        if (!cart.getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You don't have permission to update this cart item");
        }

        // تحديث الكمية
        cart.setQuantity(request.getQuantity());

        // إذا كان هناك productId جديد، نحدث المنتج أيضاً
        if (request.getProductId() != null) {
            Product product = productRepository.findById(request.getProductId().longValue())
                    .orElseThrow(() -> new RuntimeException("Product not found"));
            cart.setProduct(product);
        }

        Cart updatedCart = cartRepository.save(cart);
        return convertToCartResponse(updatedCart);
    }

    public CartResponse updateProductCartIncrease(Long cartId) {
        User currentUser = getCurrentUser();

        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        // تحقق من أن السلة تخص المستخدم الحالي
        if (!cart.getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You don't have permission to update this cart item");
        }

        // إذا كان هناك productId جديد، نحدث المنتج أيضاً
        if (cart.getProduct().getId() != null) {
            Product product = productRepository.findById(cart.getProduct().getId().longValue())
                    .orElseThrow(() -> new RuntimeException("Product not found"));
            cart.setProduct(product);
        }

        // تحديث الكمية
        cart.setQuantity( (cart.getQuantity()+1) );

        Cart updatedCart = cartRepository.save(cart);
        return convertToCartResponse(updatedCart);
    }

    public CartResponse updateProductCartDecrease(Long cartId) {
        User currentUser = getCurrentUser();

        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        // تحقق من أن السلة تخص المستخدم الحالي
        if (!cart.getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You don't have permission to update this cart item");
        }

        // إذا كان هناك productId جديد، نحدث المنتج أيضاً
        if (cart.getProduct().getId() != null) {
            Product product = productRepository.findById(cart.getProduct().getId().longValue())
                    .orElseThrow(() -> new RuntimeException("Product not found"));
            cart.setProduct(product);
        }

        // تحديث الكمية
        cart.setQuantity( (cart.getQuantity()-1) );

        Cart updatedCart = cartRepository.save(cart);
        return convertToCartResponse(updatedCart);
    }

    // ✅ حذف عنصر من السلة
    public void deleteCart(Long cartId) {
        User currentUser = getCurrentUser();

        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        // تحقق من أن السلة تخص المستخدم الحالي
        if (!cart.getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You don't have permission to delete this cart item");
        }

        cartRepository.delete(cart);
    }

    // ✅ مسح كل عناصر السلة للمستخدم الحالي
    public void clearCart() {
        User currentUser = getCurrentUser();
        List<Cart> userCarts = cartRepository.findByUser(currentUser);
        cartRepository.deleteAll(userCarts);
    }

    // ✅ تحويل كيان Cart إلى DTO CartResponse
    private CartResponse convertToCartResponse(Cart cart) {
        return CartResponse.builder()
                .id(cart.getId())
                .quantity(cart.getQuantity())
                .product(convertToProductResponse(cart.getProduct()))
                .build();
    }

    // ✅ تحويل كيان Product إلى DTO ProductResponse
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
                .categoryName(product.getCategory().getName())
                .categoryId(product.getCategory().getId())
                .build();
    }

    // ✅ نفس فكرة getCurrentUser في ProductService
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || authentication.getPrincipal() instanceof String) {
            throw new AccessDeniedException("Authentication required");
        }
        return (User) authentication.getPrincipal();
    }
}
