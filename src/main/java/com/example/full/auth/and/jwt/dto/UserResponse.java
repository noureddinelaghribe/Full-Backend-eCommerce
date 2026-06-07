package com.example.full.auth.and.jwt.dto;

import com.example.full.auth.and.jwt.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 📝 DTO لاستجابة بيانات المستخدم (User Response)
 * 
 * الوصف:
 * - يحتوي على بيانات المستخدم بدون كلمة المرور
 * - يستخدم في إرجاع معلومات المستخدم
 * 
 * ملاحظة:
 * - لا يحتوي على password لأسباب أمنية
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {

    private Long id;
    private String email;
    private String fullName;
    private String phoneNumber;
    private Role role;
    private Boolean enabled;
    private LocalDateTime createdAt;
}
