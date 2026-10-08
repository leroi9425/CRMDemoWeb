package com.crm.BackendCrm.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity 
@Setter 
@Getter 
@AllArgsConstructor 
@NoArgsConstructor 
@Table (name = "facebook_pages")
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
}
