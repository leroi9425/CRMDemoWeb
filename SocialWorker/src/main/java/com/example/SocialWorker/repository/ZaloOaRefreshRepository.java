package com.example.SocialWorker.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.SocialWorker.entity.ZaloOaRefresh;

public interface ZaloOaRefreshRepository extends JpaRepository<ZaloOaRefresh,Long> {
    Optional<ZaloOaRefresh> findByOaId(String oaId);
    List<ZaloOaRefresh> findAllByOaId(String oaId);
}
