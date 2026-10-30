package com.crm.BackendCrm.entity;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "permissions")
// @Data
@Getter 
@Setter 
@NoArgsConstructor
@AllArgsConstructor
public class Permission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column
    private String description;

    @Column(nullable = true)    
    private Long parentId;

    @Column(nullable = false)
    private String type; // đánh dấu đâu là quyền đâu là tên đâu là module

    @Column(nullable = false)
    private String category;

    @ManyToMany(mappedBy = "permissions")
    private Set<Role> roles = new HashSet<>();
}