package com.example.full.auth.and.jwt.repository;

import com.example.full.auth.and.jwt.model.Address;
import com.example.full.auth.and.jwt.model.Product;
import com.example.full.auth.and.jwt.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;

public interface AddressRepository extends JpaRepository<Address, Long> {

    Page<Address> findByUser(User user, Pageable pageable);

    Optional<Address> findByUserAndIsDefaultTrue(User user);

    @Modifying
    @Transactional
    @Query("UPDATE Address a SET a.isDefault = false WHERE a.user.id = :userId")
    void clearDefaultAddresses(Long userId);

}
