package com.crm.BackendCrm.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.crm.BackendCrm.entity.Package;

public interface PackageRepository extends JpaRepository<Package, Long> {
    @Query(value = "SELECT package_id, permission_id FROM package_permissions", nativeQuery = true)
    List<Object[]> getAllPackagePermission();
}
