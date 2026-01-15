package com.example.full.auth.and.jwt.repository;


import com.example.full.auth.and.jwt.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 📋 مستودع بيانات المستخدمين (Repository)
 * 
 * الوصف:
 * - يوفر عمليات CRUD جاهزة بدون كود إضافي
 * - يتعامل مباشرة مع جدول "users" في الداتابيز
 * - يرث من JpaRepository للحصول على وظائف جاهزة
 * 
 * العمليات المتوفرة تلقازياً:
 * ✅ findAll(): إرجاع جميع المستخدمين
 * ✅ findById(id): البحث عن مستخدم بواسطة الـ ID
 * ✅ save(user): حفظ أو تحديث مستخدم
 * ✅ delete(user): حذف مستخدم
 * ✅ deleteById(id): حذف مستخدم بواسطة الـ ID
 * ✅ count(): عدد المستخدمين
 * ✅ existsById(id): التحقق من وجود مستخدم
 * 
 * ملاحظات:
 * - يمكن إضافة دوال مخصصة مثل:
 *   findByEmail(String email)
 *   findByRole(String role)
 *   findByEnabledTrue()
 * - Spring Data JPA يولد الاستعلامات تلقائياً بناءً على اسم الدالة
 * 
 * @author فريق التطوير
 * @version 1.0
 */
// 🎯 يخبر Spring أن هذا مستودع بيانات (Repository Layer)
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // 💡 هذا الريبو يوفر CRUD جاهز بدون كود إضافي!
    // يمكن إضافة دوال مخصصة هنا مثل:
     Optional<User> findByEmail(String email);
//     List<User> findByRole(String role);
//     List<User> findByEnabledTrue();
}
