## 📘 REST API Documentation / توثيق واجهة الـ API

Simple e‑commerce REST API with JWT authentication (Auth, Users, Products, Categories, Cart, Checkout, Orders).  
واجهة برمجية (REST API) لمتجر بسيط مع مصادقة JWT (تسجيل، دخول، منتجات، تصنيفات، سلة، طلبات).

### 🔗 Base URL / عنوان الأساس

- **Base URL**: `http://localhost:8080`

### 🔐 Authentication / المصادقة

- **Header** (for protected endpoints):  
  `Authorization: Bearer <JWT_TOKEN>`
- بعد التسجيل أو تسجيل الدخول ستحصل على `token` في الاستجابة وتستخدمه في جميع الطلبات التالية.

---

## 🔑 Auth Endpoints / نقاط نهاية المصادقة

### **Register / تسجيل مستخدم جديد**

- **Method**: `POST`  
- **Path**: `/api/auth/register`  
- **Auth**: ❌ لا يحتاج JWT
- **Request body**:

```json
{
  "email": "buyer@example.com",
  "password": "secret123",
  "fullName": "Buyer Name",
  "phoneNumber": "+213555000000",
  "role": "ROLE_BUYER"
}
```

- **Success response** `200 OK` (DTO: `AuthResponse`):

```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "id": 1,
  "email": "buyer@example.com",
  "fullName": "Buyer Name",
  "role": "ROLE_BUYER",
  "message": "Registration successful"
}
```

### **Login / تسجيل الدخول**

- **Method**: `POST`  
- **Path**: `/api/auth/login`  
- **Auth**: ❌ لا يحتاج JWT
- **Request body**:

```json
{
  "email": "buyer@example.com",
  "password": "secret123"
}
```

- **Success response** `200 OK` (نفس شكل `AuthResponse`):

```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "id": 1,
  "email": "buyer@example.com",
  "fullName": "Buyer Name",
  "role": "ROLE_BUYER",
  "message": "Login successful"
}
```

### **Hello Test Endpoint**

- **Method**: `GET`  
- **Path**: `/api/auth/hello`  
- **Auth**: غالبًا مفتوح (للاختبار)  
- **Response**:

```text
Hello, World! 0
```

---

## 👤 User APIs / واجهة المستخدمين

Base path: `/api/users` (معظمها تتطلب JWT).

### **Get current user profile / جلب المستخدم الحالي**

- **Method**: `GET`  
- **Path**: `/api/users/profile`  
- **Auth**: ✅ مطلوب JWT
- **Success response** `200 OK` (DTO: `UserResponse` مثال تقريبي):

```json
{
  "id": 1,
  "email": "buyer@example.com",
  "fullName": "Buyer Name",
  "phoneNumber": "+213555000000",
  "role": "ROLE_BUYER"
}
```

### **Get all users (ADMIN) / جميع المستخدمين**

- **Method**: `GET`  
- **Path**: `/api/users/all`  
- **Auth**: ✅ `hasRole('ADMIN')`
- **Response** `200 OK`:

```json
[
  {
    "id": 1,
    "email": "admin@example.com",
    "fullName": "Admin",
    "phoneNumber": "000",
    "role": "ROLE_ADMIN"
  },
  {
    "id": 2,
    "email": "buyer@example.com",
    "fullName": "Buyer Name",
    "phoneNumber": "123",
    "role": "ROLE_BUYER"
  }
]
```

### **Get user by id / مستخدم بالمعرف**

- **Method**: `GET`  
- **Path**: `/api/users/{id}`  
- **Auth**: ✅ مطلوب JWT
- **Success** `200 OK`: نفس شكل `UserResponse`.  
- **Error** `404 NOT_FOUND`:

```json
{ "message": "User not found" }
```

### **Update user / تحديث مستخدم**

- **Method**: `PUT`  
- **Path**: `/api/users/{id}`  
- **Auth**: ✅ مطلوب JWT
- **Request body** (DTO: `UpdateUserRequest` – مثال تقريبي):

```json
{
  "fullName": "New Name",
  "phoneNumber": "+213555111111"
}
```

- **Success** `200 OK`: كائن `UserResponse` محدث.  
- **Error** `400 BAD_REQUEST` مع:

```json
{ "message": "Validation error or business error" }
```

### **Delete user (ADMIN) / حذف مستخدم**

- **Method**: `DELETE`  
- **Path**: `/api/users/{id}`  
- **Auth**: ✅ `hasRole('ADMIN')`
- **Success** `200 OK`:

```json
{ "message": "User deleted successfully" }
```

- **Error** `404 NOT_FOUND`:

```json
{ "message": "User not found" }
```

---

## 🧾 Product APIs / واجهة المنتجات

Base path: `/api/products`

### **Get all products / كل المنتجات**

- **Method**: `GET`  
- **Path**: `/api/products`  
- **Auth**: غالبًا ✅ (حسب إعدادات الأمن)
- **Response** `200 OK` (List of `ProductResponse`):

