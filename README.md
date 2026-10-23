# ShopWave Backend - Spring Boot REST APIs

A production-ready Java Spring Boot backend for the **ShopWave** e-commerce platform. It provides RESTful APIs for user registration, user login with BCrypt password hashing, product catalog browsing, inventory tracking, order creation, and Cross-Origin Resource Sharing (CORS) configured for frontend integration.

---

## Architecture & Features

- **User Authentication**:
  - `POST /api/users/register` - User registration with email uniqueness validation and BCrypt password hashing.
  - `POST /api/users/login` - User login with password hash verification (returns 401 Unauthorized for invalid credentials).
- **Product Catalog**:
  - `GET /api/products` - Product listing with optional category (`?category=Electronics`) and search (`?search=wireless`) query parameters.
  - `GET /api/products/{id}` - Single product details lookup.
  - **Auto-Seeding**: Automatic population of initial sample products on startup if the database table is empty.
- **Order Management**:
  - `POST /api/orders` - Place orders, verify product stock, automatically deduct inventory, and persist line items within a database transaction.
  - `GET /api/orders/{id}` - Fetch details of a specific order.
  - `GET /api/orders/user/{userId}` - Fetch order history for a specific customer.
- **CORS Support**:
  - Configured in `WebConfig.java` to support requests from local frontend servers (e.g. `http://127.0.0.1:5500`, `http://localhost:3000`, `http://localhost:5500`, and `file://`).
- **Security & Validation**:
  - Passwords are never stored in plain text; they are hashed with `BCryptPasswordEncoder` (via `spring-security-crypto`).
  - Standardized JSON error bodies handled globally via `@RestControllerAdvice` (`GlobalExceptionHandler`).

---

## Database Entities & MySQL Schema

Hibernate automatically creates and updates the tables on startup (`spring.jpa.hibernate.ddl-auto=update`):

1. **`users` Table**:
   - `id` (BIGINT, Primary Key, Auto Increment)
   - `name` (VARCHAR 100, Not Null)
   - `email` (VARCHAR 150, Unique, Not Null)
   - `password` (VARCHAR 255, Not Null, BCrypt Hashed)
   - `created_at`, `updated_at` (TIMESTAMP)

2. **`products` Table**:
   - `id` (BIGINT, Primary Key, Auto Increment)
   - `name` (VARCHAR 150, Not Null)
   - `description` (VARCHAR 1000)
   - `price` (DECIMAL 10,2, Not Null)
   - `image_url` (VARCHAR 500)
   - `category` (VARCHAR 100)
   - `stock_quantity` (INT, Not Null)
   - `created_at`, `updated_at` (TIMESTAMP)

3. **`orders` Table**:
   - `id` (BIGINT, Primary Key, Auto Increment)
   - `user_id` (BIGINT, Foreign Key referencing `users(id)`)
   - `total_amount` (DECIMAL 10,2, Not Null)
   - `status` (VARCHAR 30, e.g. `PENDING`, `CONFIRMED`, `SHIPPED`)
   - `shipping_address` (VARCHAR 300, Not Null)
   - `created_at`, `updated_at` (TIMESTAMP)

4. **`order_items` Table**:
   - `id` (BIGINT, Primary Key, Auto Increment)
   - `order_id` (BIGINT, Foreign Key referencing `orders(id)`)
   - `product_id` (BIGINT, Foreign Key referencing `products(id)`)
   - `quantity` (INT, Not Null)
   - `unit_price` (DECIMAL 10,2, Not Null)
   - `subtotal` (DECIMAL 10,2, Not Null)

---

## Configuration (`application.properties`)

```properties
spring.application.name=backend-app
server.port=8080

# MySQL DataSource
spring.datasource.url=${MYSQL_URL:jdbc:mysql://localhost:3306/backend_db?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true}
spring.datasource.username=${MYSQL_USERNAME:root}
spring.datasource.password=${MYSQL_PASSWORD:your_mysql_password}
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# Hibernate Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
spring.jpa.open-in-view=false
```

---

## How to Run

### 1. Set MySQL Credentials
In PowerShell:
```powershell
$env:MYSQL_USERNAME="root"
$env:MYSQL_PASSWORD="your_actual_password"
```

