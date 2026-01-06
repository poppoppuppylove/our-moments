<div align="center">
  <h1> Our Moments </h1>

  <!-- 徽章行统一间距，视觉更整齐 -->
  <div style="display: flex; justify-content: center; gap: 8px; flex-wrap: wrap; margin: 16px 0;">
    <img src="https://img.shields.io/badge/Our%20Moments-v1.0-89A9C3?style=for-the-badge" alt="Our Moments Logo">
    <img src="https://img.shields.io/badge/Spring%20Boot-3.3.0-6DB33F?style=for-the-badge&logo=springboot" alt="Spring Boot">
    <img src="https://img.shields.io/badge/Vue-3.5-4FC08D?style=for-the-badge&logo=vue.js" alt="Vue 3">
    <img src="https://img.shields.io/badge/TypeScript-5.9-3178C6?style=for-the-badge&logo=typescript" alt="TypeScript">
  </div>

  <!-- 标语增强字体和间距，突出主题 -->
  <p style="font-size: 14px; color: #666; line-height: 1.6; margin: 8px 0; font-weight: 500;">
    一个充满手绘质感的生活记录空间
  </p>
  <p style="font-size: 12px; color: #888; margin: 0 0 24px 0;">
    记录生活的点滴，留住美好的瞬间
  </p>
</div>

---

## 目录