```json
[
  {
    "id": 1,
    "name": "Laptop",
    "description": "Gaming laptop",
    "price": 1500.0,
    "stock": 5,
    "imageUrl": "https://example.com/laptop.png",
    "sellerId": 10,
    "sellerName": "Seller One",
    "categoryId": 3,
    "categoryName": "Electronics"
  }
]
```

### **Get my products / منتجاتي**

- **Method**: `GET`  
- **Path**: `/api/products/my`  
- **Auth**: ✅ مطلوب JWT (بائع)
- **Response**: قائمة `ProductResponse` للبائع الحالي.

### **Get products by seller id**

- **Method**: `GET`  
- **Path**: `/api/products/seller/{id}`

### **Get product by id / منتج محدد**

- **Method**: `GET`  
- **Path**: `/api/products/{id}`  
- **Success** `200 OK`: `ProductResponse` واحد.  
- **Error** `404 NOT_FOUND`:

```json
{ "message": "Product not found" }
```

### **Create product / إنشاء منتج**

- **Method**: `POST`  
- **Path**: `/api/products`  
- **Auth**: ✅ مطلوب JWT
- **Request body** (DTO: `ProductRequest`):

```json
{
  "name": "Laptop",
  "description": "Gaming laptop",
  "price": 1500.0,
  "stock": 5,
  "categoryId": 3,
  "imageUrl": "https://example.com/laptop.png"
}
```

- **Success** `200 OK`:

```json
{
  "id": 1,
  "name": "Laptop",
  "description": "Gaming laptop",
  "price": 1500.0,
  "stock": 5,
  "imageUrl": "https://example.com/laptop.png",
  "sellerId": 10,
  "sellerName": "Seller One",
  "categoryId": 3,
  "categoryName": "Electronics"
}
```

- **Error** `400 BAD_REQUEST` مع:

```json
{ "message": "validation or business error" }
```

### **Update product / تعديل منتج**

- **Method**: `PUT`  
- **Path**: `/api/products/{id}`  
- **Request body**: نفس `ProductRequest`.  
- **Response**: `200 OK` مع `ProductResponse` محدث أو `400 BAD_REQUEST`.

### **Delete product / حذف منتج**

- **Method**: `DELETE`  
- **Path**: `/api/products/{id}`  
- **Response**:

```json
{ "message": "Product deleted successfully" }
```

### **Search products / بحث**

- **Method**: `GET`  
- **Path**: `/api/products/search`  
- **Query param**: `q` (اختياري – اسم أو وصف المنتج)  
- **Example**: `/api/products/search?q=laptop`

---

## 🏷 Category APIs / واجهة التصنيفات

Base path: `/api/categories`

### **Get all categories / كل التصنيفات**

- **Method**: `GET`  
- **Path**: `/api/categories`
- **Response** `200 OK` (List of `CategoryResponse`):

```json
[
  {
    "id": 3,
    "name": "Electronics",
    "description": "Electronic devices and accessories"
  }
]
```

### **Get category by id**

- **Method**: `GET`  
- **Path**: `/api/categories/{id}`
- **Success** `200 OK`:

```json
{
  "id": 3,
  "name": "Electronics",
  "description": "Electronic devices and accessories"
}
```

- **Error** `404 NOT_FOUND`:

```json
{ "message": "Category not found" }
```

### **Create category / إنشاء تصنيف**

- **Method**: `POST`  
- **Path**: `/api/categories`
- **Request body** (DTO: `CategoryRequest`):

```json
{
  "name": "Electronics",
  "description": "Electronic devices and accessories"
}
```

- **Success** `200 OK`: `CategoryResponse`  
- **Error** `400 BAD_REQUEST` مع:

```json
{ "message": "validation or business error" }
```

### **Update category / تعديل تصنيف**

- **Method**: `PUT`  
- **Path**: `/api/categories/{id}`  
- **Request body**: نفس `CategoryRequest`.  
- **Response**: `200 OK` مع `CategoryResponse` أو `400 BAD_REQUEST`.

### **Delete category / حذف تصنيف**

- **Method**: `DELETE`  
- **Path**: `/api/categories/{id}`  
- **Success**:

```json
{ "message": "Product deleted successfully" }
```

- **Error** `404 NOT_FOUND`:

```json
{ "message": "Category not found" }
```

---

## 🛒 Cart APIs / واجهة السلة

Base path: `/api/cart` (كلها ✅ تتطلب JWT).

### **Get my cart / سلة المستخدم الحالي**

- **Method**: `GET`  
- **Path**: `/api/cart`
- **Success** `200 OK` (List of `CartResponse`):

```json
[
  {
    "id": 1,
    "quantity": 2,
    "product": {
      "id": 5,
      "name": "Laptop",
      "description": "Gaming laptop",
      "price": 1500.0,
      "stock": 5,
      "imageUrl": "https://example.com/laptop.png",
      "sellerId": 10,
      "sellerName": "Seller One",
      "categoryId": 3,
      "categoryName": "Electronics"
    }
  }
]
```

