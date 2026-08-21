package com.crm.BackendCrm.controller;

import com.crm.BackendCrm.dto.AuthRequest;
import com.crm.BackendCrm.dto.AuthResponse;
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

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.stream.Collectors;
import org.springframework.security.core.GrantedAuthority;
import com.crm.BackendCrm.entity.Role;

import java.util.List;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
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
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );
        
        // Đăng ký user vào luồng chạy hiện tại
        SecurityContextHolder.getContext().setAuthentication(authentication);

        final UserDetails userDetails = userDetailsService.loadUserByUsername(request.username());
        final String jwt = jwtUtils.generateToken(userDetails);
        
        List<String> permissions = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        String rolesStr = user.getRoles().stream().map(Role::getName).collect(Collectors.joining(","));
        
        // TRẢ VỀ JSON CÓ CẢ TOKEN VÀ MẢNG PERMISSIONS CHO REACT
        return ResponseEntity.ok(new AuthResponse(jwt, user.getUsername(), rolesStr, permissions));
    }

    @GetMapping("/me/permissions")
    public ResponseEntity<List<String>> getMyPermissions(@RequestHeader("Authorization") String authHeader) {
        String jwt = authHeader.substring(7);
        String username = jwtUtils.extractUsername(jwt);
        
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        List<String> permissions = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
                
        return ResponseEntity.ok(permissions);
    }
}
