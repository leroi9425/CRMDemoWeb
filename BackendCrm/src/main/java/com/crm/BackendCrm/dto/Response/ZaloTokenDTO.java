package com.crm.BackendCrm.dto.Response;


public record ZaloTokenDTO(
    String accessToken,
    String refreshToken,
    Long expiresIn
) {}
