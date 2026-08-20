package com.crm.BackendCrm.dto;

import java.util.List;
import java.io.Serializable;

public record PermissionResponseDTO(
    Long id,
    String name,
    String description,
    
    // Cái túi này sẽ chứa các quyền con lồng vào bên trong để trả về cho Giao diện vẽ Cây
    List<PermissionResponseDTO> children 
) implements Serializable {}
