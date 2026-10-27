package com.crm.BackendCrm.dto.Response;

public record PackageResponseDTO(
    Long id,
    Long price,
    String name,
    String description,
    boolean status
) {}
