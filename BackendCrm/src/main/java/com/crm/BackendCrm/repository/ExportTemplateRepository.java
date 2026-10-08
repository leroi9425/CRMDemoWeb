package com.crm.BackendCrm.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.crm.BackendCrm.entity.ExportTemplate;

public interface ExportTemplateRepository extends JpaRepository<ExportTemplate, Long> {
    List<ExportTemplate> findByUserId(Long userId);
}
