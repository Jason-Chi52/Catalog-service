
# Catalog Service API

This is a backend REST API built with **Java Spring Boot** to manage a simple product catalog. It supports basic CRUD operations (Create, Read, Update, Delete) on products with fields like name, description, and price.

---

## Tech Stack

- Java 17
- Spring Boot 3.5.x
- Spring Data JPA (with H2 in-memory DB)
- Gradle Build Tool
- REST API (JSON over HTTP)

---

## Project Structure

```
catalog-service-api/
├── controller/             # REST endpoints
├── model/                  # Product entity
├── repo/                   # JPA repository
├── resources/
│   └── application.properties
└── CatalogServiceApplication.java
```

---

## Getting Started

### Prerequisites

- Java 17+
- Gradle (or use included `gradlew`)
- Curl / Postman for testing

### Run the app

```bash
cd catalog-service-api
./gradlew bootRun   # or gradlew.bat bootRun on Windows CMD
```

The API will be available at:
`http://localhost:8080`

---

## API Endpoints

### Get all products

```http
GET /products
```

### Get product by ID

```http
GET /products/{id}
```

### Create new product

```bash
curl -X POST http://localhost:8080/products \
  -H "Content-Type: application/json" \
  -d "{\"name\":\"Chair\",\"description\":\"Wooden chair\",\"price\":79.99}"
```

### Update a product

```bash
curl -X PUT http://localhost:8080/products/1 \
  -H "Content-Type: application/json" \
  -d "{\"name\":\"Chair Deluxe\",\"description\":\"Premium wood\",\"price\":99.99}"
```

### Delete a product

```bash
curl -X DELETE http://localhost:8080/products/1
```

---

## CORS

The backend accepts requests from your React frontend:

```java
@CrossOrigin(origins = "http://localhost:5173")
```

---

## Future Enhancements

- Add input validation with `@Valid`
- Add Swagger UI for documentation
- Integrate with PostgreSQL or MySQL
- Connect to React frontend (`catalog-service-ui`)

---

## Author

Jason Chi
Software Engineering Student


---
