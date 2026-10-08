package com.example.SocialService.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.SocialService.entity.SocialMedia;

public interface SocialMediaRepository extends JpaRepository<SocialMedia, Long> {
    List<SocialMedia> findTop10ByOrderByIdAsc();
}
