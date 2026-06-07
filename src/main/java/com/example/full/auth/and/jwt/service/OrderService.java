package com.example.full.auth.and.jwt.service;

import com.example.full.auth.and.jwt.dto.*;
import com.example.full.auth.and.jwt.model.*;
import com.example.full.auth.and.jwt.repository.CartRepository;
import com.example.full.auth.and.jwt.repository.OrderItemRepository;
import com.example.full.auth.and.jwt.repository.OrderRepository;
import com.example.full.auth.and.jwt.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final AddressService addressService;
    private final UserService userService;  // injected


    // إنشاء طلب جديد من السلة
    @Transactional
    public OrderResponse createOrder(/*String shippingAddress*/) {
        User currentUser = getCurrentUser();

        // 1) جلب عناصر السلة
        List<Cart> carts = cartRepository.findByUser(currentUser);
        if (carts.isEmpty()) {
            throw new RuntimeException("Your cart is empty");
        }

        // 2) حساب الإجمالي والتحقق من المخزون
        BigDecimal total = BigDecimal.ZERO;
        for (Cart cart : carts) {
            Product product = cart.getProduct();
            if (product.getStock() < cart.getQuantity()) {
                throw new RuntimeException("Not enough stock for product: " + product.getName());
            }
            BigDecimal lineTotal = product.getPrice()
                    .multiply(BigDecimal.valueOf(cart.getQuantity()));
            total = total.add(lineTotal);
        }

        // 3) خصم من المخزون
//        for (Cart cart : carts) {
//            Product product = cart.getProduct();
//            product.setStock(product.getStock() - cart.getQuantity());
//            productRepository.save(product);
//        }


        // 3) خصم من المخزون
        for (Cart cart : carts) {
            Product product = cart.getProduct();

            // Check stock is enough (redundant safety check)
            if (product.getStock() < cart.getQuantity()) {
                throw new RuntimeException("Not enough stock for product: " + product.getName());
            }

            int newStock = product.getStock() - cart.getQuantity();
            product.setStock(newStock);

            // If stock hits 0, mark as OUT_OF_STOCK
            if (newStock == 0) {
                product.setStatus(ProductStatus.OUT_OF_STOCK);
            }

            productRepository.save(product);
        }


        AddressResponse address = addressService.getMyDefaultAddress();

        // 4) إنشاء Order
        Order order = new Order();
        order.setUser(currentUser);
        order.setTotalAmount(total);
        order.setStatus(OrderStatus.PENDING);
        order.setShippingAddress(address.getFullAddress());
        order.setCreatedAt(LocalDateTime.now());
        Order savedOrder = orderRepository.save(order);

        // 5) إنشاء OrderItem لكل عنصر في السلة
        for (Cart cart : carts) {
            OrderItem item = new OrderItem();
            item.setOrder(savedOrder);
            item.setProduct(cart.getProduct());
            item.setQuantity(cart.getQuantity());
            item.setStatus(OrderStatus.PENDING);
            orderItemRepository.save(item);
        }

        // 6) تفريغ السلة
        cartRepository.deleteAll(carts);

        // 7) إرجاع DTO بدلاً من الـ Entity مباشرة
        //return toOrderResponse(savedOrder);

        Order freshOrderer = orderRepository.findById(savedOrder.getId())
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!freshOrderer.getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You don't have permission to view this freshOrder");
        }

        return toOrderResponse(freshOrderer);

    }

    // كل طلباتي
    public Page<OrderResponse> getAllOrders(Pageable pageable) {

        User currentUser = getCurrentUser();
        boolean isAdmin = isAdminUser(currentUser);
        boolean isSeller = isSellerUser(currentUser);

        //Page<Order> orders = null;

//        if (isSeller) {
//            orders = orderRepository.findOrdersBySellerId(currentUser.getId() ,pageable);
//        }else if (isAdmin) {
//            orders = orderRepository.findAll(pageable);
//        }else if (!isAdmin & !isSeller) {
//                throw new AccessDeniedException("You can only show yours Order .");
//        }
//
//        return orders.map(this::toOrderResponse);

// 1) غيّر استدعاء الماب داخل getAllOrders

        if (isSeller) {
            Long sellerId = currentUser.getId();
            return orderRepository.findOrdersBySellerId(sellerId, pageable)
                    .map(order -> toOrderResponseForSeller(order, sellerId));
        }
        if (isAdmin) {
            return orderRepository.findAll(pageable)
                    .map(this::toOrderResponse);
        }
        throw new AccessDeniedException("You can only show your own orders.");

    }


    public Page<OrderResponse> getMyOrders(Pageable pageable) {
        User currentUser = getCurrentUser();
        Page<Order> orders = orderRepository.findByUser(currentUser, pageable);
        return orders//.stream()
                .map(this::toOrderResponse);
        //.collect(Collectors.toList());
    }



    @Transactional
    public OrderResponse updateOrderItemStatus(Long itemId, OrderStatusRequest request) {
        User currentUser = getCurrentUser();
        boolean isAdmin = isAdminUser(currentUser);
        boolean isSeller = isSellerUser(currentUser);
        boolean isBuyer = Role.ROLE_BUYER.equals(currentUser.getRole());

        OrderItem item;

        if (isAdmin) {
            item = orderItemRepository.findById(itemId)
                    .orElseThrow(() -> new RuntimeException("Order item not found with id: " + itemId));
        } else if (isSeller) {
            item = orderItemRepository.findByIdAndProductSellerId(itemId, currentUser.getId())
                    .orElseThrow(() -> new AccessDeniedException("You can only update your own order items."));
        } else if (isBuyer) {
            item = orderItemRepository.findById(itemId)
                    .orElseThrow(() -> new RuntimeException("Order item not found with id: " + itemId));
            // buyer لازم يكون صاحب الطلب
            if (!item.getOrder().getUser().getId().equals(currentUser.getId())) {
                throw new AccessDeniedException("You can only update your own order items.");
            }
        } else {
            throw new AccessDeniedException("Not allowed to update order item status.");
        }

        OrderStatus oldStatus = item.getStatus();
        OrderStatus newStatus = request.getStatus();

        if (isBuyer) {
            if (newStatus != OrderStatus.CANCELLED) {
                throw new AccessDeniedException("Buyer can only set item status to CANCELLED.");
            }
            // اختياري: منع الإلغاء بعد الشحن/التسليم
            if (oldStatus == OrderStatus.SHIPPED || oldStatus == OrderStatus.DELIVERED) {
                throw new IllegalStateException("Cannot cancel item after shipping/delivery.");
            }
        } else if (!isAdmin) {
            // seller validation الحالي
            validateItemStatusTransition(oldStatus, newStatus);
        }

        item.setStatus(newStatus);
        orderItemRepository.save(item);

        // recalculate parent order status from all items
        Order order = item.getOrder();
        updateOrderStatusFromItems(order);
        orderRepository.save(order);



        // If seller: return filtered response for that seller only
        if (isSeller) {
            return toOrderResponseForSeller(order, currentUser.getId());
        }

        return toOrderResponse(order);
    }





    private void validateItemStatusTransition(OrderStatus oldStatus, OrderStatus newStatus) {
        if (oldStatus == null || newStatus == null) {
            throw new IllegalArgumentException("Invalid status.");
        }

        // terminal states
        if (oldStatus == OrderStatus.DELIVERED || oldStatus == OrderStatus.CANCELLED) {
            throw new IllegalStateException("Cannot change status after " + oldStatus);
        }

        switch (oldStatus) {
            case PENDING -> {
                if (newStatus != OrderStatus.CONFIRMED && newStatus != OrderStatus.CANCELLED) {
                    throw new IllegalStateException("PENDING can only move to CONFIRMED or CANCELLED.");
                }
            }
            case CONFIRMED -> {
                if (newStatus != OrderStatus.SHIPPED && newStatus != OrderStatus.CANCELLED) {
                    throw new IllegalStateException("CONFIRMED can only move to SHIPPED or CANCELLED.");
                }
            }
            case SHIPPED -> {
                if (newStatus != OrderStatus.DELIVERED) {
                    throw new IllegalStateException("SHIPPED can only move to DELIVERED.");
                }
            }
            default -> throw new IllegalStateException("Unsupported status transition.");
        }
    }


