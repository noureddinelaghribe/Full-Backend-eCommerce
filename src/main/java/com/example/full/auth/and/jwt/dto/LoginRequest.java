package com.example.full.auth.and.jwt.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 📝 DTO لطلب تسجيل الدخول (Login Request)
 * 
 * الوصف:
 * - يحتوي على بيانات تسجيل الدخول
 * - يتم التحقق من صحة البيانات
 * 
 * الحقول:
 * 📧 email: البريد الإلكتروني
 * 🔑 password: كلمة المرور
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    /**
     * 📧 البريد الإلكتروني
     * - يجب أن يكون بصيغة email صحيحة
     */
    @NotBlank(message = "البريد الإلكتروني مطلوب - Email is required")
    @Email(message = "البريد الإلكتروني غير صالح - Invalid email format")
    private String email;

    /**
     * 🔑 كلمة المرور
     * - لا يمكن أن تكون فارغة
     */
    @NotBlank(message = "كلمة المرور مطلوبة - Password is required")
    private String password;
}
