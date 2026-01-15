package com.example.full.auth.and.jwt.service;

import com.example.full.auth.and.jwt.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * 🔐 Custom UserDetailsService - خدمة تحميل بيانات المستخدم
 * 
 * الوصف:
 * - يستخدمها Spring Security للتحقق من المستخدمين
 * - يحمل بيانات المستخدم من قاعدة البيانات
 * - يستخدم في عملية المصادقة
 * 
 * الوظيفة:
 * - loadUserByUsername(): تحميل المستخدم بواسطة البريد الإلكتروني
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * 👤 تحميل بيانات المستخدم بواسطة البريد الإلكتروني
     * 
     * @param email البريد الإلكتروني (username)
     * @return بيانات المستخدم
     * @throws UsernameNotFoundException إذا لم يتم العثور على المستخدم
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.debug("🔍 Loading user by email: {}", email);
        return userRepository.findByEmail(email)
                .map(user -> {
                    log.info("✅ User found - Email: {}, Enabled: {}, Role: {}", 
                            user.getEmail(), user.getEnabled(), user.getRole());
                    log.debug("🔐 Password hash starts with: {}", 
                            user.getPassword().substring(0, Math.min(20, user.getPassword().length())));
                    return user;
                })
                .orElseThrow(() -> {
                    log.error("❌ User not found with email: {}", email);
                    return new UsernameNotFoundException(
                            "المستخدم غير موجود - User not found with email: " + email
                    );
                });
    }
}
