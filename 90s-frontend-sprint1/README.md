# 90’s 校友网 - Sprint 1 前端 Demo（React）

包含 3 个最小页面：
- 登录/注册 `/auth`
- 加入空间（邀请码）`/join`
- 个人主页 `/me`（昵称、邮箱/手机号、trust_level、已加入空间列表）

并包含：
- Axios 拦截器：401 自动 refresh 并重试
- 路由守卫：未登录跳转 `/auth`；已登录但没有空间跳转 `/join`

## 运行

1) 安装依赖
```bash
npm i
```

2) 配置后端地址（可选）
复制 `.env.example` 为 `.env`，并修改：
```bash
VITE_API_BASE_URL=http://localhost:8080
```

3) 启动
```bash
npm run dev
```

访问：
- http://localhost:5173/auth

## 需要后端提供的接口（与 Sprint 1 后端 Demo 对齐）

- POST `/api/auth/register`  { identifier, nickname, password } -> { access_token, refresh_token }
- POST `/api/auth/login`     { identifier, password } -> { access_token, refresh_token }
- POST `/api/auth/refresh`   { refresh_token } -> { access_token, refresh_token }
- POST `/api/auth/logout`    {}
- GET  `/api/auth/me`        -> { id, nickname, email?, phone?, trust_level, spaces: [{id,name,membership_status}] }

- POST `/api/spaces/join-by-code` { code } -> 200

> Demo 里 refresh_token 存 localStorage。生产环境建议 refresh_token 用 httpOnly cookie 存储。

## 演示脚本（给合作伙伴）
1) 注册新用户
2) 登录后自动跳转 `/join`
3) 输入邀请码加入空间
4) 跳转 `/me`，展示用户信息与空间列表
5) 点 Logout 回到 `/auth`
