package com.example.full.auth.and.jwt.dto;

import com.example.full.auth.and.jwt.model.Role;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 📝 DTO لطلب تحديث بيانات المستخدم (Update User Request)
 * 
 * الوصف:
 * - يحتوي على البيانات التي يمكن تحديثها
 * - جميع الحقول اختيارية (يتم تحديث ما يتم إرساله فقط)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {

    /**
     * 📧 البريد الإلكتروني (اختياري)
     */
    @Email(message = "البريد الإلكتروني غير صالح - Invalid email format")
    private String email;

    /**
     * 👤 الاسم الكامل (اختياري)
     */
    private String fullName;

    /**
     * 📞 رقم الهاتف (اختياري)
     */
    private String phoneNumber;

    /**
     * 🎭 دور المستخدم (اختياري)
     */
    private Role role;

    /**
     * ✅ حالة التفعيل (اختياري)
     */
    private Boolean enabled;
}
