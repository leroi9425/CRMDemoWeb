package com.crm.BackendCrm.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.crm.BackendCrm.entity.Company;
import com.crm.BackendCrm.entity.CompanyPackage;
import com.crm.BackendCrm.entity.Package;

public interface CompanyPackageRepository extends JpaRepository<CompanyPackage, Long> {
    // Custom query methods can be defined here if needed
    Optional<Company> findByCompany_Id(Long companyId);
    Optional<Package> findByPackInfo_Id(Long packageId);
    Optional<CompanyPackage> findTopByCompany_IdAndPackInfo_IdOrderByEndDateDesc(Long companyId, Long packageId);
}
