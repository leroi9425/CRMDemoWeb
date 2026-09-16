package com.example.SocialService.service;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component 
public class RabbitConsumeService {
    @RabbitListener (queues = "test-queue")
    public void recevice(String mess){
        System.out.println(mess);
    }
}
