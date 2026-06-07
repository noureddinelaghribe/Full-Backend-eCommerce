package com.example.full.auth.and.jwt.dto;

import com.example.full.auth.and.jwt.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 📝 DTO لاستجابة المصادقة (Authentication Response)
 * 
 * الوصف:
 * - يحتوي على JWT Token وبيانات المستخدم
 * - يتم إرجاعه بعد تسجيل الدخول أو التسجيل بنجاح
 * 
 * الحقول:
 * 🎫 token: JWT Token للمصادقة
 * 🆔 id: معرف المستخدم
 * 📧 email: البريد الإلكتروني
 * 👤 fullName: الاسم الكامل
 * 🎭 role: دور المستخدم
 * 💬 message: رسالة للمستخدم
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {

    /**
     * 🎫 JWT Token
     * - يستخدم في جميع الطلبات المستقبلية للمصادقة
     */
    private String token;

    /**
     * 🆔 معرف المستخدم
     */
    private Long id;

    /**
     * 📧 البريد الإلكتروني
     */
    private String email;

    /**
     * 👤 الاسم الكامل
     */
    private String fullName;

    /**
     * 🎭 دور المستخدم
     */
    private Role role;

    /**
     * 💬 رسالة للمستخدم
     */
    private String message;
}
