package com.example.full.auth.and.jwt.repository;

import com.example.full.auth.and.jwt.model.Cart;
import com.example.full.auth.and.jwt.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartRepository extends JpaRepository<Cart, Long> {

    // كل عناصر السلة لمستخدم معيّن
    List<Cart> findByUser(User user);

}
