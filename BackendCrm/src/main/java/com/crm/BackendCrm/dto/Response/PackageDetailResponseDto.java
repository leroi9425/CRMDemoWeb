package com.crm.BackendCrm.dto.Response;

import java.util.List;

public record PackageDetailResponseDto(
    Long id,
    Long price,
    String name,
    String description,
    boolean status,
    List<PermissionResponseDTO> permisisons
) {}
