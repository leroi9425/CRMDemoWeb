package com.crm.BackendCrm.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserRequestDTO(
    @NotBlank String username,
    @NotBlank @Email String email,
    @NotBlank String password,
    String fullName
) {}
