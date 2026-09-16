package com.example.SocialService.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RabbitService {
    private final RabbitTemplate rabbitTemplate;

    public void send(String mess){
        rabbitTemplate.convertAndSend("test-queue",mess);
        System.out.println("Đã gửi queue");
    }
}
