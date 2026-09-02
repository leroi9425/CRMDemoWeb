package com.crm.BackendCrm.dto.Request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record CompanyRequestDTO(
    @NotBlank String name,
    String description,
    @Min(1) int maxUser
) {}
