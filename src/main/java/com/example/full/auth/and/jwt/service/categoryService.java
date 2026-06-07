package com.example.full.auth.and.jwt.service;

import com.example.full.auth.and.jwt.dto.CategoryRequest;
import com.example.full.auth.and.jwt.dto.CategoryResponse;
import com.example.full.auth.and.jwt.dto.ProductRequest;
import com.example.full.auth.and.jwt.dto.ProductResponse;
import com.example.full.auth.and.jwt.model.Category;
import com.example.full.auth.and.jwt.model.Product;
import com.example.full.auth.and.jwt.model.User;
import com.example.full.auth.and.jwt.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class categoryService {
    private final CategoryRepository categoryRepository;

    public Page<CategoryResponse> getAllCategorys(Pageable pageable) {
        return categoryRepository.findAll(pageable)//.stream()
                .map(this::convertToCategoryResponse);
                //.collect(Collectors.toList());
    }

    public CategoryResponse getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));

        return convertToCategoryResponse(category);
    }


    public CategoryResponse createCategory(CategoryRequest request) {
//        User currentUser = getCurrentUser();
//        boolean isAdmin = isAdminUser(currentUser);
//
//        // Only admin can update all products, sellers can only update their own products
//        if (!isAdmin) {
//            throw new AccessDeniedException("Access denied. You can only create Category.");
//        }

        Category category = Category.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();

        Category savedProduct = categoryRepository.save(category);
        return convertToCategoryResponse(savedProduct);
    }


    public CategoryResponse updateCategory(Long id, CategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));

//        User currentUser = getCurrentUser();
//        boolean isAdmin = isAdminUser(currentUser);
//
//        // Only admin can update all products, sellers can only update their own products
//        if (!isAdmin) {
//            throw new AccessDeniedException("Access denied. You can only update Category.");
//        }

        category.setName(request.getName());
        category.setDescription(request.getDescription());

        Category updatedCategory = categoryRepository.save(category);
        return convertToCategoryResponse(updatedCategory);
    }


    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new RuntimeException("Category not found with id: " + id);
        }

//        User currentUser = getCurrentUser();
//        boolean isAdmin = isAdminUser(currentUser);
//
//        // Only admin can delete all products, sellers can only delete their own products
//        if (!isAdmin) {
//            throw new AccessDeniedException("Access denied. You can only delete Category.");
//        }

        categoryRepository.deleteById(id);
    }





    private CategoryResponse convertToCategoryResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .build();
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal() instanceof String) {
            throw new AccessDeniedException("Authentication required");
        }

        return (User) authentication.getPrincipal();
    }


    private boolean isAdminUser(User user) {
        return user != null && "ADMIN".equals(user.getRole());
    }



}
