package com.example.SocialService.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.node.ObjectNode;


@Service 
@RequiredArgsConstructor 
@EnableScheduling
public class SocialMediaService {
    private final RabbitTemplate rabbitTemplate;

    public ResponseEntity<?> createFacebookJson(JsonNode data){
        System.out.println("SocialService/SocialMediaService: " + data);
        ((ObjectNode)data).put("source", "Facebook");

        rabbitTemplate.convertAndSend("test-queue", data.toString());
        System.out.println("Complete send to rabbitMQ");
        return ResponseEntity.ok().build();
    }
    public ResponseEntity<?> createZaloJson(JsonNode data){
        System.out.println("SocialService/SocialMediaService: " + data);
        ((ObjectNode)data).put("source", "Zalo");

        rabbitTemplate.convertAndSend("test-queue", data.toString());
        System.out.println("Complete send to rabbitMQ");
        return ResponseEntity.ok().build();
    }
}
