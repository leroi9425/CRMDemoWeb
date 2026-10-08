package com.crm.BackendCrm.dto.Response;

public record ContactResponseDTO(
    Long id, 
    String contactName, 
    String phoneNumber, 
    String email, 
    String customerCode, 
    Long positionId
) {}
