package com.crm.BackendCrm.dto.Response;

import java.time.LocalDateTime;

public record CustomerResponseDTO(
    Long id, 
    String name, 
    String phoneNumber,
    String dateOfBirth, 
    String location, 
    boolean gender, 
    LocalDateTime createdAt,
    Long companyId,
    Long userId,
    String customerCode
) {}
