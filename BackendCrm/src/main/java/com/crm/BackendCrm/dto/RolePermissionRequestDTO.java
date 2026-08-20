package com.crm.BackendCrm.dto;

import java.util.List;
import jakarta.validation.constraints.NotNull;

public record RolePermissionRequestDTO(
    @NotNull(message = "Danh sách quyền không được để trống")
    List<Long> permissionIds
) {}
