package com.crm.BackendCrm.dto.Request;

public record CustomerSocialRequestDTO(
    String name,
    String phoneNumber,
    String email
) {}
