package com.crm.BackendCrm.dto.Response;

import java.util.List;

public record CustomerDetailResponseDTO(
    String customerName,
    String dateOfBirth,
    List<EmailResponseDTO> emails,
    String phoneNumber,
    boolean gender,
    List<ContactPositionResponseDTO> contactPositions
) {}
