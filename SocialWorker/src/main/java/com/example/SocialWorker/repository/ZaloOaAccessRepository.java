package com.example.SocialWorker.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.SocialWorker.entity.ZaloOaAccess;
import java.util.List;


public interface ZaloOaAccessRepository extends JpaRepository<ZaloOaAccess, Long> {
    Optional<ZaloOaAccess> findByOaId(String oaId);
    List<ZaloOaAccess> findAllByOaId(String oaId);
}