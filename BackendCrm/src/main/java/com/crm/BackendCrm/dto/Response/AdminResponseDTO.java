package com.crm.BackendCrm.dto.Response;

import java.time.LocalDateTime;

public record AdminResponseDTO(
    Long id,
    String userName,
    String passWord,
    LocalDateTime createAt
) {}
