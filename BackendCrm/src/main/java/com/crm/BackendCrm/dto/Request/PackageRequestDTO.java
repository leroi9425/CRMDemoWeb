package com.crm.BackendCrm.dto.Request;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PackageRequestDTO(
    @NotNull Long price,
    @NotBlank String name,
    String description,
    @NotNull Boolean status,
    @NotNull List<Long> listPermissionId
) {}
