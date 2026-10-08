package com.crm.BackendCrm.dto.Response;

public record ExportTemplateResponseDTO(
    Long id,
    String name,
    String fields,
    Long userId
) {}
