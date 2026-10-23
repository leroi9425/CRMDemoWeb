package com.crm.BackendCrm.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import com.crm.BackendCrm.entity.ZaloOaAccess;


public interface ZaloOaAccessRepository extends JpaRepository<ZaloOaAccess, Long> {
    Boolean existsByOaId(String oaId);
    List<ZaloOaAccess> findAllByOaId(String oaId);
    Boolean existsByOaIdAndCompanyId(String oaId, String companyId);
    Optional<ZaloOaAccess> findByOaIdAndCompanyId(String oaId, String companyId);
    Optional<ZaloOaAccess> findByOaId(String oaId);
    Page<ZaloOaAccess> findByUserId(Long userId, Pageable pageable);
    Boolean existsByIdAndUserId(Long id, Long userId);
}
