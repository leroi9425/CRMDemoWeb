package com.crm.BackendCrm.dto;

import jakarta.validation.constraints.NotBlank;

public record ProductRequestDTO(
    @NotBlank String productName,
    @NotBlank String detail
) {
}