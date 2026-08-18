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
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );
        
        final UserDetails userDetails = userDetailsService.loadUserByUsername(request.username());
        final String jwt = jwtUtils.generateToken(userDetails);
        
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        String rolesStr = user.getRoles().stream().map(com.crm.BackendCrm.entity.Role::getName).collect(java.util.stream.Collectors.joining(","));
        return ResponseEntity.ok(new AuthResponse(jwt, user.getUsername(), rolesStr));
    }
}