//    private void recalculateOrderStatus(Order order) {
//        List<OrderItem> items = order.getItems();
//        if (items == null || items.isEmpty()) {
//            order.setStatus(OrderStatus.PENDING);
//            return;
//        }
//
//        boolean allCancelled = items.stream().allMatch(i -> i.getStatus() == OrderStatus.CANCELLED);
//        boolean allDelivered = items.stream().allMatch(i -> i.getStatus() == OrderStatus.DELIVERED);
//        boolean anyShipped = items.stream().anyMatch(i -> i.getStatus() == OrderStatus.SHIPPED);
//        boolean anyConfirmed = items.stream().anyMatch(i -> i.getStatus() == OrderStatus.CONFIRMED);
//
//        if (allCancelled) {
//            order.setStatus(OrderStatus.CANCELLED);
//        } else if (allDelivered) {
//            order.setStatus(OrderStatus.DELIVERED);
//        } else if (anyShipped) {
//            order.setStatus(OrderStatus.SHIPPED);
//        } else if (anyConfirmed) {
//            order.setStatus(OrderStatus.CONFIRMED);
//        } else {
//            order.setStatus(OrderStatus.PENDING);
//        }
//    }



// Replace your current updateOrderStatusFromItems(...) with this version

    private void updateOrderStatusFromItems(Order order) {
        if (order.getItems() == null || order.getItems().isEmpty()) {
            order.setStatus(OrderStatus.PENDING);
            return;
        }

        List<OrderStatus> statuses = order.getItems().stream()
                .map(OrderItem::getStatus)
                .toList();

        // 1) all cancelled => CANCELLED
        if (statuses.stream().allMatch(s -> s == OrderStatus.CANCELLED)) {
            order.setStatus(OrderStatus.CANCELLED);
            return;
        }

        // 2) all delivered => DELIVERED
        if (statuses.stream().allMatch(s -> s == OrderStatus.DELIVERED)) {
            order.setStatus(OrderStatus.DELIVERED);
            return;
        }

        // 3) any shipped or delivered => SHIPPED
        if (statuses.stream().anyMatch(s -> s == OrderStatus.SHIPPED || s == OrderStatus.DELIVERED)) {
            order.setStatus(OrderStatus.SHIPPED);
            return;
        }

        // 4) any confirmed => CONFIRMED
        if (statuses.stream().anyMatch(s -> s == OrderStatus.CONFIRMED)) {
            order.setStatus(OrderStatus.CONFIRMED);
            return;
        }

        // 5) fallback
        order.setStatus(OrderStatus.PENDING);
    }



    // كل طلباتي
    public Page<OrderResponse> getUserOrders(Long id, Pageable pageable) {
        User user = userService.getUserEntityById(id);
        Page<Order> orders = orderRepository.findByUser(user, pageable);
        return orders//.stream()
                .map(this::toOrderResponse);
        //.collect(Collectors.toList());
    }


    // طلب معيّن لي
    public OrderResponse getMyOrderById(Long id) {
        User currentUser = getCurrentUser();
        boolean isAdmin = isAdminUser(currentUser);

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.getUser().getId().equals(currentUser.getId()) || !isAdmin) {
            throw new AccessDeniedException("You don't have permission to view this order");
        }

        return toOrderResponse(order);
    }

    // إلغاء طلب (تغيير الحالة فقط)
