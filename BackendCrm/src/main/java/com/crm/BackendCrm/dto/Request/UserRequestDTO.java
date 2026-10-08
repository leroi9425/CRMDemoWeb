package com.crm.BackendCrm.dto.Request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserRequestDTO(
    @NotBlank String username,
    @NotBlank @Email String email,
    @NotBlank String password,
    String fullName,
    @NotNull Long companyId
) {}
