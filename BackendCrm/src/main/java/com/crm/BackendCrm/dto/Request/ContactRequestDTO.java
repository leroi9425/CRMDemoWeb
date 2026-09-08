package com.crm.BackendCrm.dto.Request;

public record ContactRequestDTO(
    String contactName, 
    String phoneNumber, 
    String email, 
    String customerCode, 
    Long positionId
) {}