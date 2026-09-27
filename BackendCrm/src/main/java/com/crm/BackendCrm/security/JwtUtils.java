package com.crm.BackendCrm.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import com.crm.BackendCrm.entity.User;

import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtils {

    // Should be injected from application.properties in a real app
    private final String SECRET_KEY = "super_secret_key_crm_backend_lite_app_which_is_long_enough";
    private final long JWT_EXPIRATION = 900000; // 15 phut (900000 ms)

    private Key getSignInKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    public String extractUsername(String token) {
        System.out.println("extractUsername(String token)"+token);
        return extractClaim(token, Claims::getSubject);
    }

    public String extractRole(String token) {
        System.out.println("extractRole(String token)"+token);
        return extractClaim(token, claims -> claims.get("role", String.class));
    }

    public Long extractUserId(String token) {
        System.out.println("extractUserId(String token)"+token);
        return extractClaim(token, claims -> claims.get("userId", Long.class));
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        System.out.println("public <T> T extractClaim(String token, Function<Claims, T> claimsResolver)"+token);
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        System.out.println("extractAllClaims(String token)"+token);
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String generateToken(UserDetails userDetails) {
        System.out.println("Tạo token mới tuwf userDetails");
        java.util.Map<String, Object> extraClaims = new java.util.HashMap<>();
        
        // Chỉ lưu Role vào JWT, không lưu Permissions
        if (userDetails instanceof com.crm.BackendCrm.entity.User) {
            User user = (User) userDetails;
            String roleName = user.getRoles().stream()
                    .findFirst()
                    .map(com.crm.BackendCrm.entity.Role::getName)
                    .orElse("USER");
            // 3. CHỈ NHÉT ĐÚNG CÁI TÊN ROLE ĐÓ VÀO JWT (Tuyệt đối không nhét Permission nữa)
            extraClaims.put("role", roleName);
            // cho thêm userId vào JWT để tiện cho việc lấy thông tin người dùng
            extraClaims.put("userId", user.getId());
        }
        
        System.out.println("đặt key vào token đã xong");
        // 4. Sinh ra Token mỏng nhẹ
        return generateToken(extraClaims, userDetails);
    }
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        System.out.println("Tạo token mới");
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
