package com.crm.BackendCrm.security;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.List;


public class ActiveSessionManager {
    
    // Cu?n s? c�i: Ghi nh? Username dang g?n v?i K�t s?t (Session) n�o
    private final Map<String, HttpSession> activeSessions = new ConcurrentHashMap<>();

    // 1. C?t v�o s? (G?i l�c User �ang nh?p)
    public void registerSession(String username, HttpSession session) {
        activeSessions.put(username, session);
    }

    // 2. Ph� k�t s?t (G?i l�c Admin gi�ng ch?c/du?i vi?c)
    public void kickUser(String username) {
        HttpSession session = activeSessions.get(username);
        if (session != null) {
            try {
                // B?m n�t t? h?y K�t s?t trong RAM
                session.invalidate(); 
            } catch (Exception e) {
                // B? qua n?u Session d� t? h?t h?n t? tru?c
            }
            // G?ch t�n User kh?i s?
            activeSessions.remove(username);
        }
    }

    // 3. Tr�o d?i danh s�ch quy?n trong RAM (M?m m?ng - d�ng khi d?i quy?n c�ng ty)
    public void updatePermissions(String username, List<String> newPermissions) {
        HttpSession session = activeSessions.get(username);
        if (session != null) {
            try {
                // Ch�p d� t? danh s�ch quy?n m?i v�o trong K�t s?t
                session.setAttribute("permissions", newPermissions);
            } catch (Exception e) {
                // N?u l? Session t? h?t h?n r?i th� x�a t�n kh?i s?
                activeSessions.remove(username);
            }
        }
    }
}
