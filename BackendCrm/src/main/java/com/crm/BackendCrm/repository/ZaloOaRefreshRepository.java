package com.crm.BackendCrm.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.crm.BackendCrm.entity.ZaloOaRefresh;
// import java.util.List;

public interface ZaloOaRefreshRepository extends JpaRepository<ZaloOaRefresh, Long> {
    Boolean existsByOaId(String oaId);
    // List<ZaloOaRefresh> findAllByOaId(String oaId);
    Boolean existsByOaIdAndCompanyId(String oaId, String companyId);
    Optional<ZaloOaRefresh> findByOaIdAndCompanyId(String oaId, String companyId);
    Optional<ZaloOaRefresh> findByOaId(String oaId);
    Boolean existsByIdAndUserId(Long id, Long userId);
}
