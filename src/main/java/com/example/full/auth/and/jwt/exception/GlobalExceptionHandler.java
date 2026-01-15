package com.example.full.auth.and.jwt.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * 🚨 Global Exception Handler - معالج الأخطاء العام
 * 
 * الوصف:
 * - يتعامل مع جميع الأخطاء في التطبيق
 * - يرجع استجابات JSON منسقة للأخطاء
 * - يمنع ظهور stack trace للمستخدم
 * 
 * أنواع الأخطاء المعالجة:
 * ✅ Validation Errors (أخطاء التحقق من البيانات)
 * ✅ Authentication Errors (أخطاء المصادقة)
 * ✅ Runtime Exceptions (أخطاء وقت التشغيل)
 * ✅ General Exceptions (الأخطاء العامة)
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 🔍 معالجة أخطاء التحقق من صحة البيانات
     * 
     * مثال:
     * - البريد الإلكتروني غير صالح
     * - كلمة المرور قصيرة جداً
     * - الحقول المطلوبة فارغة
     * 
     * @param ex استثناء التحقق
     * @return استجابة تحتوي على جميع أخطاء التحقق
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(
            MethodArgumentNotValidException ex
    ) {
        Map<String, String> errors = new HashMap<>();
        
        // جمع جميع أخطاء التحقق
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        Map<String, Object> response = new HashMap<>();
        response.put("status", "error");
        response.put("message", "خطأ في التحقق من البيانات - Validation failed");
        response.put("errors", errors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * 🔐 معالجة أخطاء المصادقة
     * 
     * مثال:
     * - بريد إلكتروني أو كلمة مرور خاطئة
     * - محاولة تسجيل دخول بحساب غير موجود
     * 
     * @param ex استثناء المصادقة
     * @return استجابة خطأ مصادقة
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleBadCredentialsException(
            BadCredentialsException ex
    ) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "error");
        response.put("message", "بيانات الدخول غير صحيحة - Invalid email or password");

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    /**
     * ⚠️ معالجة أخطاء وقت التشغيل
     * 
     * مثال:
     * - المستخدم غير موجود
     * - البريد الإلكتروني مستخدم بالفعل
     * - عملية غير مسموح بها
     * 
     * @param ex استثناء وقت التشغيل
     * @return استجابة خطأ
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(
            RuntimeException ex
    ) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "error");
        response.put("message", ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * 🚨 معالجة الأخطاء العامة
     * 
     * الوصف:
     * - يتعامل مع أي خطأ غير متوقع
     * - يمنع تسريب معلومات حساسة
     * 
     * @param ex الاستثناء
     * @return استجابة خطأ عامة
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneralException(
            Exception ex
    ) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "error");
        response.put("message", "حدث خطأ في الخادم - Internal server error");
        response.put("details", ex.getMessage());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
