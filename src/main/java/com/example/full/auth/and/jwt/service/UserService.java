package com.example.full.auth.and.jwt.service;


import com.example.full.auth.and.jwt.dto.UpdateUserRequest;
import com.example.full.auth.and.jwt.dto.UserResponse;
import com.example.full.auth.and.jwt.model.Role;
import com.example.full.auth.and.jwt.model.User;
import com.example.full.auth.and.jwt.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 🛠️ خدمة إدارة المستخدمين (Service Layer)
 * 
 * الوصف:
 * - تحتوي على منطق الأعمال (Business Logic)
 * - تعمل كوسيط بين Controller و Repository
 * - تعالج البيانات قبل الحفظ أو الإرجاع
 * - توفر عمليات CRUD كاملة
 * 
 * فوائد طبقة الخدمات:
 * ✅ عزل منطق الأعمال عن Controller
 * ✅ إمكانية إعادة استخدام الكود
 * ✅ سهولة الاختبار (Unit Testing)
 * ✅ سهولة الصيانة والتعديل
 * 
 * العمليات المتوفرة:
 * 📄 getAllUsers(): إرجاع جميع المستخدمين
 * 🔍 getUserById(): البحث عن مستخدم بواسطة ID
 * ✏️ updateUser(): تحديث بيانات مستخدم
 * 🗑️ deleteUser(): حذف مستخدم
 * 
 * @author فريق التطوير
 * @version 2.0
 */
// 🎯 يخبر Spring أن هذا كلاس Service (طبقة منطق الأعمال)
@Service
// 🔧 من Lombok: ينشئ Constructor للحقول الـ final تلقائياً (Dependency Injection)
@RequiredArgsConstructor
public class UserService {

    /**
     * 📋 مرجع لـ UserRepository للتعامل مع قاعدة البيانات
     * 
     * ملاحظات:
     * - final: لضمان عدم تغيير المرجع بعد التهيئة
     * - يتم حقنه تلقائياً بواسطة @RequiredArgsConstructor
     * - هذا يسمى Constructor-based Dependency Injection
     */
    private final UserRepository userRepository;

