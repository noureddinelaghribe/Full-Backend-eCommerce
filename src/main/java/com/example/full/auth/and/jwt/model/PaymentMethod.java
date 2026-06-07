package com.example.full.auth.and.jwt.model;

public enum PaymentMethod {

    CREDIT_CARD,      // الدفع ببطاقة ائتمان
    DEBIT_CARD,       // بطاقة بنكية مباشرة
    PAYPAL,           // الدفع عبر PayPal
    STRIPE,           // الدفع عبر Stripe
    APPLE_PAY,        // Apple Pay
    GOOGLE_PAY,       // Google Pay
    BANK_TRANSFER,    // تحويل بنكي
    CASH_ON_DELIVERY, // الدفع عند الاستلام
    CRYPTO,           // العملات الرقمية
    WALLET            // محفظة داخل التطبيق

}