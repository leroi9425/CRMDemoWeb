package com.example.SocialWorker.entity;

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
@NoArgsConstructor
@Getter
@Setter
@Table(name = "zalo_oa_access")
public class ZaloOaAccess {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    Long userId;

    @Column
    Long companyId;

    @Column(columnDefinition = "TEXT", nullable = false, unique = true)
    private String accessToken;

    @Column
    String oaId;

    @Column
    Long expiresIn;
}
