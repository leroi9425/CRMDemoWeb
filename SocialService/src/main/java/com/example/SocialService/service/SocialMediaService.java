package com.example.SocialService.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;

import com.example.SocialService.entity.SocialMedia;
import com.example.SocialService.repository.SocialMediaRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Service 
@RequiredArgsConstructor 
@EnableScheduling
public class SocialMediaService {
    private final SocialMediaRepository socialMediaRepository;
    private final RabbitTemplate rabbitTemplate;
    public ResponseEntity<?> create(String data){
        System.out.println(data);
        rabbitTemplate.convertAndSend("test-queue", data);
        SocialMedia socialMedia = new SocialMedia();
        socialMedia.setData(data);
        socialMedia.setType("Facebook");
        socialMediaRepository.save(socialMedia);

        return ResponseEntity.ok().build();
    }

    // @Scheduled(fixedDelay = 10000)
    @Transactional
    public void handleData(){
        System.out.println("handleData");
        List<SocialMedia> socials = socialMediaRepository.findTop10ByOrderByIdAsc();
        for (SocialMedia s : socials) {
            rabbitTemplate.convertAndSend("test-queue",s.getData());
        }
        for (SocialMedia socialMedia : socials) {
            socialMediaRepository.delete(socialMedia);
        }
    }
}
