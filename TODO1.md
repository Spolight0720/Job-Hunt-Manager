# Job-Hunt-Manager TODO 列表 (V1.0)

根据 `技术文档v1.md` 规划的各个模块梳理出的开发代办清单。

## 1. 项目初始化与环境搭建
- [x] **后端项目搭建**：初始化 Spring Boot 2.7.x 项目脚手架（统一基于 Java 8 环境）。
- [x] **项目依赖引入**：配置 Maven `pom.xml` (包含 MyBatis-Plus, MySQL Driver, Redis, Spring Security/JWT, Swagger/SpringDoc)。
- [x] **环境配置**：配置 `application.yml`，连接 MySQL 8.0 数据库与 Redis 服务。
- [ ] **前端项目搭建**：初始化 Vue 3 (Composition API) + Element Plus 项目脚手架。
- [ ] **接口通信基础**：前端配置 Axios，封装基础请求配置与统一的 JWT 异常拦截。

## 2. 数据库设计与建表 (MySQL 8.0)
- [x] 创建 `sys_user` 表（用于存储基础账户及加密密码）。
- [x] 创建 `job_application` 表（岗位投递核心台账表）。
- [x] 创建 `interview_record` 表（面试记录与对应复盘结论表）。
- [x] 【安全】给相关的查询键（如 `user_id`, `application_id` 等）添加必要索引。

## 3. 后端开发：基础鉴权与控制层搭建 (RESTful API)
- [x] **数据隔离拦截**：编写 AOP/拦截器，从请求头的 JWT 中解析并把 `user_id` 存入 ThreadLocal 中，确保所有业务请求带入对应用户的 ID 杜绝越权。
### 3.1 认证与用户模块
- [x] `POST /api/v1/auth/register`: 实现账号注册与 BCrypt 密码加密处理。
- [x] `POST /api/v1/auth/login`: 实现登录校验与下发 JWT Token 逻辑。
- [x] `GET /api/v1/user/info`: 配合 ThreadLocal 提取信息，返回当前登录用户的概要信息。
- [x] `POST /api/v1/user/clear-data`: 按照用户 ID，清空旗下所有台账及复盘数据。

### 3.2 岗位台账模块 (Job Application)
- [x] `POST /api/v1/applications`: 新建投递，**需切入防重校验（同user_id、同company_name、同job_title、投递时间90天之内）**，触犯抛出 `RepeatApplicationException`。
- [x] `PUT /api/v1/applications/{id}`: 编辑单个岗位详情（必须鉴权 `user_id` 归属）。
- [x] `DELETE /api/v1/applications/{id}`: 执行软删除。
- [x] `PUT /api/v1/applications/{id}/status`: 实现单一核心字段快捷改签的状态流转。
- [x] `GET /api/v1/applications`: 实现包含关键词搜素、状态过滤、分页、和时间排序的台账列表查询。
- [ ] `POST /api/v1/applications/import`: 解析上传的 Excel，并进行批量数据的插入。
- [ ] `GET /api/v1/applications/export`: 根据当前过滤参数，导出并返回 Excel 文件流。

### 3.3 面试复盘与提醒模块 (Interview & Reminder)
- [x] `POST /api/v1/interviews`: 新增笔试/面试记录、失败原因及复盘总结，需关联 `application_id`。
- [x] `GET /api/v1/interviews/application/{id}`: 查询一条投递下所有的笔试与面试流转轨迹记录。
- [ ] `GET /api/v1/reminders/upcoming`: 检索日程开始时间在近期（如未来 3 天内）的未过期记录并返回。
- [ ] `GET /api/v1/reminders/timeout`: 获取处于流转状态中，且距离最后一次 `update_time` 更新已超过 7-14 天的心跳停止的超时岗位。

### 3.4 极简数据统计模块 (Statistics)
- [ ] `GET /api/v1/statistics/summary`: 统计总投递数与各主要状态数值分布。
- [ ] `GET /api/v1/statistics/funnel`: 开发漏斗计算（简历初筛率 -> 笔面试转化率 -> Offer转化率）。
- [ ] `GET /api/v1/statistics/channels`: 统计各招聘渠道 `channel` 下的投递分布占比情况。

## 4. 前端界面开发
- [ ] **系统鉴权与结构**：实现路由级拦截、开发基础的侧边栏结构与全局导航头。
- [ ] **登录注册页**：实现包含表单校验的基础鉴权页面。
- [ ] **仪表盘面板 (Dashboard)**：
  - 调用极简数据统计 API，渲染漏斗图与饼图；
  - 加入 Upcoming 面试提醒看板；
  - 加入处于 Timeout 状态卡住无回复的岗位展示。
- [ ] **投递台账管理页**：核心增删改查页面；对表格加入状态标记筛选、以及通过弹窗/行内快捷更改状态的功能；对接 Excel 批量导入导出测试。
- [ ] **面试节点与复盘管理 Drawer/Modal**：从台账列表中呼出对应岗位的抽屉，展示完整流转节点，且支持长文本内容（核心题目记录与失败教训复盘输入）。防重提交告警 UI 适配处理。

## 5. 测试与部署准备
- [ ] 后端通过 `http://localhost:8080/swagger-ui.html` 完成完整的 API 黑盒测试和越权安全自测。
- [ ] Vue 项目执行 `npm run build`，后端执行 `mvn clean package`。
- [ ] 配置 Nginx 做 `dist` 代理以及 `/api` 请求到本地 8080 的转发配置。
