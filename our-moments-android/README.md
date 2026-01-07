# Our Moments Android App

这是Our Moments社交博客平台的Android客户端应用，与Web端保持一致的功能和设计风格。

## 项目概述

Our Moments是一个采用手绘拼贴风格设计的社交博客平台，用户可以记录生活点滴，分享美好时光。

## 技术栈

- **语言**: Kotlin
- **UI框架**: Jetpack Compose
- **架构**: MVVM + Clean Architecture
- **网络**: Retrofit + OkHttp
- **依赖注入**: Hilt
- **图片加载**: Coil
- **异步**: Coroutines + Flow

## 开发计划

详细的开发计划请查看 [ANDROID_DEVELOPMENT_PLAN.md](ANDROID_DEVELOPMENT_PLAN.md)

## 目录结构

```
app/
├── src/main/java/com/gravity/ourmoments/
│   ├── data/          # 数据层
│   ├── domain/        # 领域层
│   ├── presentation/  # 表现层
│   └── core/          # 核心模块
└── src/main/res/      # 资源文件
```

## 快速开始

1. 克隆项目
2. 使用Android Studio打开项目
3. 配置后端服务器地址
4. 运行应用

## 设计特色

- 手绘质感UI设计
- 拍立得风格卡片
- 胶带和图钉装饰元素
- 纸张纹理背景
- 温暖的低饱和度配色

## 功能模块

- [ ] 用户认证（登录/注册）
- [ ] 文章发布和浏览
- [ ] 好友系统
- [ ] 私信聊天
- [ ] 通知中心
- [ ] 个人资料管理
- [ ] 管理员后台

## 开发阶段

按照 [ANDROID_DEVELOPMENT_PLAN.md](ANDROID_DEVELOPMENT_PLAN.md) 中的计划分阶段开发。

## 贡献

请遵循项目的开发计划和代码规范。

## 许可证

待定