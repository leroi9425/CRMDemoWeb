package com.crm.BackendCrm.dto.Response;

public record CustomerTmpResponseDTO(
    Long id,
    Long importId,
    String dateOfBirth,
    String customerName,
    String phoneNumber,
    String location,
    Boolean gender,
    Long userId,
    Long companyId
) {}
