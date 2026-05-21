# Job-Hunt-Manager (求职投递与复盘管理系统)

这是一个轻量级的 Web 端应用，采用前后端分离架构，专注于核心的**求职岗位投递跟进**和**面试/笔试记录复盘**，以最简单、清晰的形式追踪个人秋招/春招求职进度。

## 🎯 核心功能 (Features)

- **🔐 账号与数据隔离**：支持多用户注册登录，基于 JWT 鉴权，用户的私有数据独立存储，杜绝垂直与水平越权。
- **📊 投递台账管理**：直观记录投递的公司、岗位名称、渠道及当前流转状态（如“简历初筛”、“二面”、“Offer”等）。支持基于关键词和状态的高效查询管理。
- **⏳ 智能防重校验**：后端内置“90天内同一公司同岗位投递去重警告”拦截系统，拒绝海投时的冗余台账操作。
- **📝 面试复盘与追踪**：追踪单条岗位的全生命周期轨迹，记录多轮次节点核心问题与失败教训（MVP阶段内置台账联动追踪支持）。

---

## 🛠 技术栈介绍 (Tech Stack)

### 后端 (Backend)
- **核心框架**：Java 8, Spring Boot 2.7.x
- **持久化层**：MyBatis-Plus
- **数据库**：MySQL 8.0, Redis (默认挂载)
- **安全与认证**：轻量级拦截器 + ThreadLocal 数据沙箱隔离 + JWT Token + BCrypt 密码加密

### 前端 (Frontend)
- **核心框架**：Vue 3 (Composition API `setup` 语法糖)
- **类型支持 / 构建工具**：TypeScript, Vite
- **UI 组件库**：Element-Plus
- **路由与请求**：Vue Router 4, Axios (含全局防漏拦截)

---

## 🚀 快速启动指南 (Quick Start)

### 1. 数据库准备
- 打开 MySQL，创建名为 `job_hunt_manager` 的空数据库（字符集推荐 `utf8mb4`）。
- 将本项目根目录 `sql/init.sql` 的内容导入执行。它会自动完成核心用户表、台账表与复盘表的构建并带好索引。
- 确保本地或远端有一个可用的 Redis。

### 2. 启动后端 (Spring Boot)
为了保障代码开源上传的安全性，本系统采用了**环境变量注入**的方式来传递隐私密码。你在启动项目时需要将你自己的密码传入应用。
```bash
cd backend
mvn clean package -DskipTests

# 通过启动参数注入密码
java -jar target/job-hunt-manager-backend-1.0.0-SNAPSHOT.jar \
  --DB_PASSWORD=你的数据库密码 \
  --REDIS_PASSWORD=你的Redis密码
```
*(💡 开发期提示：如果你在 IDEA 中，请直接在此 Application 启动项的 "Environment Variables / Program Arguments" 区域中填配 `$DB_PASSWORD`)*
若看到控制台输出 `JobHuntManagerApplication Started`，表示后端及数据库连接就绪，API 将暴露于 `http://localhost:8080` 端口。

### 3. 启动前端 (Vite + Vue)
前端开发需具备 Node.js 运行环境 (推荐 18+)。
```bash
cd frontend

# 安装项目依赖
npm install

# 启动本地热更新开发服务器
npm run dev
```
按照终端给出的提示（通常为 `http://localhost:5173/`），在浏览器打开后即可完成全部功能的访问与使用。

---

## 📜 维护说明
- 本仓库为 1.0 MVP 落地版，已精简了开发前期的 `PRD` 及技术脑图文件，专注于纯粹的可交付代码。基于个人需求进行修改极易拓展。
