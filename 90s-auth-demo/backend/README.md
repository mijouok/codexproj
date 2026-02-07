# 90's 校友网 Sprint 1 小 Demo (Auth/权限模板 + Space 邀请码)

这是一个可直接运行的 **Spring Boot 3 + JWT** demo，用于展示：
- 注册 / 登录
- Access/Refresh Token（Refresh 可吊销、旋转）
- RBAC（角色） + Trust Level（L0-L3）骨架
- 首发空间（Space/Cohort）+ 邀请码加入 + Membership 守门
- 最小 Admin：封禁/解封

## 运行

### 1) 启动
```bash
mvn spring-boot:run
```

服务默认在：`http://localhost:8080`

### 2) Swagger
打开：`http://localhost:8080/swagger-ui.html`

### 3) H2 控制台
打开：`http://localhost:8080/h2`
JDBC URL：`jdbc:h2:mem:alumni`

## 默认管理员
启动时会自动创建一个平台管理员：
- email: `admin@90s.local`
- password: `Admin12345!`

你可以用它创建 Space、生成邀请码。

## 关键接口（简表）
- `POST /api/auth/register`
- `POST /api/auth/login`
- `POST /api/auth/refresh`
- `POST /api/auth/logout`
- `GET /api/auth/me`

- `POST /api/spaces` (platform_admin)
- `POST /api/spaces/{id}/invite-codes` (space_admin+)
- `POST /api/spaces/join-by-code` (登录用户)

- `GET /api/admin/users` (platform_admin)
- `POST /api/admin/users/{id}/ban` (platform_admin)

## 复用建议
Auth/权限相关代码集中在：
- `com.nineties.alumni.auth.*`

后续新作品可以直接复制 `auth` 包 + `SecurityConfig/JwtService/AuthService` + 表结构，快速起一套可靠登录与权限体系。

> 注意：这是 demo，不包含完整内容审核/反黑产，仅保留最小骨架与关键扩展点。
