package com.crm.BackendCrm.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.crm.BackendCrm.entity.Notification;
import com.crm.BackendCrm.repository.NotificationRepository;
import com.crm.BackendCrm.repository.UserRepository;
import com.crm.BackendCrm.dto.Response.NotificationResponseDTO;
import com.crm.BackendCrm.entity.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;
    

    public  NotificationResponseDTO toDTO(Notification notification) {
        return new NotificationResponseDTO(
            notification.getId(),
            notification.getMessage(),
            notification.getCreatedAt()
        );
    }

}
