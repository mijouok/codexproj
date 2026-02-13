# PAC Spring Boot 本地服务器（Windows 11）

## 你得到什么
- 一个最小的 Spring Boot Web 服务：提供 `http://127.0.0.1:8899/proxy.pac`
- 一个已配置好的 `proxy.pac`（HTTP 代理端口：1082），包含：
  - 11 对战平台域名直连
  - KK 对战平台域名直连
  - 常用国内站点直连
  - 其它默认走代理（失败再直连兜底）

## 目录
- `proxy.pac`：PAC 文件（请复制到 `C:\pac\proxy.pac`，或修改 `application.yml` 的 pac.file）
- `src/...`：Spring Boot 服务源码
- `pom.xml`：Maven 项目

## 1) 放置 PAC 文件
1. 创建目录：`C:\pac\`
2. 将本项目根目录的 `proxy.pac` 复制到：`C:\pac\proxy.pac`

> 如果你想换路径：修改 `src/main/resources/application.yml` 里的 `pac.file`

## 2) 启动服务
在项目根目录执行：

```powershell
mvn spring-boot:run
```

检查：
- `http://127.0.0.1:8899/health` 应返回 `OK`
- `http://127.0.0.1:8899/proxy.pac` 应显示 PAC 内容

## 3) Windows 11 启用 PAC
路径：
设置 → 网络和 Internet → 代理 → 自动代理设置 → 使用设置脚本（开）

脚本地址填写：
`http://127.0.0.1:8899/proxy.pac`

如果改了 PAC 不生效，可用版本参数：
`http://127.0.0.1:8899/proxy.pac?v=1`（每次改就 v+1）

## 4) 修改代理端口
编辑 `C:\pac\proxy.pac` 内这行：

`var PROXY = "PROXY 127.0.0.1:1082";`

改成你的 HTTP 代理端口即可。
