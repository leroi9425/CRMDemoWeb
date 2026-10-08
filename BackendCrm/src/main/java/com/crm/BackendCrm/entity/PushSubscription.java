package com.crm.BackendCrm.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "push_subscription")
@NoArgsConstructor 
@AllArgsConstructor
@Getter 
@Setter 
public class PushSubscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(columnDefinition = "TEXT")
    private String endpoint;

    @Column(columnDefinition = "TEXT")
    private String p256dh;

    @Column(columnDefinition = "TEXT")
    private String auth;

    // public void setUser(User user2) {
    //     throw new UnsupportedOperationException("Unimplemented method 'setUser'");
    // }
}