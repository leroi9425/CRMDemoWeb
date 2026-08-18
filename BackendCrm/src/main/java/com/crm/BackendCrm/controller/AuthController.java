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

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @RequestBody AuthRequest request, 
            jakarta.servlet.http.HttpServletRequest httpRequest
    ) {
        org.springframework.security.core.Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );
        
        // Đăng ký user vào luồng chạy hiện tại
        org.springframework.security.core.context.SecurityContextHolder.getContext().setAuthentication(authentication);

        final UserDetails userDetails = userDetailsService.loadUserByUsername(request.username());
        final String jwt = jwtUtils.generateToken(userDetails);
        
        // TRÍCH XUẤT 500 PERMISSIONS TỪ USER
        java.util.List<String> permissions = userDetails.getAuthorities().stream()
                .map(org.springframework.security.core.GrantedAuthority::getAuthority)
                .collect(java.util.stream.Collectors.toList());
                
        // TẠO SESSION VÀ CẤT PERMISSIONS VÀO ĐÓ
        jakarta.servlet.http.HttpSession session = httpRequest.getSession(true);
        session.setAttribute("permissions", permissions);
        
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        String rolesStr = user.getRoles().stream().map(com.crm.BackendCrm.entity.Role::getName).collect(java.util.stream.Collectors.joining(","));
        
        // TRẢ VỀ JSON CÓ CẢ TOKEN VÀ MẢNG PERMISSIONS CHO REACT
        return ResponseEntity.ok(new AuthResponse(jwt, user.getUsername(), rolesStr, permissions));
    }
}
