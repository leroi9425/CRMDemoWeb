package com.crm.BackendCrm.dto;

public record AuthResponse(String token, String username, String role, java.util.List<String> permissions) {
}
