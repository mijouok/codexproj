# 90s Alumni Demo Frontend

React + Vite frontend for the alumni demo.

## Pages

- `/auth` - login and register
- `/me` - my profile, status, wall, friends, friend requests
- `/users/:userId` - classmate profile

## Run

```bash
npm i
npm run dev
```

Default dev URL: `http://localhost:5173`

The Vite dev server proxies `/api` to `http://localhost:8080`.

## Main API Dependencies

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
