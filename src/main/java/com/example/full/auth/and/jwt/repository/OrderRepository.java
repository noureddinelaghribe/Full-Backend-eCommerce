package com.example.full.auth.and.jwt.repository;

import com.example.full.auth.and.jwt.model.Order;
import com.example.full.auth.and.jwt.model.OrderItem;
import com.example.full.auth.and.jwt.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    // كل الطلبات لمستخدم معيّن
    Page<Order> findByUser(User user, Pageable pageable);

    @Query(
            value = """
        select distinct o
        from Order o
        join o.items oi
        join oi.product p
        where p.seller.id = :sellerId
        order by o.id desc
        """,
            countQuery = """
        select count(distinct o.id)
        from Order o
        join o.items oi
        join oi.product p
        where p.seller.id = :sellerId
        """
    )
    Page<Order> findOrdersBySellerId(@Param("sellerId") Long sellerId, Pageable pageable);




}