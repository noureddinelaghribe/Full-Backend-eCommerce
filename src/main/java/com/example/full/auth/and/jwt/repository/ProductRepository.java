package com.example.full.auth.and.jwt.repository;

import com.example.full.auth.and.jwt.model.Category;
import com.example.full.auth.and.jwt.model.Product;
import com.example.full.auth.and.jwt.model.ProductStatus;
import com.example.full.auth.and.jwt.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>,
        JpaSpecificationExecutor<Product> {

    //  for admin


    //SELECT * FROM product WHERE status = ?
    Page<Product> findByStatus(ProductStatus status, Pageable pageable);


    //SELECT * FROM product WHERE seller_id = ?
    Page<Product> findBySeller(User seller, Pageable pageable);


    //SELECT * FROM product WHERE status = ? AND seller_id = ?
    Page<Product> findByStatusAndSeller(ProductStatus status, User seller, Pageable pageable);


    //SELECT * FROM product WHERE status = ? AND category_id = ?
    Page<Product> findByStatusAndCategory(ProductStatus status, Category category, Pageable pageable);




    //  for user

    Product findByIdAndDeletedFalse(Long id);

    Page<Product> findByDeletedFalseAndStatus(ProductStatus status, Pageable pageable);

    Page<Product> findByDeletedFalseAndSeller(User seller, Pageable pageable);

    Page<Product> findByDeletedFalseAndStatusAndSeller(ProductStatus status, User seller, Pageable pageable);

    Page<Product> findByDeletedFalseAndStatusAndCategory(ProductStatus status, Category category, Pageable pageable);

}