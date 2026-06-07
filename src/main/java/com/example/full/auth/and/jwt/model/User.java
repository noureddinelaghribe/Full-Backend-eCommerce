package com.example.full.auth.and.jwt.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * 👥 نموذج بيانات المستخدم (Entity)
 * 
 * الوصف:
 * - يمثل جدول "users" في قاعدة البيانات
 * - يحتوي على جميع بيانات المستخدم
 * - يستخدم JPA للتعامل مع الداتابيز
 * 
 * الحقول:
 * 🆔 id: المعرف الفريد للمستخدم (Primary Key)
 * 📧 email: البريد الإلكتروني (فريد وإجباري)
 * 🔑 password: كلمة المرور (مشفرة)
 * 👤 fullName: الاسم الكامل (إجباري)
 * 📞 phoneNumber: رقم الهاتف (إجباري)
 * 🎭 role: دور المستخدم (مثل: ADMIN, USER)
 * ✅ enabled: هل الحساب مفعل؟
 * 📅 createdAt: تاريخ إنشاء الحساب
 * 
 * الأنوتيشنز المستخدمة:
 * 📊 @Entity: يخبر JPA أن هذا كلاس Entity
 * 📋 @Table: يحدد اسم الجدول في الداتابيز
 * 📦 @Data: ينشئ Getters/Setters/toString تلقائياً (Lombok)
 * 🏭 @NoArgsConstructor: ينشئ Constructor فارغ (Lombok)
 * 🏭 @AllArgsConstructor: ينشئ Constructor بجميع المعاملات (Lombok)
 * 
 * @author فريق التطوير
 * @version 1.0
 */
@Entity // 📊 يخبر Spring Data JPA أن هذا كلاس يمثل جدول في الداتابيز
@Table(name = "users") // 📋 اسم الجدول في قاعدة البيانات
@Data // 🔥 من Lombok: ينشئ getters / setters / toString / equals / hashCode تلقائياً
@NoArgsConstructor // 🏭 ينشئ Constructor فارغ ()
@AllArgsConstructor // 🏭 ينشئ Constructor بجميع المعاملات
public class User implements UserDetails {

    /**
     * 🆔 المعرف الفريد للمستخدم (Primary Key)
     * 
     * الوصف:
     * - يتم توليده تلقائياً بواسطة الداتابيز
     * - يزداد تلقائياً مع كل مستخدم جديد (Auto Increment)
     * - لا يمكن أن يكون null
     */
    @Id // 🆔 يحدد هذا الحقل كـ Primary Key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 🔢 توليد تلقائي للـ ID
    private Long id;

    /**
     * 📧 البريد الإلكتروني
     * 
     * القيود:
     * - يجب أن يكون فريداً (unique)
     * - لا يمكن أن يكون null (nullable = false)
     * - يستخدم لتسجيل الدخول
     */
    @Column(nullable = false, unique = true) // 🛡️ إجباري وفريد
    private String email;

    /**
     * 🔑 كلمة المرور
     * 
     * ملاحظات:
     * - يجب تشفيرها بواسطة BCrypt قبل الحفظ
     * - لا تحفظ كلمة المرور بدون تشفير!
     * - لا ترجع هذا الحقل في API Responses
     */
    private String password;

    /**
     * 👤 الاسم الكامل للمستخدم
     * 
     * القيود:
     * - لا يمكن أن يكون null (nullable = false)
     * - يجب إدخاله عند التسجيل
     */
    @Column(nullable = false) // 🛡️ حقل إجباري
    private String fullName;

    /**
     * 📞 رقم الهاتف
     * 
     * القيود:
     * - لا يمكن أن يكون null (nullable = false)
     * - يستخدم للتواصل أو التحقق
     */
    @Column(nullable = false) // 🛡️ حقل إجباري
    private String phoneNumber;

    /**
     * 🎭 دور المستخدم (Role)
     * 
     * القيم الممكنة:
     * - "USER": مستخدم عادي
     * - "ADMIN": مدير النظام
     * - "MODERATOR": مشرف
     * 
     * ملاحظة: يمكن استخدام Enum بدلاً من String
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    /**
     * ✅ حالة تفعيل الحساب
     * 
     * الوصف:
     * - true: الحساب مفعل ويمكن تسجيل الدخول
     * - false: الحساب معطل (محظور أو بانتظار التفعيل)
     * - القيمة الافتراضية: true
     */
    @Column(nullable = false)
    private Boolean enabled = true;

    /**
     * 📅 تاريخ ووقت إنشاء الحساب
     * 
     * الاستخدام:
     * - يتم تعيينه تلقائياً عند إنشاء المستخدم
     * - يستخدم للتتبع والتقارير
     * - نوع LocalDateTime من Java 8+
     */
    private java.time.LocalDateTime createdAt;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled != null && enabled;
    }
}
