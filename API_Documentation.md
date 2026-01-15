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