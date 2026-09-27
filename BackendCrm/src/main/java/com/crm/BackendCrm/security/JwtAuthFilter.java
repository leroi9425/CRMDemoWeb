package com.crm.BackendCrm.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.core.GrantedAuthority;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final com.crm.BackendCrm.service.RoleService roleService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        System.out.println("Bắt đầu vào hàm dòilterInternal");
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;
        System.out.println("chạy qua authHeader =" + authHeader);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        System.out.println("auth header ko null: " + authHeader);
        jwt = authHeader.substring(7); // cắt 7 ký tự đầu tiên của chuỗi "Bearer " để lấy token
        System.out.println("Chayj tiếp đến jwt: " + authHeader);
        try {
            username = jwtUtils.extractUsername(jwt);  // lấy username từ token
        } catch (Exception e) {
            filterChain.doFilter(request, response);
            return;
        }
        System.out.println("Username từ jwt: " + username);
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            
            // 1. Lôi tên Role từ Token ra
            String roleName = jwtUtils.extractRole(jwt);
            System.out.println("role từ jwt: " + roleName);
            
            if (roleName != null) {
                // 2. Chọc xuống Redis lấy mảng quyền (Siêu tốc độ)
                java.util.List<String> permissions = roleService.getPermissionNamesByRoleName(roleName);
                System.out.println("permission = " + permissions);
                // for (String perm : permissions) {
                //     System.out.println("Permission for role " + roleName + ": " + perm);
                // }
                // 3. Biến thành mảng quyền của Spring Security
                java.util.List<GrantedAuthority> authorities = permissions.stream()
                        .map(org.springframework.security.core.authority.SimpleGrantedAuthority::new)
                        .collect(java.util.stream.Collectors.toList());
                System.out.println("chạy qua java.util.List<GrantedAuthority> authorities = permissions.stream() ");
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        username,
                        null,
                        authorities
                );
                System.out.println("UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(");
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                System.out.println("authToken.setDetails");
                SecurityContextHolder.getContext().setAuthentication(authToken);
                System.out.println("SecurityContextHolder.getContext().setAuthentication(authToken);");
            }
        }
        filterChain.doFilter(request, response);
        System.out.println("filterChain.doFilter(request, response);");
    }
}
