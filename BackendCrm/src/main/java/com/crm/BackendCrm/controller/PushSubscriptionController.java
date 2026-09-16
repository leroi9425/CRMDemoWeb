package com.crm.BackendCrm.controller;

import java.util.List;

import org.apache.http.HttpException;
import org.jose4j.jwk.Use;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.crm.BackendCrm.dto.Request.PushSubscriptionRequestDTO;
import com.crm.BackendCrm.entity.PushSubscription;
import com.crm.BackendCrm.entity.User;
import com.crm.BackendCrm.repository.PushSubscriptionRepository;
import com.crm.BackendCrm.repository.UserRepository;
import com.crm.BackendCrm.security.JwtUtils;
import com.crm.BackendCrm.service.PushNotificationService;
import com.crm.BackendCrm.service.PushSubscriptionService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class PushSubscriptionController {

    private final PushSubscriptionService pushSubscriptionService;
    private final PushNotificationService pushNotificationService;
    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;
    private final PushSubscriptionRepository pushSubscriptionRepository;

    @PostMapping("/subscribe/webPush")
    public ResponseEntity<?> subscribe(
            @RequestBody PushSubscriptionRequestDTO request,
            @RequestHeader("Authorization") String authHeader
    ) throws Exception {
        String jwt = authHeader.substring(7);

        Long userId = jwtUtils.extractUserId(jwt);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Không tìm thấy User"
                ));

        pushSubscriptionService.save(user, request);

        List<PushSubscription> pushSubscription = pushSubscriptionRepository.findByUser_Id(userId);      

        pushSubscriptionService.send(pushSubscription.get(0), "123", "123");

        return ResponseEntity.ok().build();
    }

    // @PostMapping("/test/push/{recipientUserId}")
    // public ResponseEntity<?> testNotifi(@PathVariable Long recipientUserId) throws Exception{
    //     PushSubscription subscription = pushSubscriptionRepository.findByUser_Id(recipientUserId)
    //         .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "khong tim thay push sub"));

    //     pushNotificationService.send(
    //         subscription,
    //         "Test",
    //         "User B gửi thông báo"
    //     );
    //     return ResponseEntity.ok().build();
    // }
}