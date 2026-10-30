package com.crm.BackendCrm.repository;

import com.crm.BackendCrm.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PermissionRepository extends JpaRepository<Permission, Long> {
    Optional<Permission> findByName(String name);
    Optional<Permission> findById(Long id);
    @Query("SELECT COUNT(DISTINCT p.category) FROM Permission p")
    int countDistinctCategories();
    @Query("SELECT DISTINCT p.category FROM Permission p WHERE p.category IS NOT NULL")
    List<String> findDistinctCategories();
    List<Permission> findAllByCategory(String category);
    List<Permission> findAllByCategoryAndRoles_Name(String category, String roleName);
    List<Permission> findAllByRoles_Name(String name);
}
