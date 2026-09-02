package com.crm.BackendCrm.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.crm.BackendCrm.entity.Company;


public interface CompanyRepository extends JpaRepository<Company, Long> {
    Optional<Company> findById(Long id);
    Optional<Company> findByName(String name);
}
