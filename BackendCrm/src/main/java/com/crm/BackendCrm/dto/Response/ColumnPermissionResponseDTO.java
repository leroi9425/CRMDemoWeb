package com.crm.BackendCrm.dto.Response;

import java.util.List;

public record ColumnPermissionResponseDTO(
    int column,
    List<PermissionResponseDTO> listPermission
){}
