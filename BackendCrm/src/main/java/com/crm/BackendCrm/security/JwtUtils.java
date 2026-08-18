package com.crm.BackendCrm.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtils {

    // Should be injected from application.properties in a real app
    private final String SECRET_KEY = "super_secret_key_crm_backend_lite_app_which_is_long_enough";
    private final long JWT_EXPIRATION = 86400000; // 1 day in ms

    private Key getSignInKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String generateToken(UserDetails userDetails) {
        java.util.Map<String, Object> extraClaims = new java.util.HashMap<>();
        
        // Chỉ lưu Role vào JWT, không lưu Permissions
        if (userDetails instanceof com.crm.BackendCrm.entity.User) {
            com.crm.BackendCrm.entity.User user = (com.crm.BackendCrm.entity.User) userDetails;
            String roleName = user.getRoles().stream()
                    .findFirst()
                    .map(com.crm.BackendCrm.entity.Role::getName)
                    .orElse("USER");
            // 3. CHỈ NHÉT ĐÚNG CÁI TÊN ROLE ĐÓ VÀO JWT (Tuyệt đối không nhét Permission nữa)
            extraClaims.put("role", roleName);
        }
        
        // 4. Sinh ra Token mỏng nhẹ
        return generateToken(extraClaims, userDetails);
    }
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
}
