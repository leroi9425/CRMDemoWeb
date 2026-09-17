package com.crm.BackendCrm.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.crm.BackendCrm.dto.Request.PushSubscriptionRequestDTO;
import com.crm.BackendCrm.entity.User;
import com.crm.BackendCrm.repository.UserRepository;
import com.crm.BackendCrm.service.NotificationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/test")
public class NotificationTestController {
    private final NotificationService notificationService;
    private final UserRepository userRepository;

    @PostMapping("/notification")
    public void testNotification() {
        // Tạm hardcode ID để test (Dùng Admin ID 1 giao cho Nhanvien1 ID 2)
        User sender = userRepository.findById(1L).orElseThrow();
        User recipient = userRepository.findById(2L).orElseThrow();

        notificationService.createAndSendNotification(
                sender,
                recipient,
                "🔔 Đây là notification test"
        );
    }

    @PostMapping("/subscribe")
    public ResponseEntity<?> subscribe(
            @RequestBody PushSubscriptionRequestDTO request
    ) {
        System.out.println(request.endPoint());
        System.out.println(request.keys().p256dh());
        System.out.println(request.keys().auth());

        return ResponseEntity.ok("Subscribed");
    }
}