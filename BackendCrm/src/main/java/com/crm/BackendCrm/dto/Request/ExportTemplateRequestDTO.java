package com.crm.BackendCrm.dto.Request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ExportTemplateRequestDTO(
    @NotBlank String name,
    @NotBlank String fields,
    @NotNull Long userId
) {}
