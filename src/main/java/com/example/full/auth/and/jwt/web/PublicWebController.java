package com.example.full.auth.and.jwt.web;

import com.example.full.auth.and.jwt.dto.ProductFilterRequest;
import com.example.full.auth.and.jwt.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

@Controller
@RequiredArgsConstructor
public class PublicWebController {

    private final ProductService productService;

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/catalog")
    public String catalog(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Boolean inStock,
            @RequestParam(defaultValue = "0") int page,
            Model model
    ) {
        Pageable pageable = PageRequest.of(page, 12, Sort.by("id").descending());
        boolean hasFilters = (name != null && !name.isBlank())
                || (category != null && !category.isBlank())
                || minPrice != null
                || maxPrice != null
                || Boolean.TRUE.equals(inStock);

        if (hasFilters) {
            ProductFilterRequest filter = new ProductFilterRequest(
                    blankToNull(name),
                    blankToNull(category),
                    minPrice,
                    maxPrice,
                    inStock
            );
            model.addAttribute("products", productService.searchProducts(filter, pageable));
        } else {
            model.addAttribute("products", productService.getAllProducts(pageable));
        }
        model.addAttribute("name", name != null ? name : "");
        model.addAttribute("category", category != null ? category : "");
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        model.addAttribute("inStock", inStock != null && inStock);
        model.addAttribute("searchMode", hasFilters);
        return "catalog";
    }

    @GetMapping("/product/{id}")
    public String productDetail(@PathVariable Long id, Model model) {
        model.addAttribute("product", productService.getProductById(id));
        return "product-detail";
    }

    private static String blankToNull(String s) {
        if (s == null || s.isBlank()) {
            return null;
        }
        return s.trim();
    }
}
