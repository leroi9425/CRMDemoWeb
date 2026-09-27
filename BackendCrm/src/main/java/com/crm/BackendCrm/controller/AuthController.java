package com.crm.BackendCrm.controller;

import com.crm.BackendCrm.dto.Request.AuthRequest;
import com.crm.BackendCrm.dto.Response.AuthResponse;
import com.crm.BackendCrm.entity.User;
import com.crm.BackendCrm.repository.UserRepository;
import com.crm.BackendCrm.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.stream.Collectors;
import org.springframework.security.core.GrantedAuthority;
import com.crm.BackendCrm.entity.Role;

import java.util.List;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:81"})
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;
//     private final com.crm.BackendCrm.security.ActiveSessionManager activeSessionManager;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @RequestBody AuthRequest request
    ) {
        
        System.out.println("Bắt đầu vào hàm login do api /login");
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );
        System.out.println("chạy qua Authentication authentication =");
        // Đăng ký user vào luồng chạy hiện tại
        SecurityContextHolder.getContext().setAuthentication(authentication);
        System.out.println("chạy qua Security getcontent =");

        final UserDetails userDetails = userDetailsService.loadUserByUsername(request.username());
        System.out.println("chạy qua final UserDetails userDetails =");
        final String jwt = jwtUtils.generateToken(userDetails);
        System.out.println("chạy qua final String jwt = jwtUtils");
        
        List<String> permissions = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        
        System.out.println(" List<String> permissions =");
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        System.out.println("User user =");

        String rolesStr = user.getRoles().stream().map(Role::getName).collect(Collectors.joining(","));
        System.out.println(" String rolesStr = user");
        
        // TRẢ VỀ JSON CÓ CẢ TOKEN VÀ MẢNG PERMISSIONS CHO REACT
        return ResponseEntity.ok(new AuthResponse(jwt, user.getUsername(), rolesStr, permissions));
    }

    @GetMapping("/me/permissions")
    public ResponseEntity<?> getMyPermissions(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ") || authHeader.equals("Bearer null")) {
                return ResponseEntity.status(401).build();
        }
        try {
                String jwt = authHeader.substring(7);
                String username = jwtUtils.extractUsername(jwt);
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                List<String> permissions = userDetails.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList());
                return ResponseEntity.ok(permissions);
        } 
        catch (Exception e) {
                return ResponseEntity.status(401).build();
        }
    }
}
