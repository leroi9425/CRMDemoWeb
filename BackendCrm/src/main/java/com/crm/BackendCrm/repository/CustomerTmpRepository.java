package com.crm.BackendCrm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.transaction.Transactional;

import com.crm.BackendCrm.entity.CustomerTmp;

public interface CustomerTmpRepository extends JpaRepository<CustomerTmp, Long> {
    
    @Modifying
    @Transactional
    // @Query(value = "CALL import_khachhang_json(:jsonData)", nativeQuery = true)
    @Query(value = "CALL import_customers3(:jsonData)", nativeQuery = true)
    void processCustomerImport(@Param("jsonData") String jsonData);
}
