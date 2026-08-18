package com.crm.BackendCrm.dto;

import java.time.LocalDateTime;

public record UserResponseDTO(
    Long id, String username, String email, String fullName, String role, LocalDateTime createdAt
) {}
