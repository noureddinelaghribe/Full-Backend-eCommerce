package com.example.full.auth.and.jwt.web;

import com.example.full.auth.and.jwt.dto.CategoryRequest;
import com.example.full.auth.and.jwt.service.OrderService;
import com.example.full.auth.and.jwt.service.UserService;
import com.example.full.auth.and.jwt.service.categoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/app/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminWebController {

    private final UserService userService;
    private final categoryService categoryService;
    private final OrderService orderService;

    @GetMapping("/users")
    public String users(Model model) {
        model.addAttribute("users", userService.getAllUsers(
                PageRequest.of(0, 200, Sort.by("id").descending())));
        return "admin/users";
    }

    @PostMapping("/users/{id}/delete")
    public String deleteUser( RedirectAttributes ra) {
        try {
            userService.deleteUser();
            ra.addFlashAttribute("message", "User deleted.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/app/admin/users";
    }

    @GetMapping("/categories")
    public String categories(Model model) {
        model.addAttribute("categories", categoryService.getAllCategorys(
                PageRequest.of(0, 200, Sort.by("id").descending())));
        return "admin/categories";
    }

    @GetMapping("/orders")
    public String orders(Model model) {
        model.addAttribute("orders", orderService.getAllOrders(
                PageRequest.of(0, 200, Sort.by("id").descending())));
        return "admin/orders";
    }

    @GetMapping("/categories/new")
    public String newCategory(Model model) {
        model.addAttribute("categoryForm", CategoryRequest.builder().name("").description("").build());
        model.addAttribute("isEdit", false);
        return "admin/category-form";
    }

    @GetMapping("/categories/{id}/edit")
    public String editCategory(@PathVariable Long id, Model model) {
        var c = categoryService.getCategoryById(id);
        model.addAttribute("categoryForm", CategoryRequest.builder()
                .name(c.getName())
                .description(c.getDescription())
                .build());
        model.addAttribute("isEdit", true);
        model.addAttribute("editId", id);
        return "admin/category-form";
    }

    @PostMapping("/categories")
    public String createCategory(CategoryRequest form, RedirectAttributes ra) {
        try {
            categoryService.createCategory(form);
            ra.addFlashAttribute("message", "Category created.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/app/admin/categories/new";
        }
        return "redirect:/app/admin/categories";
    }

    @PostMapping("/categories/{id}")
    public String updateCategory(@PathVariable Long id, CategoryRequest form, RedirectAttributes ra) {
        try {
            categoryService.updateCategory(id, form);
            ra.addFlashAttribute("message", "Category updated.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/app/admin/categories";
    }

    @PostMapping("/categories/{id}/delete")
    public String deleteCategory(@PathVariable Long id, RedirectAttributes ra) {
        try {
            categoryService.deleteCategory(id);
            ra.addFlashAttribute("message", "Category deleted.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/app/admin/categories";
    }
}
