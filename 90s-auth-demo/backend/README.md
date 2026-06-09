# 90s Alumni Demo Backend

Spring Boot 3 demo backend for authentication, user profiles, home feeds, wall messages, and friend requests.

## Run

```bash
mvn spring-boot:run
```

Default service URL: `http://localhost:8080`

Swagger UI: `http://localhost:8080/swagger-ui.html`

## Default Admin

- email: `admin@90s.demo`
- password: `Admin123!`

## Main APIs

- `POST /api/auth/register`
- `POST /api/auth/login`
- `POST /api/auth/refresh`
- `POST /api/auth/logout`
- `GET /api/auth/me`
- `GET /api/home`
- `GET /api/home/users/{userId}`
- `POST /api/home/status`
- `POST /api/home/messages`
- `POST /api/home/users/{userId}/messages`
- `GET /api/friends/overview`
- `POST /api/friends/requests`
- `POST /api/friends/requests/{id}/accept`
- `POST /api/friends/requests/{id}/reject`
- `GET /api/admin/users`
- `POST /api/admin/users/{id}/ban`