//    @Transactional
//    public OrderResponse cancelMyOrder(Long id) {
//        User currentUser = getCurrentUser();
//        Order order = orderRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("Order not found"));
//
//        if (!order.getUser().getId().equals(currentUser.getId())) {
//            throw new AccessDeniedException("You don't have permission to cancel this order");
//        }
//
//        order.setStatus(OrderStatus.CANCELLED);
//        Order saved = orderRepository.save(order);
//        return toOrderResponse(saved);
//    }



    @Transactional
    public OrderResponse updateOrderStatus(Long id, OrderStatusRequest request) {

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));

        User currentUser = getCurrentUser();
        boolean isAdmin = isAdminUser(currentUser);
        boolean isBuyer = Role.ROLE_BUYER.equals(currentUser.getRole());

//        System.out.println(" isAdmin " + isAdmin );
//        System.out.println(" isBuyer " + isBuyer );


//        //boolean isSeller = isSellerUser(currentUser);
//
//        // Only admin can update all products, sellers can only update their own products
//        if (!isAdmin && !order.getUser().getId().equals(currentUser.getId())) {
//            throw new AccessDeniedException("Access denied. You can only update your own order.");
//        }
//
//        //product.setStatus(request.getStatus());
//        OrderStatus newStatus = request.getStatus();
//        // قاعدة بسيطة: البائع = ACTIVE أو INACTIVE فقط؛ الأدمن = أي حالة.
//        if (isSeller) {
//            if (newStatus != OrderStatus.CONFIRMED && newStatus != OrderStatus.SHIPPED && newStatus != OrderStatus.DELIVERED) {
//                throw new AccessDeniedException("Sellers may only set status to CONFIRMED or SHIPPED or DELIVERED.");
//            }
//        }else if (!isAdmin & !isSeller) {
//            if (newStatus != OrderStatus.CANCELLED) {
//                throw new AccessDeniedException("Buyers may only set status to CANCELLED .");
//            }
//        }


        OrderStatus newStatus = request.getStatus();
