# Simple REST API Documentation

Simple REST API with JWT authentication.

## Base URL
`http://localhost:8080`

## Authentication
Protected endpoints require JWT token:
```
Authorization: Bearer <token>
```

## API Endpoints

### Authentication

**Register User**
- `POST /api/auth/register`
- No authentication required
- Request body: `{"email":"user@example.com","password":"pass","fullName":"Name","phoneNumber":"123","role":"USER"}`
- Response: `{"token":"...","message":"Registration successful"}`

**Login User**
- `POST /api/auth/login`
- No authentication required
- Request body: `{"email":"user@example.com","password":"pass"}`
- Response: `{"token":"...","message":"Login successful"}`

### User Management (requires JWT token)

**Get Current User**

### Product Management (requires JWT token)

**Get All Products**
- `GET /api/products`
- Response: Array of all products

**Get Product By ID**
- `GET /api/products/{id}`
- Response: Single product data

**Create Product**
- `POST /api/products`
- Admin only
- Request body: `{"name":"Product Name","description":"Description","price":19.99,"stock":100,"imageUrl":"https://example.com/image.jpg"}`
- Response: Created product data

**Update Product**
- `PUT /api/products/{id}`
- Admin only
- Request body: Product update data
- Response: Updated product data

**Delete Product**
- `DELETE /api/products/{id}`
- Admin only
- Response: `{"message":"Product deleted successfully"}`

### User Management (requires JWT token)

**Get Current User**
- `GET /api/users/profile`
- Response: User profile data

**Get All Users**
- `GET /api/users/all`
- Admin only
- Response: Array of all users

**Get User By ID**
- `GET /api/users/{id}`
- Response: Single user data

**Update User**
- `PUT /api/users/{id}`
- Request body: User update data
- Response: Updated user data

**Delete User**
- `DELETE /api/users/{id}`
- Admin only
- Response: `{"message":"User deleted successfully"}`