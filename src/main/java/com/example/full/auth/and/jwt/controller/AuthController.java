package com.example.full.auth.and.jwt.controller;

import com.example.full.auth.and.jwt.dto.AuthResponse;
import com.example.full.auth.and.jwt.dto.LoginRequest;
import com.example.full.auth.and.jwt.dto.RegisterRequest;
import com.example.full.auth.and.jwt.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 🔐 AuthController - واجهة REST للمصادقة (تسجيل الدخول والتسجيل)
 *
 * الوصف:
 * - توفر نقطتي نهاية أساسيتين للمستخدمين:
 *   - /api/auth/login    → تسجيل الدخول وإرجاع JWT
 *   - /api/auth/register → تسجيل مستخدم جديد وإرجاع JWT
 * - تعتمد على {@link com.example.full.auth.and.jwt.service.AuthService} لتنفيذ منطق الأعمال
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // User login endpoint

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    // User registration endpoint
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.ok(response);
    }
}