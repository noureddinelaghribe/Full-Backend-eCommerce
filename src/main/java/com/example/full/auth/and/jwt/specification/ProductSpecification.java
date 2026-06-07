package com.example.full.auth.and.jwt.specification;

import com.example.full.auth.and.jwt.model.Product;
import com.example.full.auth.and.jwt.model.ProductStatus;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ProductSpecification {

    public static Specification<Product> filter(
            String name,
            String category,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Boolean inStock
    ) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Public catalog / search: only ACTIVE products (aligned with GET /api/products)
            predicates.add(cb.equal(root.get("status"), ProductStatus.ACTIVE));

            if (name != null && !name.trim().isEmpty()) {
                predicates.add(
                        cb.like(cb.lower(root.get("name")),
                                "%" + name.trim().toLowerCase() + "%")
                );
            }

            if (category != null && !category.trim().isEmpty()) {
                predicates.add(
                        cb.equal(
                                cb.lower(root.get("category").get("name")),
                                category.trim().toLowerCase()
                        )
                );
            }

            if (minPrice != null) {
                predicates.add(
                        cb.greaterThanOrEqualTo(root.get("price"), minPrice)
                );
            }

            if (maxPrice != null) {
                predicates.add(
                        cb.lessThanOrEqualTo(root.get("price"), maxPrice)
                );
            }

            if (inStock != null && inStock) {
                predicates.add(
                        cb.greaterThan(root.get("stock"), 0)
                );
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}