# 90's 校友网 | Sprint 1 Auth/权限 Demo（可复用模板）

这个 Demo 对应你要给合作伙伴看的 **Sprint 1**：
- 注册 / 登录 / 刷新令牌 / 登出
- RBAC 角色（platform_admin / member 等）
- Trust Level 字段（L0-L3）预留
- 空间（某学校某项目某届）创建、邀请码、加入空间、成员关系守门
- 最小管理端接口（封禁/解封/列用户）

> 这是**后端 API Demo**（Spring Boot）。前端你后面可以接 Vue/React；Sprint 1 给伙伴验证可行性，API 已足够。

## 快速开始

### 1) 环境
- JDK 17+
- Maven 3.9+

### 2) 启动
```bash
cd backend
mvn spring-boot:run
```

默认端口：`http://localhost:8080`
- Swagger UI：`/swagger-ui/index.html`
- H2 Console：`/h2`（JDBC URL 在 `application.yml`）

### 3) 默认平台管理员（演示用）
- Email: `admin@90s.demo`
- Password: `Admin123!`

## 典型演示流程（5 分钟给伙伴看懂）

### A. 管理员登录拿 token
```bash
curl -s -X POST http://localhost:8080/api/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"email":"admin@90s.demo","password":"Admin123!"}'
```
得到 `accessToken/refreshToken`。

### B. 创建首发空间（某学校某项目某届）
```bash
curl -s -X POST http://localhost:8080/api/spaces \
  -H 'Content-Type: application/json' \
  -H 'Authorization: Bearer <ACCESS_TOKEN>' \
  -d '{"name":"UCL MSc CS 2022F","slug":"ucl-msc-cs-2022f"}'
```

### C. 生成邀请码
```bash
curl -s -X POST http://localhost:8080/api/spaces/<SPACE_ID>/invite-codes \
  -H 'Content-Type: application/json' \
  -H 'Authorization: Bearer <ACCESS_TOKEN>' \
  -d '{"type":"MULTI_USE","maxUses":50,"expiresInDays":30}'
```

### D. 普通用户注册 + 用邀请码加入空间
1) 注册
```bash
curl -s -X POST http://localhost:8080/api/auth/register \
  -H 'Content-Type: application/json' \
  -d '{"email":"user1@test.com","password":"Passw0rd!","nickname":"User1"}'
```
2) 加入空间
```bash
curl -s -X POST http://localhost:8080/api/spaces/join-by-code \
  -H 'Content-Type: application/json' \
  -H 'Authorization: Bearer <USER_ACCESS_TOKEN>' \
  -d '{"code":"<INVITE_CODE>"}'
```

### E. 刷新令牌/登出
```bash
# refresh
curl -s -X POST http://localhost:8080/api/auth/refresh \
  -H 'Content-Type: application/json' \
  -d '{"refreshToken":"<REFRESH_TOKEN>"}'

# logout（撤销 refresh token）
curl -s -X POST http://localhost:8080/api/auth/logout \
  -H 'Content-Type: application/json' \
  -d '{"refreshToken":"<REFRESH_TOKEN>"}'
```

## 复用点（你后续作品可直接搬）
- `com.nineties.alumni.security.*`：JWT + Filter + Method Security
- `com.nineties.alumni.auth.*`：注册/登录/刷新/登出 + refresh token 旋转
- `RBAC + scope`：`user_roles` 支持 PLATFORM / SPACE 作用域
- Trust Level：`users.trust_level` 已预留，后续可用来放开“联系请求/发资源/建活动”等权限

## 下一步（Sprint 2）
- 目录、个人主页、隐私开关
- 动态 Feed（时间线）
- 举报/拉黑 + 最小内容治理后台
