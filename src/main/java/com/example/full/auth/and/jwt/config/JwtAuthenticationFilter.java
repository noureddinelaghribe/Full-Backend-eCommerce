package com.example.full.auth.and.jwt.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * 🔒 JWT Authentication Filter - فلتر المصادقة باستخدام JWT
 * 
 * الوصف:
 * - يعترض جميع الطلبات الواردة
 * - يتحقق من وجود JWT Token في Header
 * - يتحقق من صحة Token
 * - يضع بيانات المستخدم في SecurityContext
 * 
 * الآلية:
 * 1. استخراج Token من Authorization Header
 * 2. التحقق من صحة Token
 * 3. تحميل بيانات المستخدم
 * 4. إضافة المستخدم إلى SecurityContext
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    /**
     * 🔍 معالجة كل طلب وارد
     * 
     * @param request الطلب الوارد
     * @param response الاستجابة
     * @param filterChain سلسلة الفلاتر
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        
        // 📋 استخراج Authorization Header
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        // ❌ إذا لم يكن هناك token أو لا يبدأ بـ "Bearer "
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 🎫 استخراج Token من Header
        jwt = authHeader.substring(7); // إزالة "Bearer " من البداية
        
        try {
            // 📧 استخراج البريد الإلكتروني من Token
            userEmail = jwtUtil.extractUsername(jwt);

            // ✅ إذا كان البريد موجود والمستخدم غير مصادق بعد
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                
                // 👤 تحميل بيانات المستخدم من قاعدة البيانات
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

                // 🔐 التحقق من صحة Token
                if (jwtUtil.validateToken(jwt, userDetails)) {
                    
                    // 🎯 إنشاء Authentication Token
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    
                    // 📝 إضافة تفاصيل الطلب
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    
                    // 💾 حفظ المصادقة في SecurityContext
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            // 🚨 في حالة وجود خطأ في Token (منتهي أو غير صالح)
            logger.error("JWT Token validation failed: " + e.getMessage());
        }

        // ⏩ الانتقال إلى الفلتر التالي
        filterChain.doFilter(request, response);
    }
}
