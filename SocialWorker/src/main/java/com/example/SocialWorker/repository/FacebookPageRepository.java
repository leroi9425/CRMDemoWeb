package com.example.SocialWorker.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.SocialWorker.entity.FacebookPage;

public interface FacebookPageRepository extends JpaRepository<FacebookPage, Long> {
    Optional<FacebookPage> findByPageId(String pageId);
}
