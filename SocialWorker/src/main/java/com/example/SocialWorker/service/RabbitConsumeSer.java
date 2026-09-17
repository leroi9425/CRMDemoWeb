package com.example.SocialWorker.service;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.example.SocialWorker.dto.CustomerSocialResponseDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Component 
@RequiredArgsConstructor 
public class RabbitConsumeSer{
    @RabbitListener (queues = "test-queue")
    public void recevice(String mess){
        // System.out.println("RabbitConsumeService : "+new Date());
        // System.out.println(mess);
        // System.out.println("End RabbitConsumeService");

        // xu ly cac kieu
        ObjectMapper mapper = new ObjectMapper();
        String chatContext = "";
        String cusName = "";
        String phoneNumber = "";
        String email = "";
        try {
            JsonNode rootNode = mapper.readTree(mess);
            JsonNode entryNode = rootNode.path("entry").get(0);
            if(entryNode.has("changes")){
                // comment
                chatContext = entryNode.path("changes").get(0).path("value").path("message").asText();
                cusName = entryNode.path("changes").get(0).path("value").path("from").path("name").asText();
            }
            else if(entryNode.has("messaging")){
                // tin nhan
                // chatContext = entryNode.path("messaging").path("sender").path("id").asText();
            }

            if(chatContext != "" && !chatContext.isBlank()){
                // lay sdt
                Pattern patternPhoneNumber = Pattern.compile("(0[3|5|7|8|9]\\d{8})");
                Matcher matcherPhoneNumber = patternPhoneNumber.matcher(chatContext);

                if(matcherPhoneNumber.find()){
                    phoneNumber = matcherPhoneNumber.group();
                    System.out.println("recevice" + new Date());
                    System.out.println("ten: "+cusName);
                    System.out.println("so dien thoai: "+phoneNumber);
                    System.out.println("end recevice");
                }

                // lay email
                Pattern patternEmail = Pattern.compile("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}");
                Matcher matcherEmail = patternEmail.matcher(chatContext);

                if(matcherEmail.find()){
                    email = matcherEmail.group();
                    System.out.println("recevice" + new Date());
                    System.out.println("email: "+email);
                    System.out.println("end recevice");
                }

            }
            CustomerSocialResponseDTO cusSDTO = new CustomerSocialResponseDTO(
                cusName,
                phoneNumber,
                email
            );
            RestClient client = RestClient.create();

            String result = client.post()
                            .uri("http://localhost:8080/api/customer/social")
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(cusSDTO)
                            .retrieve()
                            .body(String.class);

            System.out.println("Kết quả gửi cusDTO: "+ result);

        } catch (Exception e) {
            System.out.print("Loi khi xu ly JSON: " + e);
        }
    }
}
