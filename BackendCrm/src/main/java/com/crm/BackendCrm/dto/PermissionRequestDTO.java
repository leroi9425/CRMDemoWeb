package com.crm.BackendCrm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PermissionRequestDTO(
    @NotBlank(message = "Tên quyền không được để trống")
    @Size(max = 50, message = "Tên quyền không vượt quá 50 ký tự")
    String name,

    @Size(max = 255, message = "Mô tả không được quá dài")
    String description,

    // Dành cho cây quyền, nếu là quyền gốc (root) thì gửi lên null
    Long parentId 
) {}