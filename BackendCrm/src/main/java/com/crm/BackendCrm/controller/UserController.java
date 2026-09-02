package com.crm.BackendCrm.controller;

import com.crm.BackendCrm.dto.Request.UserRequestDTO;
import com.crm.BackendCrm.dto.Response.UserResponseDTO;
import com.crm.BackendCrm.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import com.crm.BackendCrm.security.JwtUtils;


@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class UserController {
    private final UserService userService;
    private final JwtUtils jwtUtils;

    @GetMapping
    @PreAuthorize("hasAuthority('QUAN_LY_USER')")
    public List<UserResponseDTO> getAll() {
        return userService.getAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('QUAN_LY_USER')")
    public UserResponseDTO getById(@PathVariable Long id) {
        return userService.getById(id);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('QUAN_LY_USER')")
    public ResponseEntity<UserResponseDTO> create(@Valid @RequestBody UserRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.create(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('QUAN_LY_USER')")
    public UserResponseDTO update(@PathVariable Long id, @Valid @RequestBody UserRequestDTO dto) {
        return userService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('QUAN_LY_USER')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me/company")
    public List<UserResponseDTO> getMyCompany(@RequestHeader("Authorization") String authHeader) {
        String jwt = authHeader.substring(7);
        String username = jwtUtils.extractUsername(jwt);

        UserResponseDTO userResponseDTO = userService.getByName(username);

        return userService.getAllUserInCompany(userResponseDTO.company_id());
    }
    
}
