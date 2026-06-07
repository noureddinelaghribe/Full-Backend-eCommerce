package com.example.full.auth.and.jwt.repository;

import com.example.full.auth.and.jwt.model.Cart;
import com.example.full.auth.and.jwt.model.Product;
import com.example.full.auth.and.jwt.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {

    // كل عناصر السلة لمستخدم معيّن
    Page<Cart> findByUser(User user, Pageable pageable);

    List<Cart> findByUser(User user);

    Optional<Cart> findByUserAndProduct(User user, Product product);

}