1. [项目简介](#项目简介)
2. [功能特性](#功能特性)
    - [内容创作与分享](#📝-内容创作与分享)
    - [社交互动](#👥-社交互动)
    - [视觉体验](#🎨-视觉体验)
    - [技术功能](#🔧-技术功能)
    - [⚙后台管理](#⚙️-后台管理)
    - [可见性控制](#🔧-可见性控制)
    - [通知系统](#👥--通知系统)
3. [技术栈](#技术栈)
    - [后端技术栈](#后端技术栈)
    - [前端技术栈](#前端技术栈)
4. [项目结构](#项目结构)
5. [快速开始](#快速开始)
    - [环境要求](#环境要求)
    - [后端启动](#后端启动)
    - [前端启动](#前端启动)
    - [构建生产版本](#构建生产版本)
6. [API 文档](#api-文档)
7. [联系方式](#联系方式)
---

## 项目简介

**Our Moments** 是一个独特的社交博客平台，采用手绘拼贴风格的设计理念。它不仅仅是一个博客网站，更像是一个线上的"拼贴手账"。用户可以像在纸质手账上一样，自由记录文字、图片，装饰自己的生活记忆，与朋友分享美好时刻。

### 核心愿景

- **手绘质感**：模拟真实的纸质手账体验，边框、贴纸、胶带都带有手绘的温暖感
- **自由拼贴**：图片和便签可以像贴纸一样自由摆放，带有自然的角度倾斜和层叠效果
- **低饱和配色**：采用柔和的自然色系，营造温馨舒适的视觉体验
- **社交互动**：支持好友系统、私信、评论等功能，让分享更有温度

---

## 功能特性

###  内容创作与分享

- **富文本编辑器**：支持多种格式的内容创作，文字与图片自由组合
- **内联图片插入**：在文章中任意位置插入图片，打造拼贴手账效果
- **标签与分类**：使用手绘标签牌的形式组织和展示内容
- **多重可见性设置**：公开、仅好友可见、私密、仅伴侣可见
- **元数据支持**：天气、心情、地点等生活记录元数据

###  社交互动

- **好友系统**：发送好友请求、验证好友关系、管理好友列表
- **私信聊天**：与好友进行实时私聊，WebSocket 架构保证即时性
- **评论互动**：在日志上留言，模拟便利贴样式的评论展示
- **通知中心**：实时接收评论、好友请求、新日志分享等通知
- **邮件提醒**：重要互动通过邮件通知，不错过任何精彩瞬间

###  视觉体验

- **拍立得风格卡片**：宽白边 + 下方留空的经典拍立得样式
- **胶带粘贴效果**：图片角落装饰半透明胶带，增加真实感
- **纸张纹理背景**：纸质背景，增强质感
- **随机旋转角度**：每张照片都有轻微的自然倾斜，模拟手账粘贴效果
- **手绘边框**：不规则的手绘风格边框，避免机械化外观
- **动态视差背景**：鼠标移动时背景产生微妙的视差效果

###  技术功能

- **JWT 身份认证**：安全的令牌认证机制，支持用户和管理员角色
- **阿里云 OSS 存储**：图片上传至云端，支持大文件和高并发访问
- **自动图片压缩**：上传时的图片优化，提升加载速度
- **懒加载优化**：图片延迟加载，优化首屏渲染性能
- **筛选功能**：按标签、心情、日期筛选日志，快速找到想要的内容
- **WebSocket 实时通信**：私信和通知的即时推送

###  后台管理

- **内容管理**：查看、编辑、删除用户发布的内容
- **用户管理**：管理用户账户和权限
- **评论审核**：管理和审核评论内容
- **API 文档**：自动生成的 Swagger UI 接口文档

###  可见性控制

四种可见性级别：

| 可见性                       | 说明 | 场景 |
|---------------------------|-----|------|
| PUBLIC                    | 公开 | 所有人可见的日常分享 |
| FRIENDS                   | 好友 | 个人隐私日记 |
| PRIVATE                   | 私密 | 完全个人记录 |
| PARTNER_ONLY(需要设置user_id) | 仅伴侣 | 情侣或亲密伙伴 |

###   通知系统

支持四种通知类型：

| 类型 | 说明 | 触发条件 |
|------|------|----------|
| COMMENT | 评论通知 | 有人评论你的日志 |
| FRIEND_REQUEST | 好友请求 | 收到新的好友请求 |
| NEW_POST | 新日志分享 | 好友发布新日志 |
| MESSAGE | 私信通知 | 收到新的私信 |

---
## 技术栈

### 后端技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| **Spring Boot** | 3.3.0 | 核心 Java Web 框架 |
| **Java** | 17 | 编程语言 |
| **Spring Security** | 3.3.0 | 安全框架，实现认证和授权 |
| **MyBatis** | 3.0.3 | ORM 框架，数据库持久层 |
| **MySQL Connector** | Latest | MySQL 数据库驱动 |
| **JWT** | 0.11.5 | JSON Web Token 令牌认证 |
| **Spring Mail** | 3.3.0 | 邮件发送服务 |
| **WebSocket** | 3.3.0 | 实时双向通信 |
| **SpringDoc OpenAPI** | 2.5.0 | API 文档自动生成 |
| **阿里云 OSS SDK** | 3.17.4 | 对象存储服务 |
| **Lombok** | Latest | 简化 Java 代码 |
| **Maven** | 3.x | 项目构建工具 |

### 前端技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| **Vue** | 3.5.24 | 渐进式 JavaScript 框架 |
| **TypeScript** | 5.9.3 | 超集 JavaScript，添加类型系统 |
| **Vite** | 7.2.4 | 下一代前端构建工具 |
| **Vue Router** | 4.6.4 | 官方路由管理器 |
| **Pinia** | 3.0.4 | 状态管理库 |
| **Axios** | 1.13.2 | HTTP 请求库 |
| **SCSS** | 1.97.0 | CSS 预处理器 |
| **TailwindCSS** | 4.1.18 | 原子化 CSS 框架 |
| **STOMP** | 7.2.1 | WebSocket 消息协议 |
| **SockJS** | 1.6.1 | WebSocket 降级兼容 |

---

## 项目结构

```
our-moments/
├── our-moments/                    # 后端项目
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/gravity/ourmoments/
│   │   │   │   ├── config/        # 配置类（安全、CORS、WebSocket、OSS 等）
│   │   │   │   ├── controller/    # 控制器层（REST API）
│   │   │   │   ├── service/       # 服务层（业务逻辑）
│   │   │   │   ├── mapper/        # 数据访问层（MyBatis）
│   │   │   │   ├── entity/        # 实体类（数据模型）
│   │   │   │   ├── dto/           # 数据传输对象
│   │   │   │   ├── vo/            # 视图对象
│   │   │   │   ├── exception/     # 自定义异常
│   │   │   │   ├── interceptor/   # 拦截器（JWT 验证）
│   │   │   │   ├── websocket/     # WebSocket 配置和处理器
│   │   │   │   └── util/          # 工具类
│   │   │   └── resources/
│   │   │       ├── mapper/        # MyBatis XML 映射文件
│   │   │       ├── static/        # 静态资源
│   │   │       ├── templates/     # 模板文件
│   │   │       └── application*.properties # 配置文件
│   │   └── test/                  # 测试代码
│   └── pom.xml                    # Maven 项目配置
│
├── our-moments-frontend/           # 前端项目
│   ├── public/                     # 公共静态资源
│   ├── src/
│   │   ├── assets/                 # 资源文件（图片、字体等）
│   │   │   ├── images/            # 图片资源
│   │   │   └── styles/            # 全局样式
│   │   ├── components/             # Vue 组件
│   │   │   ├── base/              # 基础组件（按钮、卡片、输入框等）
│   │   │   ├── common/            # 通用组件（页脚、通知等）
│   │   │   ├── decorative/        # 装饰组件（胶带、图钉、纸张纹理等）
│   │   │   ├── notifications/     # 通知相关组件
│   │   │   ├── messages/          # 消息相关组件
│   │   │   └── posts/             # 帖子相关组件
│   │   ├── composables/           # 组合式函数（可复用逻辑）
│   │   ├── router/                # 路由配置
│   │   ├── store/                 # Pinia 状态管理
│   │   ├── types/                 # TypeScript 类型定义
│   │   ├── api/                   # API 请求封装
│   │   └── views/                 # 页面组件
│   │       ├── home/              # 首页
│   │       ├── auth/              # 认证相关页面
│   │       ├── post/              # 日志相关页面
│   │       ├── friends/           # 好友页面
│   │       ├── notifications/     # 通知中心
│   │       ├── chat/              # 私信聊天
│   │       └── admin/             # 后台管理
│   ├── index.html                 # HTML 入口
│   ├── vite.config.ts             # Vite 配置
│   ├── tsconfig.json              # TypeScript 配置
│   ├── tailwind.config.js         # TailwindCSS 配置
│   └── package.json               # npm 项目配置
│
├── roadmap_and_design.md          # 项目设计文档
└── README.md                      # 项目说明文档
```

---

## 快速开始

### 环境要求

- **Java**: JDK 17+
- **Node.js**: 18.x 或 20.x
- **MySQL**: 8.0+
- **Maven**: 3.6+
- **IDE**: IntelliJ IDEA 或 VS Code（推荐）

### 后端启动

1. **克隆项目**
   ```bash
   git clone https://github.com/poppoppuppylove/our-moments.git
   cd our-moments/our-moments
   ```

2. **配置数据库**
``````
详见data.sql和db_schema.sql
``````
3. **修改配置文件**

   编辑 `src/main/resources/application.properties`，配置数据库连接和阿里云 OSS：

   ```properties
   # 数据库配置
   spring.datasource.url=jdbc:mysql://localhost:3306/our_moments
   spring.datasource.username=your_username
   spring.datasource.password=your_password

   # JWT 配置
   jwt.secret=your-secret-key
   jwt.expiration=86400000

   # 阿里云 OSS 配置
   oss.accessKeyId=your-access-key-id
   oss.accessKeySecret=your-access-key-secret
   oss.bucketName=your-bucket-name
   oss.endpoint=your-oss-endpoint

   # 邮件配置
   spring.mail.host=smtp.example.com
   spring.mail.port=
   spring.mail.username=your-email@example.com
   spring.mail.password=your-password
   ```

4. **初始化数据库**

5. **运行项目**
   ```bash
   # 使用 Maven
   mvn spring-boot:run

   # 或使用 IDE 运行主类
   # com.gravity.ourmoments.OurMomentsApplication
   ```

6. **验证启动**

   访问 `http://localhost:8080` 和 API 文档 `http://localhost:8080/swagger-ui.html`

### 前端启动

1. **进入前端目录**
   ```bash
   cd our-moments-frontend
   ```

2. **安装依赖**
   ```bash
   npm install
   ```

3. **配置 API 地址**

   编辑 `src/api/request.ts`，配置后端 API 基础地址：
   ```typescript
   const BASE_URL = 'http://localhost:8080/api'
   ```

4. **启动开发服务器**
   ```bash
   npm run dev
   ```

5. **访问应用**

   打开浏览器访问 `http://localhost:3000`

### 构建生产版本

**后端打包**
```bash
cd our-moments/our-moments
mvn clean package
# 生成的 JAR 文件位于 target/our-moments-0.0.1-SNAPSHOT.jar
java -jar target/our-moments-0.0.1-SNAPSHOT.jar
```

**前端打包**
```bash
cd our-moments-frontend
npm run build
# 构建产物位于 dist/ 目录
```

---

## API 文档

项目集成了 Swagger UI，启动后端服务后访问：

**Swagger UI**: `http://localhost:8080/swagger-ui.html`

包含所有 REST API 的详细说明、请求/响应示例和在线测试功能。

---


## 联系方式

- 项目主页: [GitHub Repository](https://github.com/poppoppuppylove/our-moments)
- 提交问题: [GitHub Issues](https://github.com/poppoppuppylove/our-moments/issues)
- 邮箱: lastonean@163.com

---


<div align="center">

**最后，感谢我的好蛋老大**


© 2026 Our Moments. All rights reserved.

</div>