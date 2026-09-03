package com.crm.BackendCrm.dto.Response;

import java.time.LocalDateTime;

public record NotificationResponseDTO(
    Long id,
    String message,
    boolean isRead,
    Long recipientId,
    Long senderId,
    LocalDateTime createdAt
) {
}