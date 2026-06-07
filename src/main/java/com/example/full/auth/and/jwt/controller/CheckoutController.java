//package com.example.full.auth.and.jwt.controller;
//
//import com.example.full.auth.and.jwt.dto.CheckoutRequest;
//import com.example.full.auth.and.jwt.dto.CheckoutResponse;
//import com.example.full.auth.and.jwt.dto.UpdatePaymentStatusRequest;
//import com.example.full.auth.and.jwt.service.CheckoutService;
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//import java.util.Map;
//
///**
// * 💳 CheckoutController - واجهة REST لعملية إكمال الطلب (Checkout)
// *
// * الوصف:
// * - تنفّذ عملية الشراء بناءً على عناصر السلة الحالية للمستخدم
// * - تحفظ تفاصيل الطلب (المبلغ الكلي، طريقة الدفع، العنوان، العناصر)
// * - جميع المسارات تحت المسار الأساسي /api/checkout
// *
// * أهم العمليات:
// * - POST /api/checkout        → تنفيذ عملية checkout للمستخدم الحالي
// * - GET  /api/checkout        → جلب جميع طلبات الشراء السابقة للمستخدم
// * - GET  /api/checkout/{id}   → جلب تفاصيل طلب شراء واحد
// */
//@RestController
//@RequestMapping("/api/checkout")
//@RequiredArgsConstructor
//public class CheckoutController {
//
//    private final CheckoutService checkoutService;
//
//    @PostMapping
//    public ResponseEntity<CheckoutResponse> doCheckout(
//            @Valid @RequestBody CheckoutRequest request
//    ) {
//        CheckoutResponse response = checkoutService.checkout(request);
//        return ResponseEntity.status(HttpStatus.CREATED).body(response);
//    }
//
//    @GetMapping
//    public ResponseEntity<List<CheckoutResponse>> getMyCheckouts() {
//        return ResponseEntity.ok(checkoutService.getMyCheckouts());
//    }
//
//    @GetMapping("/{id}")
//    public ResponseEntity<CheckoutResponse> getCheckout(@PathVariable Long id) {
//        return ResponseEntity.ok(checkoutService.getCheckoutById(id));
//    }
//
//    @PatchMapping("/{id}/payment-status")
//    public ResponseEntity<CheckoutResponse> updatePaymentStatus(
//            @PathVariable Long id,
//            @Valid @RequestBody UpdatePaymentStatusRequest request
//    ) {
//        CheckoutResponse response = checkoutService.updatePaymentStatus(id, request.getPaymentStatus());
//        return ResponseEntity.ok(response);
//    }
//
//
//    @DeleteMapping("/{id}")
//    //@PreAuthorize("hasAuthority('ROLE_ADMIN')")
//    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
//        try {
//            checkoutService.deleteCheckout(id);
//            return ResponseEntity.ok(Map.of("message", "checkout deleted successfully"));
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
//        }
//    }
//
//}
//
