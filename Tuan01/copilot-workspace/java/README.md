# Social Media API - Spring Boot Implementation

This is a RESTful API implementation using Spring Boot that follows the same structure as the FastAPI Python version.

## Project Structure

```
src/main/java/com/contoso/socialapp/
├── SocialappApplication.java          # Main application class
├── config/
│   └── CorsConfig.java                # CORS configuration
├── controller/                        # REST Controllers
│   ├── HealthController.java          # Health check endpoints
│   ├── PostController.java            # Posts CRUD operations
│   ├── CommentController.java         # Comments CRUD operations
│   └── LikeController.java            # Like/Unlike operations
├── dto/                               # Data Transfer Objects
│   ├── PostCreateRequest.java
│   ├── PostUpdateRequest.java
│   ├── PostResponse.java
│   ├── CommentCreateRequest.java
│   ├── CommentUpdateRequest.java
│   ├── CommentResponse.java
│   ├── LikeRequest.java
│   └── LikeResponse.java
├── exception/                         # Exception handling
│   ├── ResourceNotFoundException.java
│   ├── BadRequestException.java
│   ├── ErrorResponse.java
│   └── GlobalExceptionHandler.java
├── model/                            # JPA Entities
│   ├── Post.java
│   ├── Comment.java
│   ├── Like.java
│   └── LikeId.java
├── repository/                       # Data Access Layer
│   ├── PostRepository.java
│   ├── CommentRepository.java
│   └── LikeRepository.java
└── service/                         # Business Logic Layer
    ├── PostService.java
    ├── CommentService.java
    └── LikeService.java
```

## API Endpoints

Base URL: `http://localhost:8080/api`

### Health Check
- `GET /` - Home endpoint
- `GET /health` - Health check

### Posts
- `GET /posts` - List all posts
- `POST /posts` - Create a new post
- `GET /posts/{postId}` - Get a specific post
- `PATCH /posts/{postId}` - Update a post
- `DELETE /posts/{postId}` - Delete a post

### Comments
- `GET /posts/{postId}/comments` - List comments for a post
- `POST /posts/{postId}/comments` - Create a comment
- `GET /posts/{postId}/comments/{commentId}` - Get a specific comment
- `PATCH /posts/{postId}/comments/{commentId}` - Update a comment
- `DELETE /posts/{postId}/comments/{commentId}` - Delete a comment

### Likes
- `POST /posts/{postId}/likes` - Like a post
- `DELETE /posts/{postId}/likes` - Unlike a post

## Running the Application

### Prerequisites
- Java 17 or higher
- Gradle 7.x or higher (included via wrapper)

### Steps

1. Navigate to the project directory:
   ```bash
   cd D:\HKII_4\Seminar\HP-Seminar-Vibe-Coding-SGU\Tuan01\copilot-workspace\java\socialapp
   ```

2. Run the application:
   ```bash
   # Windows
   .\gradlew.bat bootRun
   
   # Linux/Mac
   ./gradlew bootRun
   ```

3. Access the API:
   - API Base URL: http://localhost:8080/api
   - Swagger UI: http://localhost:8080/api/ (OpenAPI documentation)
   - H2 Console: http://localhost:8080/api/h2-console

## Features Implemented

### RESTful Design
- Proper HTTP methods (GET, POST, PATCH, DELETE)
- Appropriate HTTP status codes (200, 201, 204, 400, 404, 500)
- Resource-based URL structure
- JSON request/response format

### Data Validation
- Bean Validation with @Valid annotations
- Custom validation messages
- Proper error responses with meaningful messages

### Database
- H2 in-memory database for development
- JPA/Hibernate for ORM
- Automatic table creation
- Transaction management

### Exception Handling
- Global exception handler with @ControllerAdvice
- Custom exception classes
- Consistent error response format

### Documentation
- OpenAPI 3.0 specification
- Swagger UI integration
- API endpoint documentation with @Operation annotations

### Testing
- JUnit 5 ready
- Spring Boot Test support
- MockMvc for integration testing

### Development Features
- Hot reload with Spring Boot DevTools
- Actuator endpoints for monitoring
- H2 console for database inspection

## Configuration

The application is configured via `application.yml`:

```yaml
server:
  port: 8080
  servlet:
    context-path: /api

spring:
  datasource:
    url: jdbc:h2:mem:socialapp
    driver-class-name: org.h2.Driver
    username: sa
    password: password
    
  h2:
    console:
      enabled: true
      path: /h2-console
      
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: false
    database-platform: org.hibernate.dialect.H2Dialect
```

## Example Usage

### Create a Post
```bash
curl -X POST http://localhost:8080/api/posts \
  -H "Content-Type: application/json" \
  -d '{"username": "john", "content": "Hello World!"}'
```

### Get All Posts
```bash
curl http://localhost:8080/api/posts
```

### Create a Comment
```bash
curl -X POST http://localhost:8080/api/posts/{postId}/comments \
  -H "Content-Type: application/json" \
  -d '{"username": "jane", "content": "Nice post!"}'
```

### Like a Post
```bash
curl -X POST http://localhost:8080/api/posts/{postId}/likes \
  -H "Content-Type: application/json" \
  -d '{"username": "john"}'
```

## Notes

- The API follows the same business logic as the Python FastAPI implementation
- Username validation ensures only the original author can edit/delete their content
- Cascading deletes handle relationships properly
- The API uses proper RESTful conventions and HTTP status codes
- All endpoints include OpenAPI documentation for easy testing with Swagger UI
