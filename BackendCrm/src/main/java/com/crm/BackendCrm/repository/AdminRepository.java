package com.crm.BackendCrm.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.crm.BackendCrm.entity.Admin;

public interface AdminRepository extends JpaRepository<Admin, Long> {
    Optional<Admin> findById(Long id);
    Optional<Admin> findByUserName(String userName);
}
