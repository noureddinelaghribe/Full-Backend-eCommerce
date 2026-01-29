package com.example.full.auth.and.jwt.repository;

import com.example.full.auth.and.jwt.model.Product;
import com.example.full.auth.and.jwt.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // ترجع كل المنتجات اللي seller بتاعها هو نفس المستخدم
    List<Product> findBySeller(User seller);

}