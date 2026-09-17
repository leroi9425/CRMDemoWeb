package com.crm.BackendCrm.dto.Request;

import jakarta.validation.constraints.NotBlank;

public record CustomerSocialRequestDTO(
    String name,
    String phoneNumber,
    String email
) {}
