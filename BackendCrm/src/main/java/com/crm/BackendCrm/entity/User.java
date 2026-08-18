package com.crm.BackendCrm.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    private String fullName;

    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_role",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private java.util.Set<Role> roles = new java.util.HashSet<>();
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Tạo một cái giỏ trống để đựng quyền
        java.util.Set<GrantedAuthority> authorities = new java.util.HashSet<>();
        
        // Vòng lặp 1: Đi qua từng Nhóm quyền (Role) của User (Ví dụ: Kế toán, Marketing)
        for (Role role : roles) {
            // Vòng lặp 2: Đi qua từng Quyền chi tiết (Permission) nằm trong Nhóm quyền đó
            for (Permission permission : role.getPermissions()) {
                // Nhặt cái tên quyền (Ví dụ "XEM_KH") bỏ vào giỏ
                authorities.add(new SimpleGrantedAuthority(permission.getName()));
            }
        }
        
        // Trả về cái giỏ chứa đầy đủ mọi quyền hạn của User này!
        return authorities;
    }
    @Override
    public boolean isAccountNonExpired() { return true; }
    @Override
    public boolean isAccountNonLocked() { return true; }
    @Override
    public boolean isCredentialsNonExpired() { return true; }
    @Override
    public boolean isEnabled() { return true; }
}
