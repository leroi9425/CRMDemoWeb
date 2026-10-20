package com.example.SocialWorker.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "zalo_oa_refresh")
public class ZaloOaRefresh {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    Long userId;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String refreshToken;

    @Column
    String oaId;

    @Column
    LocalDateTime createdAt;
}
