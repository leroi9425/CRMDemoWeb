package com.crm.BackendCrm.repository;

import com.crm.BackendCrm.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PermissionRepository extends JpaRepository<Permission, Long> {
    Optional<Permission> findByName(String name);
    Optional<Permission> findById(Long id);
}
