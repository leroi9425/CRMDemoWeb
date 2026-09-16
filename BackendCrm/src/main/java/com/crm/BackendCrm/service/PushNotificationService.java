package com.crm.BackendCrm.service;

import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import nl.martijndwars.webpush.Notification;
import com.crm.BackendCrm.entity.PushSubscription;

import jakarta.annotation.PostConstruct;
import nl.martijndwars.webpush.PushService;
import com.crm.BackendCrm.entity.PushSubscription;

@Service
public class PushNotificationService {
}