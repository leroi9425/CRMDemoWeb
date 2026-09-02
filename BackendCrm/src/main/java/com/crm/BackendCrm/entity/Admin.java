package com.crm.BackendCrm.entity;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "admins")
@NoArgsConstructor
@AllArgsConstructor
public class Admin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String userName;

    @Column(nullable = false)
    private String passWord;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "admin_role",
        joinColumns = @JoinColumn(name = "admin_id"),
        inverseJoinColumns = @JoinColumn(name = "fole_id")
    )
    private Set<Role> roles = new HashSet<>();

    @Column(nullable = false)
    LocalDateTime createAt = LocalDateTime.now();
}
