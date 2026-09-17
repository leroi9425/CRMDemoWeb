package com.example.SocialService.service.TestRabbit;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component 
@RequiredArgsConstructor 
public class TestRabbit implements CommandLineRunner{
    private final RabbitSend rabbitService;

    @Override
    public void run(String... args) throws Exception {
        rabbitService.send("Hello RabbitMQ!");
    }

    
}
