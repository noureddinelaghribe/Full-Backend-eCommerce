package com.example.full.auth.and.jwt.controller;

import com.example.full.auth.and.jwt.dto.ProductFilterRequest;
import com.example.full.auth.and.jwt.dto.ProductRequest;
import com.example.full.auth.and.jwt.dto.ProductResponse;
import com.example.full.auth.and.jwt.dto.ProductStatusRequest;
import com.example.full.auth.and.jwt.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 🧾 ProductController - واجهة REST لإدارة المنتجات
 *
 * الوصف:
 * - توفر جميع عمليات CRUD الخاصة بالمنتجات
 * - تعتمد على {@link com.example.full.auth.and.jwt.service.ProductService}
 * - جميع المسارات تحت المسار الأساسي /api/products
 *
 * أهم العمليات:
 * - GET /api/products         → جلب كل المنتجات
 * - GET /api/products/my      → جلب منتجات المستخدم الحالي (البائع)
 * - GET /api/products/{id}    → جلب منتج واحد حسب المعرف
 * - POST /api/products        → إنشاء منتج جديد
 * - PUT /api/products/{id}    → تعديل منتج موجود
 * - DELETE /api/products/{id} → حذف منتج
 */
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // 🟢 جلب كل المنتجات - متاح لكل المستخدمين المصادقين
//    @GetMapping
//    public ResponseEntity<List<ProductResponse>> getAllProducts() {
//        List<ProductResponse> products = productService.getAllProducts();
//        return ResponseEntity.ok(products);
//    }


//     أبسط استدعاء
//    curl http://localhost:8080/api/products?page=0&size=5
//
//             مع ترتيب
//    curl "http://localhost:8080/api/products?page=0&size=5&sort=name,asc"
//
//             صفحة ثانية مرتبة بالسعر تنازلياً
//    curl "http://localhost:8080/api/products?page=1&size=10&sort=price,desc"

    @GetMapping
    public ResponseEntity<Page<ProductResponse>> getAllProducts(
            @PageableDefault(page = 0, size = 15, sort = "id", direction = Sort.Direction.ASC)
            Pageable pageable
    ) {
        Page<ProductResponse> products = productService.getAllProducts(pageable);
        return ResponseEntity.ok(products);
    }

    // 🟢 جلب المنتجات الخاصة بالمستخدم الحالي (البائع)
    @GetMapping("/my")
    public ResponseEntity<Page<ProductResponse>> getMyProducts(
            @PageableDefault(page = 0, size = 5, sort = "id", direction = Sort.Direction.ASC)
            Pageable pageable
    ) {
        Page<ProductResponse> products = productService.getMyProducts(pageable);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/seller/{id}")
    public ResponseEntity<Page<ProductResponse>> getSellerProducts(
            @PathVariable("id") Long id,
            @PageableDefault(page = 0, size = 5, sort = "id", direction = Sort.Direction.ASC)
            Pageable pageable
    ) {
        Page<ProductResponse> products = productService.getSellerProducts(id, pageable);
        return ResponseEntity.ok(products);
    }


    @GetMapping("/category/{id}")
    public ResponseEntity<Page<ProductResponse>> getCategoryProducts(
            @PathVariable("id") Long id,
            @PageableDefault(page = 0, size = 5, sort = "id", direction = Sort.Direction.ASC)
            Pageable pageable
            ) {
        Page<ProductResponse> products = productService.getCategoryProducts(id, pageable);
        return ResponseEntity.ok(products);
    }


    // 🟢 جلب منتج واحد عن طريق المعرف - متاح لكل المستخدمين المصادقين
    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable Long id) {
        try {
            ProductResponse product = productService.getProductById(id);
            return ResponseEntity.ok(product);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
        }
    }

    // 🟢 إنشاء منتج جديد - أي مستخدم مصادق يمكنه إنشاء منتجاته الخاصة
    //@PostMapping
//    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SELLER')")
//    public ResponseEntity<?> createProduct(
//            @RequestPart("product") ProductRequest request,
//            @RequestPart("image") MultipartFile image
//    ) {
//        try {
//            ProductResponse product = productService.createProduct(request, image);
//            return ResponseEntity.ok(product);
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
//        }
//    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SELLER')")
    @PreAuthorize("hasAnyAuthority('ROLE_SELLER')")
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

    // 🟠 تعديل منتج - الأدمن يمكنه تعديل أي منتج، والبائع يمكنه تعديل منتجاته فقط
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
    //@PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SELLER')")
    @PreAuthorize("hasAnyAuthority('ROLE_SELLER')")
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


    // 🔴 حذف منتج - الأدمن يمكنه حذف أي منتج، والبائع يمكنه حذف منتجاته فقط
    @PutMapping("/delete/{id}")
    //@PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SELLER')")
    @PreAuthorize("hasAnyAuthority('ROLE_SELLER')")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        try {
            productService.deleteProduct(id);
            return ResponseEntity.ok(Map.of("message", "Product deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
        }
    }


    // 🔍 Search products by name OR description
//    @GetMapping("/search")
//    public ResponseEntity<List<ProductResponse>> searchProducts(
//            @RequestParam(name = "q", required = false) String query
//    ) {
//        List<ProductResponse> products = productService.searchProducts(query);
//        return ResponseEntity.ok(products);
//    }


//    # بحث بالاسم فقط
//    GET /api/products/search?name=laptop
//
//    # فلتر السعر
//    GET /api/products/search?minPrice=100&maxPrice=500
//
//    # بحث + فئة + متوفر في المخزون
//    GET /api/products/search?name=phone&category=electronics&inStock=true
//
//    # كل شيء مع pagination
//    GET /api/products/search?name=shirt&minPrice=10&maxPrice=50&inStock=true&page=0&size=5&sort=price,asc
//
//    # بدون أي فلتر — يجلب كل المنتجات مع pagination
//    GET /api/products/search?page=0&size=10

    @GetMapping("/search")
    public ResponseEntity<Page<ProductResponse>> searchProducts(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Boolean inStock,
            @PageableDefault(size = 5, sort = "name", direction = Sort.Direction.ASC)
            Pageable pageable) {

        ProductFilterRequest filter = new ProductFilterRequest(
                name, category, minPrice, maxPrice, inStock
        );
        return ResponseEntity.ok(productService.searchProducts(filter, pageable));
    }




}