package com.crm.BackendCrm.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import com.crm.BackendCrm.entity.FacebookPage;

public interface FacebookPageRepository extends JpaRepository<FacebookPage, Long> {
    Optional<FacebookPage> findByPageId(String pageId);
    Boolean existsByPageId(String pageId);
    Page<FacebookPage> findByUserId(Long userId, Pageable pageable);
    boolean exexistsByPageIdAndCompanyId(String pageId, String companyId);
}
