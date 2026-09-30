package com.example.SocialWorker.service;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Component 
@RequiredArgsConstructor 
public class RabbitConsumeSer{
    private final FacebookService facebookService;
    private final ZaloService zaloService;
    private final String[] socialSources = {"Facebook", "Zalo", "Tiktok", "Instagram"};
    
    @RabbitListener (queues = "test-queue")
    public void recevice(String mess){
        // System.out.println("nhan duoc mess: " + mess);
        if(mess == null || mess.isEmpty()) 
            return;
        String messClear = mess.trim();
        if (!messClear.startsWith("{") && !messClear.startsWith("[")) {
            System.out.println("do khong phai json ne ko chay tiep");
            return; 
        }
        // chuyển String -> Json
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(messClear);
            String source = rootNode.path("source").asText();
            switch (source) {
                case "Facebook":
                    facebookService.handleFaceWebHook(rootNode);
                    break;
                case "Zalo":
                    zaloService.handleZaloWebHook(rootNode);
                default:
                    break;
            }
        } catch (Exception e) {
            System.out.println("Loi khi xu ly Rabbit: " + e);
        }
    }

    // hàm bóc tách dữ liệu
}
