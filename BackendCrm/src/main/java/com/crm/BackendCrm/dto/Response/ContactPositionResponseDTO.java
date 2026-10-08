package com.crm.BackendCrm.dto.Response;

public record ContactPositionResponseDTO(
    Long id,
    String contactName,
    String phoneNumber,
    String email,
    String customerCode,
    String positionName
) {}
