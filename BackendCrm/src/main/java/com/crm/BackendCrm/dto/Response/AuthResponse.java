package com.crm.BackendCrm.dto.Response;

public record AuthResponse(String token, String username, String role, java.util.List<String> permissions) {
}
