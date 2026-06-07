package com.example.full.auth.and.jwt.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * 🔐 JWT Utility Class - أداة التعامل مع JWT Tokens
 * 
 * الوصف:
 * - توليد JWT Tokens جديدة
 * - التحقق من صحة JWT Tokens
 * - استخراج البيانات من JWT Tokens
 * 
 * الوظائف الرئيسية:
 * ✅ generateToken(): توليد token جديد
 * ✅ extractUsername(): استخراج اسم المستخدم من token
 * ✅ validateToken(): التحقق من صحة token
 * ✅ isTokenExpired(): التحقق من انتهاء صلاحية token
 */
@Component
public class JwtUtil {

    /**
     * 🔑 المفتاح السري لتوقيع JWT
     * - يتم قراءته من application.properties
     * - يجب أن يكون قوياً وآمناً
     */
    @Value("${jwt.secret:404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970}")
    private String SECRET_KEY;

    /**
     * ⏱️ مدة صلاحية التوكن بالميلي ثانية
     * - الافتراضي: 24 ساعة (86400000 ms)
     * 10 سنوات ساعة (315360000000 ms)
     */
    @Value("${jwt.expiration:315360000000}")
    private Long jwtExpiration;

    /**
     * 🎫 توليد JWT Token للمستخدم
     * 
     * @param userDetails بيانات المستخدم
     * @return JWT Token
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails.getUsername());
    }

    /**
     * 🏗️ إنشاء JWT Token مع Claims محددة
     * 
     * @param claims البيانات الإضافية
     * @param subject الموضوع (عادة اسم المستخدم)
     * @return JWT Token
     */
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 🔑 الحصول على مفتاح التوقيع
     * 
     * @return مفتاح التوقيع
     */
    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * 📧 استخراج اسم المستخدم (Email) من Token
     * 
     * @param token JWT Token
     * @return اسم المستخدم
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * 📅 استخراج تاريخ انتهاء الصلاحية من Token
     * 
     * @param token JWT Token
     * @return تاريخ انتهاء الصلاحية
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * 📋 استخراج claim محدد من Token
     * 
     * @param token JWT Token
     * @param claimsResolver دالة استخراج Claim
     * @param <T> نوع البيانات المستخرجة
     * @return البيانات المستخرجة
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * 📦 استخراج جميع Claims من Token
     * 
     * @param token JWT Token
     * @return جميع Claims
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * ⏰ التحقق من انتهاء صلاحية Token
     * 
     * @param token JWT Token
     * @return true إذا انتهت الصلاحية، false إذا لم تنته
     */
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * ✅ التحقق من صحة Token
     * 
     * @param token JWT Token
     * @param userDetails بيانات المستخدم
     * @return true إذا كان Token صحيحاً، false إذا لم يكن
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}
