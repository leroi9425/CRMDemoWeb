package com.crm.BackendCrm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.transaction.Transactional;
import java.util.List;

import com.crm.BackendCrm.entity.CustomerTmp;

public interface CustomerTmpRepository extends JpaRepository<CustomerTmp, Long> {
    
    @Modifying
    @Transactional
    @Query(value = 
    "DELETE FROM customers_tmp t" +
    "WHERE t.import_id = :importId" +
    "AND EXISTS (" +
    "SELECT 1 FROM customers c"+
    "WHERE c.phone_number = t.phone_number OR c.email = t.email)", nativeQuery = true)
    void deleteDuplicatesWithRealTable(@Param("importId") Long importId);

    // 2. Lấy danh sách sạch (còn sống sót) để bê sang bảng thật
    List<CustomerTmp> findAllByImportId(Long importId);
}
