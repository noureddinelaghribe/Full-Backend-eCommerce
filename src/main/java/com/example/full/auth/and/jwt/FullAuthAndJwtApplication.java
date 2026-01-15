package com.example.full.auth.and.jwt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 🚀 تطبيق Spring Boot الرئيسي للمصادقة وإدارة المستخدمين
 * 
 * الوصف:
 * - نقطة البداية الرئيسية للتطبيق
 * - يحتوي على الإعدادات الأساسية والتكوين التلقائي
 * 
 * الميزات:
 * ✅ @SpringBootApplication: يجمع ثلاث أنوتيشنز مهمة:
 *    - @Configuration: يجعل الكلاس مصدر للإعدادات
 *    - @EnableAutoConfiguration: يفعل التكوين التلقائي
 *    - @ComponentScan: يبحث عن الكومبوننتس في الباكج الحالي وما تحته
 * 
 * @author فريق التطوير
 * @version 1.0
 */
@SpringBootApplication
public class FullAuthAndJwtApplication {

	/**
	 * 🎯 نقطة البداية الرئيسية للتطبيق
	 * 
	 * تفاصيل:
	 * - يتم استدعاء هذه الدالة عند تشغيل التطبيق
	 * - تقوم بتهيئة Spring Container وتشغيل التطبيق
	 * - تحميل جميع الإعدادات من application.properties
	 * - تفعيل جميع الـ Beans والـ Components
	 * 
	 * @param args معاملات سطر الأوامر (اختيارية)
	 */
	public static void main(String[] args) {
		// 🔥 تشغيل تطبيق Spring Boot
		SpringApplication.run(FullAuthAndJwtApplication.class, args);
	}

}
