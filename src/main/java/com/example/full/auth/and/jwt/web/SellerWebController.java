package com.example.full.auth.and.jwt.web;

import com.example.full.auth.and.jwt.dto.ProductRequest;
import com.example.full.auth.and.jwt.dto.ProductStatusRequest;
import com.example.full.auth.and.jwt.model.ProductStatus;
import com.example.full.auth.and.jwt.service.ProductService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/app/seller")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('SELLER','ADMIN')")
public class SellerWebController {

    private final ProductService productService;
    private final categoryService categoryService;

    @GetMapping("/products")
    public String myProducts(Model model) {
        model.addAttribute("products", productService.getMyProducts(
                PageRequest.of(0, 100, Sort.by("id").descending())));
        return "seller/products";
    }

    @GetMapping("/products/new")
    public String newProduct(Model model) {
        model.addAttribute("productForm", ProductRequest.builder()
                .name("")
                .description("")
                .build());
        model.addAttribute("categories", categoryService.getAllCategorys(PageRequest.of(0, 500)));
        model.addAttribute("isEdit", false);
        return "seller/product-form";
    }

    @GetMapping("/products/{id}/edit")
    public String editProduct(@PathVariable Long id, Model model) {
        var p = productService.getProductById(id);
        model.addAttribute("productForm", ProductRequest.builder()
                .name(p.getName())
                .description(p.getDescription())
                .price(p.getPrice())
                .stock(p.getStock())
                .categoryId(p.getCategoryId())
                .imageUrl(p.getImageUrl())
                .build());
        model.addAttribute("categories", categoryService.getAllCategorys(PageRequest.of(0, 500)));
        model.addAttribute("isEdit", true);
        model.addAttribute("editId", id);
        model.addAttribute("currentStatus", p.getStatus());
        return "seller/product-form";
    }

    @PostMapping("/products")
    public String createProduct(ProductRequest form, RedirectAttributes ra) {
        try {
            productService.createProduct(form, null);
            ra.addFlashAttribute("message", "Product created (pending approval if applicable).");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/app/seller/products/new";
        }
        return "redirect:/app/seller/products";
    }

    @PostMapping("/products/{id}")
    public String updateProduct(@PathVariable Long id, ProductRequest form, RedirectAttributes ra) {
        try {
            productService.updateProduct(id, form);
            ra.addFlashAttribute("message", "Product updated.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/app/seller/products";
    }

    @PostMapping("/products/{id}/delete")
    public String deleteProduct(@PathVariable Long id, RedirectAttributes ra) {
        try {
            productService.deleteProduct(id);
            ra.addFlashAttribute("message", "Product deleted.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/app/seller/products";
    }

    @PostMapping("/products/{id}/status")
    public String status(@PathVariable Long id, @RequestParam ProductStatus status, RedirectAttributes ra) {
        try {
            productService.updateProductStatus(id, ProductStatusRequest.builder().status(status).build());
            ra.addFlashAttribute("message", "Status updated.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/app/seller/products";
    }
}
