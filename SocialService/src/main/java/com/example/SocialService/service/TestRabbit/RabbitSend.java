package com.example.SocialService.service.TestRabbit;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RabbitSend {
    private final RabbitTemplate rabbitTemplate;

    public void send(String mess){
        System.out.println("Đã gửi queue");
        rabbitTemplate.convertAndSend("test-queue",mess);
    }
}
