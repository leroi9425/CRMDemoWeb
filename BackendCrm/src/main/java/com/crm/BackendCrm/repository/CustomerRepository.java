package com.crm.BackendCrm.repository;

import com.crm.BackendCrm.entity.Customer;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    boolean existsByPhoneNumber(String phoneNumber);
    boolean existsByEmail(String email);
    Page<Customer> findByUserId(Long userId, Pageable pageable);    
    Optional<Customer> findByCustomerCode(String costomerCode);
}
