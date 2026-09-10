package com.crm.BackendCrm.dto.Request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CustomerRequestDTO(
    @NotBlank String name,
    @NotBlank String phoneNumber,
    @NotBlank String dateOfBirth,
    @NotBlank String location,
    @NotNull Boolean gender,
    @NotNull Long companyId,
    @NotNull Long userId,
    String email
) {}
