package com.crm.BackendCrm.dto.Response;

public record EmailResponseDTO(
    Long id,
    String emailAddress,
    String customerCode
) {}