    /**
     * Get current authenticated user
     */
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal() instanceof String) {
            throw new AccessDeniedException("Authentication required");
        }

        return (User) authentication.getPrincipal();
    }

    /**
     * 🔑 التحقق مما إذا كان المستخدم هو مسؤول
     * 
     * @param user المستخدم للتحقق
     * @return true إذا كان المستخدم مسؤولًا، false خلاف ذلك
     */
    private boolean isAdmin(User user) {
        //return user != null && "ADMIN".equals(user.getRole());
        return user != null && Role.ROLE_ADMIN.equals(user.getRole());

    }

    /**
     * ✅ التحقق مما إذا كان المستخدم يمكنه الوصول إلى بيانات المستخدم الآخر
     * 
     * @param currentUser المستخدم الحالي
     * @param targetUser المستخدم الهدف
     * @return true إذا كان لديه إذن للوصول، false خلاف ذلك
     */
    private boolean canAccessUser(User currentUser, User targetUser) {
        return isAdmin(currentUser) || currentUser.getId().equals(targetUser.getId());
    }

    /**
     * ✏️ التحقق مما إذا كان المستخدم يمكنه تحديث بيانات المستخدم الآخر
     * 
     * @param currentUser المستخدم الحالي
     * @param targetUser المستخدم الهدف
     * @return true إذا كان لديه إذن للتحديث، false خلاف ذلك
     */
    private boolean canUpdateUser(User currentUser, User targetUser) {
        return isAdmin(currentUser) || currentUser.getId().equals(targetUser.getId());
    }

    /**
     * 📄 إرجاع جميع المستخدمين من قاعدة البيانات
     * 
     * الوظيفة:
     * - تستدعي Repository.findAll() للحصول على البيانات
     * - تحول المستخدمين إلى UserResponse (بدون كلمة المرور)
     * - ترجع قائمة كاملة من كل المستخدمين
     * - فقط ADMINS يمكنهم الوصول إلى هذه القائمة
     * 
     * @return قائمة بجميع المستخدمين (يمكن أن تكون فارغة)
     */
    public Page<UserResponse> getAllUsers(Pageable pageable) {

        
        // Only ADMIN can get all users
//        if (!isAdmin(currentUser)) {
//            throw new RuntimeException("Access denied. Only administrators can view all users.");
//        }
        
        return userRepository.findAll(pageable)//.stream()
                .map(this::convertToUserResponse);
                //.collect(Collectors.toList());
    }

    /**
     * 🔍 البحث عن مستخدم بواسطة ID
     * 
     * الوظيفة:
     * - تبحث عن المستخدم في قاعدة البيانات
     * - تحقق من إذن الوصول (المستخدم يمكنه فقط الوصول لملفه أو ADMINS يمكنهم الوصول لأي ملف)
     * - ترجع بيانات المستخدم إذا وجد
     * - ترمي استثناء إذا لم يتم العثور عليه أو لا يوجد إذن
     * 
     * @param id معرف المستخدم
     * @return بيانات المستخدم
     * @throws RuntimeException إذا لم يتم العثور على المستخدم أو لا يوجد إذن للوصول
     */
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "المستخدم غير موجود - User not found with id: " + id
                ));
        
        User currentUser = getCurrentUser();
        
        // Check if current user is accessing their own data or is admin
        if (!canAccessUser(currentUser, user)) {
            throw new RuntimeException("Access denied. You can only access your own data.");
        }
        
        return convertToUserResponse(user);
    }


    public User getUserEntityById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    /**
     * ✏️ تحديث بيانات مستخدم
     * 
     * الوظيفة:
     * - تبحث عن المستخدم
     * - تحقق من إذن التحديث (المستخدم يمكنه فقط تحديث ملفه أو ADMINS يمكنهم التحديث لأي ملف)
     * - تحدث البيانات المرسلة فقط (لا تمس باقي البيانات)
     * - تحفظ التغييرات
     * 
     * @param id معرف المستخدم
     * @param request البيانات الجديدة
     * @return بيانات المستخدم المحدثة
     */
    public UserResponse updateUser(Long id, UpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "المستخدم غير موجود - User not found with id: " + id
                ));

        User currentUser = getCurrentUser();
        
        // Check if current user can update this user's data
        if (!canUpdateUser(currentUser, user)) {
            throw new RuntimeException("Access denied. You can only update your own data.");
        }

        // التحقق من إذن تغيير الدور والحالة (فقط ADMINS يمكنهم تغيير هذه الحقول)
        if (!isAdmin(currentUser)) {
            // إذا لم يكن المستخدم هو ADMIN، لا يمكنه تغيير الدور أو الحالة
            if (request.getRole() != null && !request.getRole().equals(user.getRole())) {
                throw new RuntimeException("Access denied. Only administrators can change user roles.");
            }
            if (request.getEnabled() != null && !request.getEnabled().equals(user.getEnabled())) {
                throw new RuntimeException("Access denied. Only administrators can change user status.");
            }
        }

        // تحديث البيانات المرسلة فقط
        if (request.getEmail() != null && !request.getEmail().isEmpty()) {
            // التحقق من عدم وجود البريد عند مستخدم آخر
            userRepository.findByEmail(request.getEmail())
                    .ifPresent(existingUser -> {
                        if (!existingUser.getId().equals(id)) {
                            throw new RuntimeException(
                                    "البريد الإلكتروني مستخدم بالفعل - Emolop00o0oail already exists"
                            );
                        }
                    });
            user.setEmail(request.getEmail());
        }
        if (request.getFullName() != null && !request.getFullName().isEmpty()) {
            user.setFullName(request.getFullName());
        }
        if (request.getPhoneNumber() != null && !request.getPhoneNumber().isEmpty()) {
            user.setPhoneNumber(request.getPhoneNumber());
        }
        if (request.getRole() != null) {
            user.setRole(request.getRole());
        }
        if (request.getEnabled() != null) {
            user.setEnabled(request.getEnabled());
        }

        User updatedUser = userRepository.save(user);
        return convertToUserResponse(updatedUser);
    }

    /**
     * 🗑️ حذف مستخدم
     * 
     * الوظيفة:
     * - تبحث عن المستخدم
     * - تحقق من إذن الحذف (فقط ADMINS يمكنهم الحذف)
     * - تحذفه من قاعدة البيانات
     * 
     *  id معرف المستخدم
     * @throws RuntimeException إذا لم يتم العثور على المستخدم أو لا يوجد إذن للحذف
     */
    public UserResponse deleteUser() {

        // Check if current user is admin
        User currentUser = getCurrentUser();
        
//        if (!isAdmin(currentUser)) {
//            throw new RuntimeException("Access denied. Only administrators can delete users.");
//        }
        
        // Don't allow admin to delete themselves
//        if (id.equals(currentUser.getId())) {
//            throw new RuntimeException("You cannot delete your own account.");
//        }
        currentUser.setEnabled(false);

        User updatedUser = userRepository.save(currentUser);
        return convertToUserResponse(updatedUser);
    }


    public UserResponse enablelDisableUserById(Long id, UpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "المستخدم غير موجود - User not found with id: " + id
                ));

        User currentUser = getCurrentUser();

        // Check if current user can update this user's data
        if (!canUpdateUser(currentUser, user)) {
            throw new RuntimeException("Access denied. You can only update your own data.");
        }

        // Don't allow admin to delete themselves
        if (id.equals(currentUser.getId())) {
            throw new RuntimeException("You cannot delete your own account.");
        }

        if (request.getEnabled() != null) {
            user.setEnabled(request.getEnabled());
        }

        User updatedUser = userRepository.save(user);
        return convertToUserResponse(updatedUser);
    }

    /**
     * 🔄 تحويل User Entity إلى UserResponse DTO
     * 
     * الوظيفة:
     * - تنسخ البيانات من User إلى UserResponse
     * - لا تنسخ كلمة المرور (لأسباب أمنية)
     * 
     * @param user كائن User من قاعدة البيانات
     * @return UserResponse للإرجاع في API
     */
    private UserResponse convertToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .phoneNumber(user.getPhoneNumber())
                .role(user.getRole())
                .enabled(user.getEnabled())
                .createdAt(user.getCreatedAt())
                .build();
    }
}