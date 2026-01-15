package com.example.full.auth.and.jwt.service;

import com.example.full.auth.and.jwt.config.JwtUtil;
import com.example.full.auth.and.jwt.dto.AuthResponse;
import com.example.full.auth.and.jwt.dto.LoginRequest;
import com.example.full.auth.and.jwt.dto.RegisterRequest;
import com.example.full.auth.and.jwt.model.User;
import com.example.full.auth.and.jwt.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 🔐 Authentication Service - خدمة المصادقة
 * 
 * الوصف:
 * - إدارة عمليات التسجيل وتسجيل الدخول
 * - توليد JWT Tokens
 * - التحقق من البيانات
 * 
 * العمليات:
 * ✅ register(): تسجيل مستخدم جديد
 * ✅ login(): تسجيل الدخول
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    /**
     * 📝 تسجيل مستخدم جديد
     * 
     * الخطوات:
     * 1. التحقق من عدم وجود البريد مسبقاً
     * 2. تشفير كلمة المرور
     * 3. حفظ المستخدم في قاعدة البيانات
     * 4. توليد JWT Token
     * 5. إرجاع الاستجابة
     * 
     * @param request بيانات التسجيل
     * @return AuthResponse يحتوي على Token وبيانات المستخدم
     */
    public AuthResponse register(RegisterRequest request) {
        log.info("🔵 Starting registration for email: {}", request.getEmail());
        
        // 🔍 التحقق من عدم وجود المستخدم مسبقاً
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            log.warn("⚠️ Registration failed - Email already exists: {}", request.getEmail());
            throw new RuntimeException("البريد الإلكتروني مستخدم بالفعل - Email already exists");
        }

        // 🏗️ إنشاء كائن المستخدم الجديد
        User user = new User();
        user.setEmail(request.getEmail());
        String rawPassword = request.getPassword();
        String encodedPassword = passwordEncoder.encode(rawPassword);
        log.info("🔐 Password encoding - Raw length: {}, Encoded: {}", rawPassword.length(), encodedPassword.substring(0, 20) + "...");
        user.setPassword(encodedPassword); // 🔐 تشفير كلمة المرور
        user.setFullName(request.getFullName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setRole(request.getRole() != null ? request.getRole() : "USER");
        user.setEnabled(true);
        user.setCreatedAt(LocalDateTime.now());

        // 💾 حفظ المستخدم في قاعدة البيانات
        User savedUser = userRepository.save(user);
        log.info("✅ User registered successfully with ID: {}", savedUser.getId());

        // 🎫 توليد JWT Token
        String token = jwtUtil.generateToken(savedUser);

        // 📤 إرجاع الاستجابة
        return AuthResponse.builder()
                .token(token)
                .id(savedUser.getId())
                .email(savedUser.getEmail())
                .fullName(savedUser.getFullName())
                .role(savedUser.getRole())
                .message("تم التسجيل بنجاح - Registration successful")
                .build();
    }

    /**
     * 🔑 تسجيل الدخول
     * 
     * الخطوات:
     * 1. التحقق من البريد وكلمة المرور
     * 2. المصادقة باستخدام AuthenticationManager
     * 3. توليد JWT Token
     * 4. إرجاع الاستجابة
     * 
     * @param request بيانات تسجيل الدخول
     * @return AuthResponse يحتوي على Token وبيانات المستخدم
     */
    public AuthResponse login(LoginRequest request) {
        log.info("🔵 Login attempt for email: {}", request.getEmail());
        
        try {
            // 🔐 المصادقة (التحقق من البريد وكلمة المرور)
            log.debug("🔍 Attempting authentication...");
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

            log.info("✅ Authentication successful for: {}", request.getEmail());
            
            // 👤 الحصول على بيانات المستخدم
            User user = (User) authentication.getPrincipal();
            log.debug("👤 User details retrieved - ID: {}, Role: {}", user.getId(), user.getRole());

            // 🎫 توليد JWT Token
            String token = jwtUtil.generateToken(user);
            log.info("🎫 JWT Token generated for user: {}", user.getEmail());

            // 📤 إرجاع الاستجابة
            return AuthResponse.builder()
                    .token(token)
                    .id(user.getId())
                    .email(user.getEmail())
                    .fullName(user.getFullName())
                    .role(user.getRole())
                    .message("تم تسجيل الدخول بنجاح - Login successful")
                    .build();
        } catch (Exception e) {
            log.error("❌ Login failed for email: {} - Error: {}", request.getEmail(), e.getMessage());
            throw e;
        }
    }
}
