package com.crm.BackendCrm.repository;

import com.crm.BackendCrm.entity.Product;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findById(Long id);
    Optional<Product> findByProductName(String productName);
}
