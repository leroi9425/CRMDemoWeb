package com.crm.BackendCrm.repository;

import java.util.Optional;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.crm.BackendCrm.entity.Email;

public interface EmailRepository extends JpaRepository<Email, Long> {
    Optional<Email> findById(Long id);
    List<Email> getAllByCustomerCode(String customerCode);
}
