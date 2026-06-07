package com.example.full.auth.and.jwt.repository;

import com.example.full.auth.and.jwt.model.Category;
import com.example.full.auth.and.jwt.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    //List<Product> findByCategory(Product product);


}
