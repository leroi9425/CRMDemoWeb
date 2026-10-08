package com.crm.BackendCrm.service;

import com.crm.BackendCrm.dto.Request.UserRequestDTO;
import com.crm.BackendCrm.dto.Response.UserResponseDTO;
import com.crm.BackendCrm.entity.User;
import com.crm.BackendCrm.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public List<UserResponseDTO> getAll() {
        return userRepository.findAll().stream().map(this::toDTO).toList();
    }

    public UserResponseDTO getById(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        return toDTO(user);
    }

    public UserResponseDTO getByName(String userName){
        User user = userRepository.findByUsername(userName).
        orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        return toDTO(user);
    }

    public UserResponseDTO create(UserRequestDTO dto) {
        if (userRepository.existsByUsername(dto.username()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username exists");
        User user = new User();
        user.setUsername(dto.username());
        user.setEmail(dto.email());
        user.setPassword(passwordEncoder.encode(dto.password()));
        user.setFullName(dto.fullName());
        return toDTO(userRepository.save(user));
    }

    public UserResponseDTO update(Long id, UserRequestDTO dto) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        user.setEmail(dto.email());
        user.setFullName(dto.fullName());
        if (dto.password() != null && !dto.password().isBlank())
            user.setPassword(passwordEncoder.encode(dto.password()));
        return toDTO(userRepository.save(user));
    }

    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    public List<UserResponseDTO> getAllUserInCompany(Long userId){
        User user = userRepository.findById(userId).
        orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "user not found"));

        Long companyId = user.getCompany().getId();

        return userRepository.findByCompanyId(companyId).stream().map(this::toDTO).collect(Collectors.toList());
    }

    private UserResponseDTO toDTO(User u) {
        String rolesStr = u.getRoles().stream().map(com.crm.BackendCrm.entity.Role::getName).collect(java.util.stream.Collectors.joining(","));
        return new UserResponseDTO(
            u.getId(), 
            u.getUsername(), 
            u.getEmail(), 
            u.getFullName(), 
            rolesStr, 
            u.getCreatedAt(), 
            u.getCompany().getId()
        );
    }
}
