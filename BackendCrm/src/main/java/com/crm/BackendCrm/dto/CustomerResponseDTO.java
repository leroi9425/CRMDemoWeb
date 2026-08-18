package com.crm.BackendCrm.dto;

import java.time.LocalDateTime;

public record CustomerResponseDTO(
    Long id, 
    String name, 
    String phoneNumber, 
    String email, 
    String dateOfBirth, 
    String location, 
    boolean gender, 
    LocalDateTime createdAt
) {}
