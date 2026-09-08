package com.crm.BackendCrm.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.crm.BackendCrm.entity.Notification;
import com.crm.BackendCrm.repository.NotificationRepository;
import com.crm.BackendCrm.dto.Response.NotificationResponseDTO;
import com.crm.BackendCrm.entity.User;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public void createAndSendNotification(User sender, User recipient, String message) {
        Notification notification = new Notification();
        notification.setSender(sender);
        notification.setRecipient(recipient);
        notification.setMessage(message);
        notification.setRead(false);
        notification.setCreatedAt(LocalDateTime.now());

        // luu notification vao database
        notificationRepository.save(notification);

        // tao response DTO
        NotificationResponseDTO notificatioResponseDTO = toDTO(notification);

        // gui real time cho recipient qua WebSocket
        messagingTemplate.convertAndSend(
            "/topic/notifications/" + recipient.getId(), 
            notificatioResponseDTO
        );
    }

    public  NotificationResponseDTO toDTO(Notification notification) {
        return new NotificationResponseDTO(
            notification.getId(),
            notification.getMessage(),
            notification.isRead(),
            notification.getRecipient().getId(),
            notification.getSender().getId(),
            notification.getCreatedAt()
        );
    }
}