//        // Admin: can change to any status
//        if (isAdmin) {
//            order.setStatus(newStatus);
//            syncOrderItemsStatus( order, newStatus);
//            return toOrderResponse(orderRepository.save(order));
//        }
//        // Buyer: only owner of order, and only CANCELLED
//
//        System.out.println(" currentUser.getId() " + currentUser.getId());
//        System.out.println(" order.getUser().getId() " + order.getUser().getId());
//
//        if (isBuyer) {
//            if (!order.getUser().getId().equals(currentUser.getId())) {
//                throw new AccessDeniedException("Access denied. You can only update your own order.");
//            }
//            if (newStatus != OrderStatus.CANCELLED) {
//                throw new AccessDeniedException("Buyers may only set status to CANCELLED.");
//            }
//            order.setStatus(newStatus);
//            syncOrderItemsStatus( order, newStatus);
//            return toOrderResponse(orderRepository.save(order));
//        }

        // مسار الأدمن
        if (isAdmin) {
            order.setStatus(newStatus);
            syncOrderItemsStatus(order, newStatus);
            orderRepository.save(order);

            // ✅ أعد التحميل من DB لضمان الحصول على البيانات المحدّثة
            Order freshOrder = orderRepository.findById(order.getId())
                    .orElseThrow(() -> new RuntimeException("Order not found"));
            return toOrderResponse(freshOrder);
        }

// مسار البائع
        if (isBuyer) {
            if (!order.getUser().getId().equals(currentUser.getId())) {
                throw new AccessDeniedException("Access denied. You can only update your own order.");
            }
            if (newStatus != OrderStatus.CANCELLED) {
                throw new AccessDeniedException("Buyers may only set status to CANCELLED.");
            }
            order.setStatus(newStatus);
            syncOrderItemsStatus(order, newStatus);
            orderRepository.save(order);

            // ✅ أعد التحميل من DB
            Order freshOrder = orderRepository.findById(order.getId())
                    .orElseThrow(() -> new RuntimeException("Order not found"));
            return toOrderResponse(freshOrder);
        }

        // Seller (and any other role): no access to update orders
        throw new AccessDeniedException("Access denied. You are not allowed to update order status.");

//        order.setStatus(newStatus);
//
//        Order saved = orderRepository.save(order);
//        return toOrderResponse(saved);

    }


    private void syncOrderItemsStatus(Order order, OrderStatus status) {
        if (order.getItems() == null || order.getItems().isEmpty()) return;
        order.getItems().forEach(i -> i.setStatus(status));
        orderItemRepository.saveAll(order.getItems());
    }


    // ===== Helper: تحويل Order إلى OrderResponse =====
    private OrderResponse toOrderResponse(Order order) {
        // تحويل كل OrderItem إلى OrderItemResponse
        List<OrderItemResponse> itemResponses = null;
        if (order.getItems() != null) {
            itemResponses = order.getItems().stream()
                    .map(this::toOrderItemResponse)
                    .collect(Collectors.toList());
        }

        return OrderResponse.builder()
                .id(order.getId())
                .totalAmount(order.getTotalAmount())
                .shippingAddress(order.getShippingAddress())
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .userId(order.getUser().getId())
                .userFullName(order.getUser().getFullName())
                .userEmail(order.getUser().getEmail())
                .items(itemResponses)
                .build();
    }

    // ===== Helper: تحويل OrderItem إلى OrderItemResponse =====
    private OrderItemResponse toOrderItemResponse(OrderItem item) {
        Product product = item.getProduct();
        Category category = product.getCategory();

        return OrderItemResponse.builder()
                .id(item.getId())
                .quantity(item.getQuantity())
                .productId(product.getId())
                .productName(product.getName())
                .productPrice(product.getPrice())
                .productImageUrl(product.getImageUrl())
                .categoryId(category != null ? category.getId() : null)
                .categoryName(category != null ? category.getName() : null)
                .status(item.getStatus())
                .build();
    }


    // 2) أضف هذه الدالة الجديدة داخل OrderService

    private OrderResponse toOrderResponseForSeller(Order order, Long sellerId) {
        List<OrderItem> sellerItems = order.getItems() == null
                ? List.of()
                : order.getItems().stream()
                .filter(item -> item.getProduct() != null
                        && item.getProduct().getSeller() != null
                        && sellerId.equals(item.getProduct().getSeller().getId()))
                .collect(Collectors.toList());

        List<OrderItemResponse> itemResponses = sellerItems.stream()
                .map(this::toOrderItemResponse)
                .collect(Collectors.toList());

        BigDecimal sellerTotal = sellerItems.stream()
                .map(item -> item.getProduct().getPrice()
                        .multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return OrderResponse.builder()
                .id(order.getId())
                .totalAmount(sellerTotal) // مهم: مجموع عناصر البائع فقط
                .shippingAddress(order.getShippingAddress())
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .userId(order.getUser().getId())
                .userFullName(order.getUser().getFullName())
                .userEmail(order.getUser().getEmail())
                .items(itemResponses)
                .build();
    }


    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || authentication.getPrincipal() instanceof String) {
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


}