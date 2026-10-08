package com.crm.BackendCrm.service;

import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.stereotype.Service;

import com.crm.BackendCrm.dto.Request.PushSubscriptionRequestDTO;
import com.crm.BackendCrm.entity.PushSubscription;
import com.crm.BackendCrm.entity.User;
import com.crm.BackendCrm.repository.PushSubscriptionRepository;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import nl.martijndwars.webpush.PushService;
import org.springframework.beans.factory.annotation.Value;

import nl.martijndwars.webpush.Notification;

@Service
@RequiredArgsConstructor
public class PushSubscriptionService {
    // quản lý địa chỉ gửi

    private final PushSubscriptionRepository pushSubscriptionRepository;

    public void save(
            User user,
            PushSubscriptionRequestDTO request
    ) {
        PushSubscription subscription = new PushSubscription();

        subscription.setUser(user);
        subscription.setEndpoint(request.endPoint());
        subscription.setP256dh(request.keys().p256dh());
        subscription.setAuth(request.keys().auth());

        pushSubscriptionRepository.save(subscription);
    }



    // push notification
    
    // Quản lý địa chỉ gửi

    @Value("${vapid.public-key}")
    private String publicKey;

    @Value("${vapid.private-key}")
    private String privateKey;

    private PushService pushService;

    @PostConstruct
    public void init() throws Exception {

        Security.addProvider(new BouncyCastleProvider());

        pushService = new PushService(
                publicKey,
                privateKey,
                "mailto:test@example.com"
        );
    }

    public void send(
        PushSubscription subscription,
        String title,
        String message
    ) throws Exception {

        String payload = """
            {
                "title": "%s",
                "message": "%s"
            }
            """.formatted(title, message);

        Notification notification = new Notification(
                subscription.getEndpoint(),
                subscription.getP256dh(),
                subscription.getAuth(),
                payload
        );

        pushService.send(notification);
    }
}