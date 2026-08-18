package com.crm.BackendCrm.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CustomerRequestDTO(
    @NotBlank String name,
    @NotBlank String phoneNumber,
    @NotBlank @Email String email,
    @NotBlank String dateOfBirth,
    @NotBlank String location,
    @NotNull Boolean gender
) {}
