package com.crm.BackendCrm.repository;

import com.crm.BackendCrm.entity.Customer;

import java.util.Optional;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CustomerRepository extends JpaRepository<Customer, Long>, JpaSpecificationExecutor<Customer> {
    boolean existsByPhoneNumber(String phoneNumber);
    boolean existsByEmail(String email);
    Page<Customer> findByUserId(Long userId, Pageable pageable); 
    Optional<Customer> findByCustomerCode(String costomerCode);
    @Query(value = "SELECT * FROM customers c WHERE " +
               "(:isTicked = false OR c.customer_name LIKE CONCAT('%', :keyword, '%'))", 
       nativeQuery = true)
    List<Customer> searchCustomer(@Param("keyword") String keyword, @Param("isTicked") boolean isTicked);
}
