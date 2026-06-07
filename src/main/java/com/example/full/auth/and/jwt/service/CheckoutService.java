//package com.example.full.auth.and.jwt.service;
//
//import com.example.full.auth.and.jwt.dto.*;
//import com.example.full.auth.and.jwt.model.Cart;
//import com.example.full.auth.and.jwt.model.Checkout;
//import com.example.full.auth.and.jwt.model.Product;
//import com.example.full.auth.and.jwt.model.User;
//import com.example.full.auth.and.jwt.repository.AddressRepository;
//import com.example.full.auth.and.jwt.repository.CartRepository;
//import com.example.full.auth.and.jwt.repository.CheckoutRepository;
//import com.example.full.auth.and.jwt.repository.ProductRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.access.AccessDeniedException;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Service;
//
//import java.math.BigDecimal;
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Service
//@RequiredArgsConstructor
//public class CheckoutService {
//
//    private final CheckoutRepository checkoutRepository;
//    private final CartRepository cartRepository;
//    private final ProductRepository productRepository;
//    private final AddressService addressService;
//
//    // ✅ تنفيذ الـ checkout لسلة المستخدم الحالي
//    public CheckoutResponse checkout(CheckoutRequest request) {
//        User currentUser = getCurrentUser();
//
//        // 1) جلب عناصر السلة للمستخدم
//        List<Cart> carts = cartRepository.findByUser(currentUser);
//        if (carts.isEmpty()) {
//            throw new RuntimeException("Your cart is empty");
//        }
//
//        // 2) التأكد من توفر الـ stock + حساب الإجمالي
//        BigDecimal total = BigDecimal.ZERO;
//        for (Cart cart : carts) {
//            Product product = cart.getProduct();
//            if (product.getStock() < cart.getQuantity()) {
//                throw new RuntimeException(
//                        "Not enough stock for product: " + product.getName()
//                );
//            }
//            BigDecimal lineTotal = product.getPrice()
//                    .multiply(BigDecimal.valueOf(cart.getQuantity()));
//            total = total.add(lineTotal);
//        }
//
//        // 3) خصم الكمية من مخزون المنتجات
//        carts.forEach(cart -> {
//            Product product = cart.getProduct();
//            product.setStock(product.getStock() - cart.getQuantity());
//        });
//        productRepository.saveAll(
//                carts.stream().map(Cart::getProduct).distinct().toList()
//        );
//
//        AddressResponse address = addressService.getMyDefaultAddress();
//
//        // 4) إنشاء سجل الـ checkout وربطه بعناصر السلة (carts) الحالية
//        Checkout checkout = Checkout.builder()
//                .user(currentUser)
//                .totalAmount(total)
//                .paymentMethod(request.getPaymentMethod())
//                .paymentStatus("PAID") // أو "PENDING" لو عندك دفع حقيقي
//                .status("PENDING")
//                .shippingAddress(address.getId().toString())
//                .carts(carts)
//                .build();
//
//        Checkout saved = checkoutRepository.save(checkout);
//
//        // 5) تجهيز العناصر للـ response
//        List<CartResponse> itemDtos = carts.stream()
//                .map(this::convertToCartResponse)
//                .collect(Collectors.toList());
//
//        // 6) إرجاع البيانات للـ client
//        return CheckoutResponse.builder()
//                .id(saved.getId())
//                .totalAmount(saved.getTotalAmount())
//                .paymentMethod(saved.getPaymentMethod())
//                .paymentStatus(saved.getPaymentStatus())
//                .shippingAddress(saved.getShippingAddress())
//                .createdAt(saved.getCreatedAt())
//                .items(itemDtos)
//                .build();
//    }
//
//    // ✅ كل الـ checkout للمستخدم الحالي مع العناصر المرتبطة
//    public List<CheckoutResponse> getMyCheckouts() {
//        User currentUser = getCurrentUser();
//        return checkoutRepository.findByUser(currentUser).stream()
//                .map(this::convertToCheckoutResponseWithItems)
//                .collect(Collectors.toList());
//    }
//
//    // ✅ واحد Checkout بالتفصيل (بدون عناصر السلة القديمة)
//    public CheckoutResponse getCheckoutById(Long id) {
//        User currentUser = getCurrentUser();
//        Checkout checkout = checkoutRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("Checkout not found"));
//
//        if (!checkout.getUser().getId().equals(currentUser.getId())) {
//            throw new AccessDeniedException("You don't have permission to view this checkout");
//        }
//
//        return convertToCheckoutResponseWithItems(checkout);
//    }
//
//
//    //
//    public CheckoutResponse updatePaymentStatus(Long checkoutId, String newStatus) {
//        User currentUser = getCurrentUser();
//
//        Checkout checkout = checkoutRepository.findById(checkoutId)
//                .orElseThrow(() -> new RuntimeException("Checkout not found"));
//
//        // تأكد أن الطلب يخص نفس المستخدم
//        if (!checkout.getUser().getId().equals(currentUser.getId())) {
//            throw new AccessDeniedException("You don't have permission to update this checkout");
//        }
//
//        // هنا تقدر تضيف validation على القيم المسموحة للحالة
//        checkout.setPaymentStatus(newStatus);
//        checkout.setUpdatedAt(java.time.LocalDateTime.now());
//
//        Checkout saved = checkoutRepository.save(checkout);
//
//        // نعيد نفس الفورمات اللي عندك (مع العناصر)
//        return convertToCheckoutResponseWithItems(saved);
//    }
//
//
//
//    public void deleteCheckout(Long id) {
//        if (!checkoutRepository.existsById(id)) {
//            throw new RuntimeException(
//                    " غير موجود - checkout not found with id: " + id
//            );
//        }
//
//        // Check if current user is admin
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        //User currentUser = (User) authentication.getPrincipal();
//
////        if (!isAdmin(currentUser)) {
////            throw new RuntimeException("Access denied. Only administrators can delete users.");
////        }
//
//        checkoutRepository.deleteById(id);
//    }
//
//
//
//    // ===== Helpers =====
//
//    private CartResponse convertToCartResponse(Cart cart) {
//        return CartResponse.builder()
//                .id(cart.getId())
//                .quantity(cart.getQuantity())
//                .product(convertToProductResponse(cart.getProduct()))
//                .build();
//    }
//
//    private ProductResponse convertToProductResponse(Product product) {
//        return ProductResponse.builder()
//                .id(product.getId())
//                .name(product.getName())
//                .description(product.getDescription())
//                .price(product.getPrice())
//                .stock(product.getStock())
//                .imageUrl(product.getImageUrl())
//                .sellerId(product.getSeller().getId())
//                .sellerName(product.getSeller().getFullName())
//                .build();
//    }
//
//    private CheckoutResponse convertToCheckoutResponseWithItems(Checkout checkout) {
//        List<CartResponse> items = checkout.getCarts() == null ? List.of()
//                : checkout.getCarts().stream()
//                .map(this::convertToCartResponse)
//                .collect(Collectors.toList());
//
//        return CheckoutResponse.builder()
//                .id(checkout.getId())
//                .totalAmount(checkout.getTotalAmount())
//                .paymentMethod(checkout.getPaymentMethod())
//                .paymentStatus(checkout.getPaymentStatus())
//                .shippingAddress(checkout.getShippingAddress())
//                .createdAt(checkout.getCreatedAt())
//                .items(items)
//                .build();
//    }
//
//    private User getCurrentUser() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (authentication == null || !authentication.isAuthenticated()
//                || authentication.getPrincipal() instanceof String) {
//            throw new AccessDeniedException("Authentication required");
//        }
//        return (User) authentication.getPrincipal();
//    }
//}