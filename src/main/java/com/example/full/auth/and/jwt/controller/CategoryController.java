package com.example.full.auth.and.jwt.controller;

import com.example.full.auth.and.jwt.dto.CategoryRequest;
import com.example.full.auth.and.jwt.dto.CategoryResponse;
import com.example.full.auth.and.jwt.dto.ProductRequest;
import com.example.full.auth.and.jwt.dto.ProductResponse;
import com.example.full.auth.and.jwt.model.Category;
import com.example.full.auth.and.jwt.repository.CategoryRepository;
import com.example.full.auth.and.jwt.service.categoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    //private final CategoryRepository categoryRepository;
    private final categoryService categoryService;


    @GetMapping
    public ResponseEntity<Page<CategoryResponse>> getAllcategorys(
            @PageableDefault(page = 0, size = 5, sort = "id", direction = Sort.Direction.ASC)
            Pageable pageable
    ) {
        Page<CategoryResponse> categorys = categoryService.getAllCategorys(pageable);
        return ResponseEntity.ok(categorys);
    }



    @GetMapping("/{id}")
    public ResponseEntity<?> getCategorieById(@PathVariable Long id) {

        try {
            CategoryResponse category = categoryService.getCategoryById(id);
            return ResponseEntity.ok(category);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
        }

    }


//
//    @PostMapping
//    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
//    public ResponseEntity<?> createCategory(@Valid @RequestBody CategoryRequest request) {
//        try {
//            CategoryResponse categoryResponse = categoryService.createCategory(request);
//            return ResponseEntity.ok(categoryResponse);
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
//        }
//    }
//
//
//    @PutMapping("/{id}")
//    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
//    public ResponseEntity<?> updateCategory(
//            @PathVariable Long id,
//            @Valid @RequestBody CategoryRequest request) {
//        try {
//            CategoryResponse updatedCategory = categoryService.updateCategory(id, request);
//            return ResponseEntity.ok(updatedCategory);
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
//        }
//    }
//
//    @DeleteMapping("/{id}")
//    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
//    public ResponseEntity<?> deleteCategory(@PathVariable Long id) {
//        try {
//            categoryService.deleteCategory(id);
//            return ResponseEntity.ok(Map.of("message", "Product deleted successfully"));
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
//        }
//    }
//











}