### 2. Run Using Maven or IDE
- **In an IDE (IntelliJ IDEA / VS Code / Eclipse)**:
  Open this project folder, let the IDE import Maven dependencies, and run `BackendAppApplication.java`.
- **In Maven CLI** (if Maven is in your PATH):
  ```bash
  mvn spring-boot:run
  ```

---

## API Documentation & Examples

### 1. User Registration
- **URL**: `POST /api/users/register`
- **Request Body**:
```json
{
  "name": "Alex Johnson",
  "email": "alex@example.com",
  "password": "SecretPassword123"
}
```
- **Response (`201 Created`)**:
```json
{
  "id": 1,
  "name": "Alex Johnson",
  "email": "alex@example.com",
  "message": "User registered successfully",
  "timestamp": "2026-10-20T12:00:00"
}
```

---

### 2. User Login
- **URL**: `POST /api/users/login`
- **Request Body**:
```json
{
  "email": "alex@example.com",
  "password": "SecretPassword123"
}
```
- **Response (`200 OK`)**:
```json
{
  "id": 1,
  "name": "Alex Johnson",
  "email": "alex@example.com",
  "message": "Login successful",
  "timestamp": "2026-10-20T12:01:00"
}
```
- **Failed Response (`401 Unauthorized`)**:
```json
{
  "timestamp": "2026-10-20T12:01:05",
  "status": 401,
  "error": "Unauthorized",
  "message": "Invalid email or password",
  "path": "/api/users/login"
}
```

---

### 3. Product Listing & Search
- **URL**: `GET /api/products`
- **Filtered by Category**: `GET /api/products?category=Electronics`
- **Filtered by Search**: `GET /api/products?search=wireless`
- **Response (`200 OK`)**:
```json
[
  {
    "id": 1,
    "name": "Wireless Noise-Canceling Headphones",
    "description": "Premium wireless over-ear headphones with active noise cancellation and 30-hour battery life.",
    "price": 199.99,
    "imageUrl": "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=500",
    "category": "Electronics",
    "stockQuantity": 50
  }
]
```

---

### 4. Single Product Details
- **URL**: `GET /api/products/1`
- **Response (`200 OK`)**:
```json
{
  "id": 1,
  "name": "Wireless Noise-Canceling Headphones",
  "description": "Premium wireless over-ear headphones with active noise cancellation and 30-hour battery life.",
  "price": 199.99,
  "imageUrl": "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=500",
  "category": "Electronics",
  "stockQuantity": 50
}
```

---

### 5. Create Order
- **URL**: `POST /api/orders`
- **Request Body**:
```json
{
  "userId": 1,
  "shippingAddress": "123 Ocean Avenue, Wave City, WC 90210",
  "items": [
    {
      "productId": 1,
      "quantity": 2
    },
    {
      "productId": 3,
      "quantity": 1
    }
  ]
}
```
- **Response (`201 Created`)**:
```json
{
  "orderId": 1,
  "userId": 1,
  "userName": "Alex Johnson",
  "userEmail": "alex@example.com",
  "totalAmount": 489.88,
  "status": "PENDING",
  "shippingAddress": "123 Ocean Avenue, Wave City, WC 90210",
  "createdAt": "2026-10-20T12:05:00",
  "items": [
    {
      "productId": 1,
      "productName": "Wireless Noise-Canceling Headphones",
      "unitPrice": 199.99,
      "quantity": 2,
      "subtotal": 399.98
    },
    {
      "productId": 3,
      "productName": "Ergonomic Running Shoes",
      "unitPrice": 89.90,
      "quantity": 1,
      "subtotal": 89.90
    }
  ]
}
```

---

### 6. Get Order Details & Customer Orders
- **Single Order**: `GET /api/orders/{id}`
- **User Orders History**: `GET /api/orders/user/{userId}`

---

## Connecting from Frontend (JavaScript Fetch Example)

```javascript
// Register a user
async function register(name, email, password) {
  const response = await fetch("http://localhost:8080/api/users/register", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ name, email, password })
  });
  return response.json();
}

// Fetch products for the shop page
async function loadProducts() {
  const response = await fetch("http://localhost:8080/api/products");
  const products = await response.json();
  console.log("Loaded products:", products);
}
```
