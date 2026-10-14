package com.crm.BackendCrm.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.UniqueConstraint;

@Entity 
@Setter 
@Getter 
@AllArgsConstructor 
@NoArgsConstructor 
@Table (
    name = "facebook_pages",
    uniqueConstraints = {
        @UniqueConstraint(
            columnNames = {"page_id", "company_id"}
        )
    }
)
public class FacebookPage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column (nullable = false, unique = true)
    private String pageId;

    @Column (nullable = false)
    private String pageName;

    @Column (nullable = false)
    private String longPageAccessToken;

    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn (name = "user_id",nullable = false)
    private User user;

    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn (name = "company_id", nullable = false)
    private Company company;
}