### **Add to cart / إضافة منتج للسلة**

- **Method**: `POST`  
- **Path**: `/api/cart`
- **Request body** (DTO: `CartCreateRequest` – لاحظ أنه لا يوجد `quantity`):

```json
{
  "productId": 5
}
```

- **Success** `201 CREATED`:

```json
{
  "id": 1,
  "quantity": 1,
  "product": {
    "id": 5,
    "name": "Laptop",
    "price": 1500.0
  }
}
```

### **Update cart item / تعديل عنصر**

- **Method**: `PUT`  
- **Path**: `/api/cart/{cartId}`
- **Request body** (DTO: `CartUpdateRequest`):

```json
{
  "quantity": 3,
  "productId": 5
}
```

- **Response** `200 OK`: كائن `CartResponse` محدث.

### **Increase quantity / زيادة الكمية**

- **Method**: `PUT`  
- **Path**: `/api/cart/Increase/{cartId}`

### **Decrease quantity / إنقاص الكمية**

- **Method**: `PUT`  
- **Path**: `/api/cart/Decrease/{cartId}`

### **Remove item / حذف عنصر**

- **Method**: `DELETE`  
- **Path**: `/api/cart/{cartId}`
- **Response**: `204 NO_CONTENT`

### **Clear cart / مسح السلة**

- **Method**: `DELETE`  
- **Path**: `/api/cart`
- **Response**: `204 NO_CONTENT`

---

## 💳 Checkout APIs / واجهة إكمال الطلب

Base path: `/api/checkout`

### **Do checkout / تنفيذ الشراء**

- **Method**: `POST`  
- **Path**: `/api/checkout`  
- **Auth**: ✅ مطلوب JWT  
- **Request body** (DTO: `CheckoutRequest`):

```json
{
  "paymentMethod": "CASH_ON_DELIVERY",
  "shippingAddress": "Algiers, Algeria"
}
```

- **Success** `201 CREATED` (DTO: `CheckoutResponse`):

```json
{
  "id": 10,
  "totalAmount": 3000.0,
  "paymentMethod": "CASH_ON_DELIVERY",
  "paymentStatus": "PENDING",
  "shippingAddress": "Algiers, Algeria",
  "createdAt": "2026-03-05T12:00:00",
  "items": [
    {
      "id": 1,
      "quantity": 2,
      "product": {
        "id": 5,
        "name": "Laptop",
        "price": 1500.0
      }
    }
  ]
}
```

### **Get my checkouts / طلبات الشراء الخاصة بي**

- **Method**: `GET`  
- **Path**: `/api/checkout`
- **Response**: قائمة من `CheckoutResponse`.

### **Get checkout by id**

- **Method**: `GET`  
- **Path**: `/api/checkout/{id}`

### **Update payment status / تحديث حالة الدفع**

- **Method**: `PATCH`  
- **Path**: `/api/checkout/{id}/payment-status`  
- **Request body** (DTO: `UpdatePaymentStatusRequest`):

```json
{
  "paymentStatus": "PAID"
}
```

- **Response** `200 OK`: كائن `CheckoutResponse` محدث.

---

## 📦 Order APIs / واجهة الطلبات

Base path: `/api/orders`

### **Create order / إنشاء طلب من السلة**

- **Method**: `POST`  
- **Path**: `/api/orders`  
- **Query param**: `shippingAddress`  
- **Example**:

```http
POST /api/orders?shippingAddress=Algiers%2C%20Algeria
Authorization: Bearer <token>
```

- **Success** `201 CREATED` (DTO: `OrderResponse` – مثال مبسط):

```json
{
  "id": 20,
  "totalAmount": 3000.0,
  "shippingAddress": "Algiers, Algeria",
  "status": "PENDING",
  "createdAt": "2026-03-05T12:05:00",
  "userId": 1,
  "userFullName": "Buyer Name",
  "userEmail": "buyer@example.com",
  "items": [
    {
      "productId": 5,
      "productName": "Laptop",
      "quantity": 2,
      "price": 1500.0
    }
  ]
}
```

### **Get my orders / طلباتي**

- **Method**: `GET`  
- **Path**: `/api/orders/my`
- **Response**: قائمة `OrderResponse`.

### **Get order by id / طلب محدد**

- **Method**: `GET`  
- **Path**: `/api/orders/{id}`

### **Cancel order / إلغاء طلب**

- **Method**: `PATCH`  
- **Path**: `/api/orders/{id}/cancel`
- **Response**: `200 OK` مع `OrderResponse` بحالة محدثة (مثال: `status: "CANCELLED"`).

---

## 🧪 Common Error Response / شكل الأخطاء

- **Validation or not found error example**:

```json
{
  "message": "Error message here"
}
```

Use this document كمرجع سريع للمسارات، الأجسام (Body) و نماذج الاستجابة أثناء اختبار الـ API من Postman أو Frontend.