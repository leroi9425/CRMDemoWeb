package com.crm.BackendCrm.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7); // cắt 7 ký tự đầu tiên của chuỗi "Bearer " để lấy token
        username = jwtUtils.extractUsername(jwt);  // lấy username từ token

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // KHÔNG GỌI DATABASE NỮA! Lấy Session vòng trong
            jakarta.servlet.http.HttpSession session = request.getSession(false);
            if (session != null) {
                @SuppressWarnings("unchecked")
                java.util.List<String> permissions = (java.util.List<String>) session.getAttribute("permissions");
                
                if (permissions != null) {
                    // biển permission thành các mảng thẩm quyền của Spring Security
                    java.util.List<org.springframework.security.core.GrantedAuthority> authorities = permissions.stream()
                            .map(org.springframework.security.core.authority.SimpleGrantedAuthority::new)
                            .collect(java.util.stream.Collectors.toList());

                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            username,
                            null,
                            authorities
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        }
        filterChain.doFilter(request, response);
    }
}
