package com.example.SocialWorker.dto;

public record CustomerSocialResponseDTO(
    String name,
    String phoneNumber,
    String email,
    Long userId,
    Long companyId
) {}
