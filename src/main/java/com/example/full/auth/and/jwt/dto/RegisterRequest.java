package com.example.full.auth.and.jwt.dto;

import com.example.full.auth.and.jwt.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 📝 DTO لطلب التسجيل (Register Request)
 * 
 * الوصف:
 * - يحتوي على البيانات المطلوبة لإنشاء حساب جديد
 * - يتم التحقق من صحة البيانات باستخدام Bean Validation
 * 
 * الحقول:
 * 📧 email: البريد الإلكتروني (إجباري وفريد)
 * 🔑 password: كلمة المرور (إجباري)
 * 👤 fullName: الاسم الكامل (إجباري)
 * 📞 phoneNumber: رقم الهاتف (إجباري)
 * 🎭 role: دور المستخدم (اختياري، افتراضي: USER)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    /**
     * 📧 البريد الإلكتروني
     * - يجب أن يكون بصيغة email صحيحة
     * - لا يمكن أن يكون فارغاً
     */
    @NotBlank(message = "البريد الإلكتروني مطلوب - Email is required")
    @Email(message = "البريد الإلكتروني غير صالح - Invalid email format")
    private String email;

    /**
     * 🔑 كلمة المرور
     * - يجب أن تكون على الأقل 6 أحرف
     * - لا يمكن أن تكون فارغة
     */
    @NotBlank(message = "كلمة المرور مطلوبة - Password is required")
    @Size(min = 6, message = "كلمة المرور يجب أن تكون 6 أحرف على الأقل - Password must be at least 6 characters")
    private String password;

    /**
     * 👤 الاسم الكامل
     * - لا يمكن أن يكون فارغاً
     */
    @NotBlank(message = "الاسم الكامل مطلوب - Full name is required")
    private String fullName;

    /**
     * 📞 رقم الهاتف
     * - لا يمكن أن يكون فارغاً
     */
    @NotBlank(message = "رقم الهاتف مطلوب - Phone number is required")
    private String phoneNumber;

    /**
     * 🎭 دور المستخدم
     * - اختياري، القيمة الافتراضية: USER
     */
    private Role role = Role.ROLE_BUYER;
}
