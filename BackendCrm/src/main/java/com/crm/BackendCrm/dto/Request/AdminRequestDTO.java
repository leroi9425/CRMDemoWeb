package com.crm.BackendCrm.dto.Request;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AdminRequestDTO(
    @NotBlank String userName,
    @NotBlank String passWord,
    @NotNull LocalDateTime createAt
) {}
