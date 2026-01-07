# Our Moments Android App 开发计划

## 项目概述

### 1.1 项目目标
开发一个与Web端功能一致、设计风格相近的原生Android应用，提供手绘质感的用户体验。

### 1.2 核心设计理念
- **保持一致**: 功能、UI/UX与Web端完全一致
- **手绘质感**: 融入Material Design，保留手绘拼贴风格
- **原生体验**: 充分利用Android原生特性
- **性能优先**: 流畅的用户交互和快速响应

### 1.3 项目技术栈

#### 核心技术
- **语言**: Kotlin 1.9+
- **构建**: Gradle 8.0+
- **最低版本**: Android 7.0 (API 24)
- **目标版本**: Android 14 (API 34)

#### 核心依赖库
```kotlin
// UI框架
implementation("androidx.compose.ui:ui:1.5.4")
implementation("androidx.compose.material3:material3:1.1.2")
implementation("androidx.activity:activity-compose:1.8.0")

// 架构组件
implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2")
implementation("androidx.navigation:navigation-compose:2.7.5")

// 依赖注入
implementation("com.google.dagger:hilt-android:2.48")
kapt("com.google.dagger:hilt-compiler:2.48")

// 网络
implementation("com.squareup.retrofit2:retrofit:2.9.0")
implementation("com.squareup.retrofit2:converter-moshi:2.9.0")
implementation("com.squareup.okhttp3:okhttp:4.11.0")
implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")

// 图片加载
implementation("io.coil-kt:coil-compose:2.5.0")

// WebSocket
implementation("com.squareup.okhttp3:okhttp-ws:4.11.0")

// 本地存储
implementation("androidx.datastore:datastore-preferences:1.0.0")

// 异步
implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")

// 分页
implementation("androidx.paging:paging-compose:3.2.1")
```

---

## 二、项目架构

### 2.1 MVVM分层架构

```
app/
├── src/main/java/com/gravity/ourmoments/
│   ├── data/                          # 数据层
│   │   ├── remote/                   # 远程数据源
│   │   │   ├── api/                  # API接口
│   │   │   │   ├── ApiService.kt
│   │   │   │   ├── AuthApi.kt
│   │   │   │   ├── PostApi.kt
│   │   │   │   ├── UserApi.kt
│   │   │   │   ├── FriendshipApi.kt
│   │   │   │   ├── MessageApi.kt
│   │   │   │   └── NotificationApi.kt
│   │   │   ├── dto/                  # 数据传输对象
│   │   │   │   ├── UserDto.kt
│   │   │   │   ├── PostDto.kt
│   │   │   │   ├── CommentDto.kt
│   │   │   │   ├── FriendshipDto.kt
│   │   │   │   ├── MessageDto.kt
│   │   │   │   ├── NotificationDto.kt
│   │   │   │   └── ApiResponse.kt
│   │   │   ├── okhttp/               # OkHttp配置
│   │   │   │   ├── NetworkModule.kt
│   │   │   │   ├── TokenInterceptor.kt
│   │   │   │   └── LoggingInterceptor.kt
│   │   │   └── websocket/            # WebSocket
│   │   │       ├── ChatWebSocket.kt
│   │   │       └── WebSocketManager.kt
│   │   ├── local/                    # 本地数据源
│   │   │   ├── database/             # Room数据库
│   │   │   │   ├── AppDatabase.kt
│   │   │   │   ├── UserDao.kt
│   │   │   │   └── PostDao.kt
│   │   │   └── datastore/            # DataStore存储
│   │   │       ├── UserDataStore.kt
│   │   │       └── PreferencesDataStore.kt
│   │   ├── mapper/                   # 数据映射
│   │   │   ├── UserMapper.kt
│   │   │   ├── PostMapper.kt
│   │   │   └── CommentMapper.kt
│   │   └── repository/               # 仓库层
│   │       ├── UserRepository.kt
│   │       ├── AuthRepository.kt
│   │       ├── PostRepository.kt
│   │       ├── CommentRepository.kt
│   │       ├── FriendshipRepository.kt
│   │       ├── MessageRepository.kt
│   │       └── NotificationRepository.kt
│   ├── domain/                        # 领域层
│   │   ├── model/                    # 业务模型
│   │   │   ├── User.kt
│   │   │   ├── Post.kt
│   │   │   ├── Comment.kt
│   │   │   ├── Friendship.kt
│   │   │   ├── Message.kt
│   │   │   ├── Notification.kt
│   │   │   └── UiState.kt
│   │   ├── usecase/                  # 用例
│   │   │   ├── auth/
│   │   │   │   ├── LoginUseCase.kt
│   │   │   │   ├── RegisterUseCase.kt
│   │   │   │   └── LogoutUseCase.kt
│   │   │   ├── post/
│   │   │   │   ├── GetPostsUseCase.kt
│   │   │   │   ├── GetPostUseCase.kt
│   │   │   │   ├── CreatePostUseCase.kt
│   │   │   │   └── DeletePostUseCase.kt
│   │   │   ├── user/
│   │   │   │   ├── GetUserUseCase.kt
│   │   │   │   ├── UpdateProfileUseCase.kt
│   │   │   │   └── UploadAvatarUseCase.kt
│   │   │   ├── friendship/
│   │   │   │   ├── GetFriendsUseCase.kt
│   │   │   │   ├── SendFriendRequestUseCase.kt
│   │   │   │   └── AcceptFriendRequestUseCase.kt
│   │   │   ├── message/
│   │   │   │   ├── SendMessageUseCase.kt
│   │   │   │   ├── GetChatHistoryUseCase.kt
│   │   │   │   └── GetUnreadMessagesUseCase.kt
│   │   │   └── notification/
│   │   │       ├── GetNotificationsUseCase.kt
│   │   │       └── MarkAsReadUseCase.kt
│   │   └── repository/               # 仓库接口
│   │       ├── UserRepository.kt
│   │       └── PostRepository.kt
│   ├── presentation/                 # 表现层
│   │   ├── ui/                       # UI页面
│   │   │   ├── home/                 # 首页
│   │   │   │   ├── HomeScreen.kt
│   │   │   │   └── HomeViewModel.kt
│   │   │   ├── auth/                 # 认证
│   │   │   │   ├── LoginScreen.kt
│   │   │   │   ├── LoginViewModel.kt
│   │   │   │   ├── RegisterScreen.kt
│   │   │   │   └── RegisterViewModel.kt
│   │   │   ├── post/                 # 文章
│   │   │   │   ├── PostDetailScreen.kt
│   │   │   │   ├── PostDetailViewModel.kt
│   │   │   │   ├── PostCreateScreen.kt
│   │   │   │   └── PostCreateViewModel.kt
│   │   │   ├── friends/              # 好友
│   │   │   │   ├── FriendsListScreen.kt
│   │   │   │   ├── FriendsListViewModel.kt
│   │   │   │   ├── AddFriendScreen.kt
│   │   │   │   └── friendRequests/
│   │   │   │       └── FriendRequestsScreen.kt
│   │   │   ├── chat/                 # 聊天
│   │   │   │   ├── ChatListScreen.kt
│   │   │   │   ├── ChatListViewModel.kt
│   │   │   │   ├── ChatScreen.kt
│   │   │   │   └── ChatViewModel.kt
│   │   │   ├── notification/         # 通知
│   │   │   │   ├── NotificationCenter.kt
│   │   │   │   ├── NotificationScreen.kt
│   │   │   │   └── NotificationViewModel.kt
│   │   │   ├── profile/              # 个人资料
│   │   │   │   ├── ProfileScreen.kt
│   │   │   │   ├── ProfileViewModel.kt
│   │   │   │   └── EditProfileScreen.kt
│   │   │   └── admin/                # 管理员
│   │   │       ├── AdminDashboard.kt
│   │   │       └── PostManagement.kt
│   │   ├── theme/                    # 主题配置
│   │   │   ├── Color.kt              # 颜色定义
│   │   │   ├── Type.kt               # 字体
│   │   │   ├── Theme.kt              # 主题配置
│   │   │   └── DesignSystem.kt       # 设计系统
│   │   ├── component/                # 可复用组件
│   │   │   ├── base/                 # 基础组件
│   │   │   │   ├── HandButton.kt     # 手绘按钮
│   │   │   │   ├── HandInput.kt      # 手绘输入框
│   │   │   │   ├── HandCard.kt       # 手绘卡片
│   │   │   │   ├── HandTextField.kt  # 手绘文本框
│   │   │   │   ├── HandLoader.kt     # 加载动画
│   │   │   │   └── HandToast.kt      # 提示消息
│   │   │   ├── decorative/           # 装饰组件
│   │   │   │   ├── Tape.kt           # 胶带
│   │   │   │   ├── Pin.kt            # 图钉
│   │   │   │   ├── PaperTexture.kt   # 纸张纹理
│   │   │   │   └── HandBorder.kt     # 手绘边框
│   │   │   ├── post/                 # 文章相关组件
│   │   │   │   ├── PostCard.kt       # 文章卡片
│   │   │   │   ├── PostItem.kt       # 文章列表项
│   │   │   │   ├── CommentItem.kt    # 评论项
│   │   │   │   └── ImageViewer.kt    # 图片查看器
│   │   │   ├── message/              # 消息相关组件
│   │   │   │   ├── MessageBubble.kt  # 消息气泡
│   │   │   │   ├── MessageInput.kt   # 消息输入框
│   │   │   │   ├── ChatItem.kt       # 聊天列表项
│   │   │   │   └── AttachmentPanel.kt # 附件面板
│   │   │   └── common/               # 通用组件
│   │   │       ├── TopBar.kt         # 顶部栏
│   │   │       ├── BottomBar.kt      # 底部栏
│   │   │       ├── EmptyState.kt     # 空状态
│   │   │       └── ErrorState.kt     # 错误状态
│   │   └── navigation/               # 导航配置
│   │       ├── Screen.kt             # 屏幕定义
│   │       ├── NavGraph.kt           # 导航图
│   │       └── NavigationAnimations.kt # 导航动画
│   ├── core/                          # 核心模块
│   │   ├── di/                        # 依赖注入
│   │   │   ├── AppModule.kt
│   │   │   ├── NetworkModule.kt
│   │   │   ├── DatabaseModule.kt
│   │   │   └── RepositoryModule.kt
│   │   ├── util/                      # 工具类
│   │   │   ├── DateUtil.kt           # 日期格式化
│   │   │   ├── ImageUtil.kt          # 图片处理
│   │   │   ├── StyleUtil.kt          # 手绘风格工具
│   │   │   ├── HandDrawnUtil.kt      # 手绘绘制工具
│   │   │   ├── RandomUtil.kt         # 随机工具
│   │   │   └── ValidationUtil.kt     # 验证工具
│   │   ├── constant/                  # 常量定义
│   │   │   ├── AppConstants.kt
│   │   │   ├── ApiConstants.kt
│   │   │   ├── DesignConstants.kt
│   │   │   └── ErrorMessages.kt
│   │   ├── result/                    # Result封装
│   │   │   └── Result.kt
│   │   ├── exception/                 # 异常处理
│   │   │   └── AppExceptionHandler.kt
│   │   └── model/                     # 核心模型
│   │       └── Resource.kt           # 资源状态
│   └── MainActivity.kt                # 主Activity
├── res/                               # 资源文件
│   ├── drawable/                      # 图片资源
│   │   ├── ic_launcher_foreground.xml
│   │   ├── ic_launcher_background.xml
│   │   ├── tape_yellow_drawable.xml
│   │   ├── tape_pink_drawable.xml
│   │   ├── tape_blue_drawable.xml
│   │   ├── tape_green_drawable.xml
│   │   ├── tape_purple_drawable.xml
│   │   ├── pin_red_drawable.xml
│   │   ├── pin_blue_drawable.xml
│   │   ├── paper_texture.xml
│   │   └── bg_home_*.xml
│   ├── values/                         # 值资源
│   │   ├── strings.xml
│   │   ├── colors.xml
│   │   ├── dimens.xml
│   │   └── themes.xml
│   ├── font/                          # 字体文件
│   │   ├── ma_shan_zheng.ttf
│   │   ├── lxgw_wenkai.ttf
│   │   └── noto_serif_sc.ttf
│   └── mipmap-*/                       # 应用图标
├── proguard-rules.pro                 # ProGuard规则
└── build.gradle.kts                    # 构建配置
```

---

## 三、UI/UX设计方案

### 3.1 手绘风格组件实现

#### 3.1.1 配色方案
参考web端的低饱和色系，定义 Material Design 3 颜色方案：

```kotlin
// Color.kt
val PaperTheme = lightColorScheme(
    // Material 3 标准颜色
    primary = Color(0xFF957DAD),              // soft-purple
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFE0BBE4),     // dusty-pink
    onPrimaryContainer = Color(0xFF4A4A4A),

    secondary = Color(0xFFD291BC),            // mauve
    onSecondary = Color(0xFFFFFFFF),

    tertiary = Color(0xFFFEC8D8),            // peach
    onTertiary = Color(0xFF4A4A4A),

    background = Color(0xFFF9F5F0),          // paper
    onBackground = Color(0xFF4A4A4A),        // ink-light

    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF4A4A4A),

    error = Color(0xFFBA1A1A),
    onError = Color(0xFFFFFFFF),

    // 自定义手绘风格颜色
    paperSurface = Color(0xFFF9F5F0),         // 纸张背景
    paperDark = Color(0xFFF0EAD6),            // 深色纸张
    kraftPaper = Color(0xFFD4C4A8),           // 牛皮纸
    watercolor = Color(0xFFF5E6D3),           // 水彩纸

    // 胶带颜色
    tapeYellow = Color(0xFFFFE8D1),
    tapeYellowAlpha = Color(0xFFFFE8D1).copy(alpha = 0.85f),
    tapePink = Color(0xFFFFC8D8),
    tapePinkAlpha = Color(0xFFFFC8D8).copy(alpha = 0.85f),
    tapeBlue = Color(0xFFADD8E6),
    tapeBlueAlpha = Color(0xFFADD8E6).copy(alpha = 0.85f),
    tapeGreen = Color(0xFFB5EAD7),
    tapeGreenAlpha = Color(0xFFB5EAD7).copy(alpha = 0.85f),
    tapePurple = Color(0xFFE0BBE4),
    tapePurpleAlpha = Color(0xFFE0BBE4).copy(alpha = 0.85f),

    // 图钉颜色
    pinRed = Color(0xFFDC143C),
    pinRedDark = Color(0xFFB22222),
    pinBlue = Color(0xFF1E90FF),
    pinBlueDark = Color(0xFF0000CD),
    pinGreen = Color(0xFF32CD32),
    pinGreenDark = Color(0xFF228B22),
    pinYellow = Color(0xFFFFD700),
    pinYellowDark = Color(0xFFDAA520),
    pinPurple = Color(0xFF9932CC),
    pinPurpleDark = Color(0xFF8B008B),

    // 墨水色
    inkLight = Color(0xFF4A4A4A),
    inkDark = Color(0xFF2C2C2C),
    inkFaded = Color(0xFF8C8C8C),

    // 点缀色
    blush = Color(0xFFFFDFD3),
    mint = Color(0xFFB5EAD7),

    // 阴影
    shadowWarm = Color(0x33D291BC).copy(alpha = 0.2f),
    shadowPaper = Color(0x334A4A4A).copy(alpha = 0.1f),
)

val DarkTheme = darkColorScheme(
    primary = Color(0xFFB794F4),
    onPrimary = Color(0xFF21005D),
    primaryContainer = Color(0xFF431E7F),
    onPrimaryContainer = Color(0xFFE6DDFF),

    secondary = Color(0xFFCEB8D8),
    onSecondary = Color(0xFF332D41),

    tertiary = Color(0xFFE0BDD5),
    onTertiary = Color(0xFF4A2546),

    background = Color(0xFF1A1A1A),          // 深色纸张
    onBackground = Color(0xFFE6E0E4),

    surface = Color(0xFF252525),
    onSurface = Color(0xFFE6E0E4),
)
```

#### 3.1.2 手绘边框组件
```kotlin
// HandBorder.kt
@Composable
fun HandBorder(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.inkLight,
    strokeWidth: Float = 1.dp,
    roughness: Float = 5f,
    content: @Composable BoxScope.() -> Unit
) {
    Box(modifier = modifier) {
        content()
        Canvas(
            modifier = Modifier.matchParentSize()
        ) {
            val density = LocalDensity.current
            val path = generateHandDrawnPath(
                size = size,
                roughness = with(density) { roughness.toPx() },
                strokeWidth = with(density) { strokeWidth.toPx() }
            )
            drawPath(
                path = path,
                color = color.copy(alpha = 0.3f),
                style = Stroke(width = with(density) { strokeWidth.toPx() })
            )
        }
    }
}

// 使用自定义Shape
class HandDrawnShape(
    private val cornerRadius: Dp,
    private val irregularity: Float = 0.15f
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = Path().apply {
            val r = with(density) { cornerRadius.toPx() }
            val irregular = size.width * irregularity

            // 不规则矩形路径
            moveTo(0f, Random.nextFloat() * irregular)

            // 右上角
            lineTo(size.width - Random.nextFloat() * irregular, 0f)
            lineTo(size.width, Random.nextFloat() * irregular)

            // 右下角
            lineTo(size.width, size.height - Random.nextFloat() * irregular)
            lineTo(size.width - Random.nextFloat() * irregular, size.height)

            // 左下角
            lineTo(Random.nextFloat() * irregular, size.height)
            lineTo(0f, size.height - Random.nextFloat() * irregular)

            close()
        }
        return Outline.Generic(path)
    }
}
```

#### 3.1.3 胶带装饰组件
```kotlin
// Tape.kt
@Composable
fun TapeDecoration(
    modifier: Modifier = Modifier,
    variant: TapeVariant = TapeVariant.YELLOW,
    position: TapePosition = TapePosition.TOP_RIGHT
) {
    val context = LocalContext.current
    val colors by remember { mutableStateOf(getTapeColors(variant)) }

    Box(
        modifier = modifier
            .size(80.dp, 24.dp)
            .graphicsLayer {
                // 根据位置计算旋转角度
                rotationZ = getTapeRotation(position)
            }
            .background(
                brush = Brush.linearGradient(
                    colors = colors,
                    start = Offset(0f, 0f),
                    end = Offset(size.width, size.height)
                ),
                shape = RoundedCornerShape(2.dp)
            )
            .alpha(0.85f)
    )
}

enum class TapeVariant {
    YELLOW, PINK, BLUE, GREEN, PURPLE
}

enum class TapePosition {
    TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT
}

private fun getTapeColors(variant: TapeVariant): List<Color> {
    return when (variant) {
        TapeVariant.YELLOW -> listOf(
            Color(0xFFFFE8D1).copy(alpha = 0.8f),
            Color(0xFFFFE4B5).copy(alpha = 0.6f)
        )
        TapeVariant.PINK -> listOf(
            Color(0xFFFFC8D8).copy(alpha = 0.7f),
            Color(0xFFFFB6C1).copy(alpha = 0.5f)
        )
        TapeVariant.BLUE -> listOf(
            Color(0xFFADD8E6).copy(alpha = 0.7f),
            Color(0xFF87CEEB).copy(alpha = 0.5f)
        )
        TapeVariant.GREEN -> listOf(
            Color(0xFFB5EAD7).copy(alpha = 0.7f),
            Color(0xFF98FB98).copy(alpha = 0.5f)
        )
        TapeVariant.PURPLE -> listOf(
            Color(0xFFE0BBE4).copy(alpha = 0.7f),
            Color(0xFFC8A2C8).copy(alpha = 0.5f)
        )
    }
}

private fun getTapeRotation(position: TapePosition): Float {
    return when (position) {
        TapePosition.TOP_LEFT -> -15f + Random.nextFloat() * 10f
        TapePosition.TOP_RIGHT -> 15f - Random.nextFloat() * 10f
        TapePosition.BOTTOM_LEFT -> -15f + Random.nextFloat() * 10f
        TapePosition.BOTTOM_RIGHT -> 15f - Random.nextFloat() * 10f
    }
}
```

#### 3.1.4 拍立得卡片组件
```kotlin
// HandCard.kt
@Composable
fun PolaroidCard(
    modifier: Modifier = Modifier,
    imageUrl: String?,
    title: String,
    content: String? = null,
    onClick: () -> Unit
) {
    // 随机旋转角度
    val randomRotation = remember { (-3.0..3.0).random().toFloat() }

    Card(
        onClick = onClick,
        modifier = modifier
            .graphicsLayer { rotationZ = randomRotation },
        shape = RoundedCornerShape(2.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp,
            pressedElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(
                start = 12.dp,
                end = 12.dp,
                top = 12.dp,
                bottom = 48.dp  // 底部留空模拟拍立得样式
            )
        ) {
            // 图片区域
            if (imageUrl != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .background(Color.Gray)
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(imageUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 胶带装饰（随机位置）
            TapeDecoration(
                variant = (0..4).random().let {
                    when (it) {
                        0 -> TapeVariant.YELLOW
                        1 -> TapeVariant.PINK
                        2 -> TapeVariant.BLUE
                        3 -> TapeVariant.GREEN
                        else -> TapeVariant.PURPLE
                    }
                },
                position = (0..3).random().let {
                    when (it) {
                        0 -> TapePosition.TOP_LEFT
                        1 -> TapePosition.TOP_RIGHT
                        2 -> TapePosition.BOTTOM_LEFT
                        else -> TapePosition.BOTTOM_RIGHT
                    }
                }
            )

            // 标题
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontFamily = FontFamily(Font(R.font.ma_shan_zheng)),
                color = MaterialTheme.colorScheme.inkLight,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            // 内容摘要
            if (content != null) {
                Text(
                    text = content,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.inkFaded,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    fontFamily = FontFamily(Font(R.font.lxgw_wenkai))
                )
            }
        }
    }
}
```

#### 3.1.5 手绘按钮组件
```kotlin
// HandButton.kt
@Composable
fun HandButton(
    text: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: HandButtonVariant = HandButtonVariant.Primary,
    size: HandButtonSize = HandButtonSize.Medium,
    enabled: Boolean = true,
    loading: Boolean = false
) {
    val colors = getButtonColors(variant)
    val textSize = when (size) {
        HandButtonSize.Small -> 14.sp
        HandButtonSize.Medium -> 16.sp
        HandButtonSize.Large -> 18.sp
    }
    val height = when (size) {
        HandButtonSize.Small -> 36.dp
        HandButtonSize.Medium -> 44.dp
        HandButtonSize.Large -> 52.dp
    }

    // 不规则圆角
    val shape = HandDrawnShape(
        cornerRadius = 12.dp,
        irregularity = 0.2f
    )

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    Button(
        onClick = onClick,
        modifier = modifier
            .height(height)
            .then(
                if (variant != HandButtonVariant.Ghost && variant != HandButtonVariant.Outline)
                    Modifier.graphicsLayer {
                        // 按下时的缩放效果
                        val scale = if (isPressed) 0.95f else 1f
                        scaleX = scale
                        scaleY = scale
                    }
                else Modifier
            ),
        enabled = enabled && !loading,
        shape = shape,
        colors = ButtonDefaults.buttonColors(
            containerColor = colors.container,
            contentColor = colors.content,
            disabledContainerColor = colors.disabledContainer,
            disabledContentColor = colors.disabledContent
        ),
        border = when (variant) {
            HandButtonVariant.Outline -> BorderStroke(
                1.dp,
                if (enabled) colors.content else colors.disabledContent.copy(alpha = 0.5f)
            )
            else -> null
        },
        elevation = when (variant) {
            HandButtonVariant.Ghost, HandButtonVariant.Outline -> null
            else -> ButtonDefaults.buttonElevation(
                defaultElevation = 2.dp,
                pressedElevation = 0.dp
            )
        },
        interactionSource = interactionSource
    ) {
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = colors.content,
                strokeWidth = 2.dp
            )
        } else {
            Text(
                text = text ?: "",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = textSize,
                    fontWeight = FontWeight.Medium
                ),
                fontFamily = FontFamily(Font(R.font.lxgw_wenkai))
            )
        }
    }
}

enum class HandButtonVariant {
    Primary, Secondary, Outline, Ghost
}

enum class HandButtonSize {
    Small, Medium, Large
}

private data class ButtonColors(
    val container: Color,
    val content: Color,
    val disabledContainer: Color,
    val disabledContent: Color
)

@Composable
private fun getButtonColors(variant: HandButtonVariant): ButtonColors {
    return when (variant) {
        HandButtonVariant.Primary -> ButtonColors(
            container = MaterialTheme.colorScheme.primary,
            content = MaterialTheme.colorScheme.onPrimary,
            disabledContainer = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
            disabledContent = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
        )
        HandButtonVariant.Secondary -> ButtonColors(
            container = MaterialTheme.colorScheme.secondary,
            content = MaterialTheme.colorScheme.onSecondary,
            disabledContainer = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f),
            disabledContent = MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.7f)
        )
        HandButtonVariant.Outline -> ButtonColors(
            container = Color.Transparent,
            content = MaterialTheme.colorScheme.primary,
            disabledContainer = Color.Transparent,
            disabledContent = MaterialTheme.colorScheme.inkFaded
        )
        HandButtonVariant.Ghost -> ButtonColors(
            container = Color.Transparent,
            content = MaterialTheme.colorScheme.inkLight,
            disabledContainer = Color.Transparent,
            disabledContent = MaterialTheme.colorScheme.inkFaded
        )
    }
}
```

#### 3.1.6 纸张纹理组件
```kotlin
// PaperTexture.kt
@Composable
fun PaperTexture(
    modifier: Modifier = Modifier,
    variant: PaperVariant = PaperVariant.LIGHT,
    content: @Composable BoxScope.() -> Unit
) {
    Box(modifier = modifier) {
        // 纸张纹理背景
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    when (variant) {
                        PaperVariant.LIGHT -> Color(0xFFF9F5F0)
                        PaperVariant.WARM -> Color(0xFFFFF9F0)
                        PaperVariant.KRAFT -> Color(0xFFD4C4A8)
                        PaperVariant.WATERCOLOR -> Color(0xFFF5E6D3)
                    }
                )
        )
        // 噪声纹理（模拟纸张质感）
        Canvas(
            modifier = Modifier.matchParentSize()
        ) {
            // 绘制半透明噪声层
            drawRect(
                color = Color.Black.copy(alpha = 0.02f),
                blendMode = BlendMode.Overlay
            )
        }
        content()
    }
}

enum class PaperVariant {
    LIGHT, WARM, KRAFT, WATERCOLOR
}
```

### 3.2 页面布局设计

#### 3.2.1 底部导航栏
```kotlin
// BottomBar.kt
@Composable
fun BottomNavBar(
    navController: NavController,
    currentRoute: String?,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.paperSurface.copy(alpha = 0.95f),
        tonalElevation = 2.dp
    ) {
        val screens = listOf(
            BottomNavItem.Home,
            BottomNavItem.Discover,
            BottomNavItem.Create,
            BottomNavItem.Friends,
            BottomNavItem.Profile
        )

        screens.forEach { item ->
            val isSelected = currentRoute == item.route

            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                        tint = if (isSelected)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.inkFaded
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        style = MaterialTheme.typography.labelSmall,
                        fontFamily = FontFamily(Font(R.font.lxgw_wenkai))
                    )
                }
            )
        }
    }
}

data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
) {
    companion object {
        val Home = BottomNavItem(Screen.Home.route, "首页", Icons.Default.Home)
        val Discover = BottomNavItem(Screen.Discover.route, "发现", Icons.Default.Explore)
        val Create = BottomNavItem(Screen.PostCreate.route, "发布", Icons.Default.Add)
        val Friends = BottomNavItem(Screen.Friends.route, "好友", Icons.Default.People)
        val Profile = BottomNavItem(Screen.Profile.route, "我的", Icons.Default.Person)
    }
}
```

#### 3.2.2 首页布局（瀑布流）
```kotlin
// HomeScreen.kt
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    navController: NavController
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        topBar = {
            HomeTopBar(
                onFilterClick = { viewModel.toggleFilter() },
                onBgChangeClick = { viewModel.changeBackground() },
                scrollBehavior = scrollBehavior
            )
        },
        bottomBar = {
            BottomNavBar(
                navController = navController,
                currentRoute = Screen.Home.route
            )
        }
    ) { padding ->
        when (val state = uiState) {
            is HomeUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is HomeUiState.Success -> {
                PullToRefreshBox(
                    isRefreshing = state.isRefreshing,
                    onRefresh = { viewModel.refresh() },
                    modifier = Modifier.padding(padding)
                ) {
                    if (state.posts.isEmpty()) {
                        EmptyState(
                            message = "还没有任何记录",
                            description = "快去记录你的第一个时刻吧！"
                        )
                    } else {
                        StaggeredGrid(state.posts) { post ->
                            PostCard(
                                post = post,
                                onClick = { navController.navigate(Screen.PostDetail.createRoute(post.postId)) }
                            )
                        }
                    }
                }
            }
            is HomeUiState.Error -> {
                ErrorState(
                    message = state.message,
                    onRetry = { viewModel.loadPosts() }
                )
            }
        }
    }
}

@Composable
fun StaggeredGrid(
    posts: List<BlogPost>,
    onPostClick: (BlogPost) -> Unit
) {
    VerticalStaggeredGrid(
        columns = StaggeredGridCells.Adaptive(minSize = 180.dp),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalItemSpacing = 12.dp
    ) {
        items(posts, key = { it.postId }) { post ->
            PostCard(post = post, onClick = { onPostClick(post) })
        }
    }
}
```

---

## 四、分阶段开发计划

### 第一阶段：基础架构 (2周)

#### 第一周：项目初始化
- [ ] 创建Android Studio项目
- [ ] 配置Gradle和依赖
- [ ] 搭建项目目录结构
- [ ] 配置Hilt依赖注入
- [ ] 配置Coil图片加载
- [ ] 配置Material Design 3主题
- [ ] 创建基础主题配色和字体配置
- [ ] 配置导航系统

#### 第二周：基础组件开发
- [ ] 实现手绘风格按钮（HandButton）
- [ ] 实现手绘风格输入框（HandInput）
- [ ] 实现拍立得卡片（HandCard）
- [ ] 实现胶带装饰组件（Tape）
- [ ] 实现图钉装饰组件（Pin）
- [ ] 实现纸张纹理（PaperTexture）
- [ ] 实现手绘边框（HandBorder）
- [ ] 实现底部导航栏
- [ ] 实现加载动画组件
- [ ] 实现Toast提示组件

### 第二阶段：用户认证 (2周)

#### 第三周：认证功能
- [ ] 实现登录页面
- [ ] 实现注册页面
- [ ] 实现表单验证
- [ ] 配置JWT Token管理
- [ ] 实现自动登录功能
- [ ] 实现Token刷新机制
- [ ] 实现登录状态持久化
- [ ] 实现退出登录功能

#### 第四周：用户信息
- [ ] 实现个人资料显示页
- [ ] 实现个人资料编辑页
- [ ] 实现头像选择和上传
- [ ] 实现头像裁剪功能
- [ ] 实现背景图片更换
- [ ] 实现好友列表页面
- [ ] 实现消息列表导航

### 第三阶段：文章系统 (3周)

#### 第五周：首页和列表
- [ ] 实现首页布局
- [ ] 实现顶部筛选栏
- [ ] 实现文章瀑布流展示
- [ ] 实现拍立得卡片展示
- [ ] 实现随机旋转效果
- [ ] 实现筛选功能
- [ ] 实现分页加载

#### 第六周：文章详情
- [ ] 实现文章详情页布局
- [ ] 实现大图展示
- [ ] 实现多图滑动查看
- [ ] 实现图文混排展示
- [ ] 实现图片查看器（缩放、滑动）
- [ ] 实现标签显示

#### 第七周：评论系统
- [ ] 实现评论区布局
- [ ] 实现评论列表展示
- [ ] 实现发送评论功能
- [ ] 实现评论输入框
- [ ] 实现评论删除功能

### 第四阶段：社交功能 (3周)

#### 第八周：好友系统
- [ ] 实现好友列表页面
- [ ] 实现添加好友搜索页面
- [ ] 实现发送好友请求
- [ ] 实现好友请求列表
- [ ] 实现接受/拒绝请求
- [ ] 实现删除好友功能

#### 第九-十周：聊天系统
- [ ] 实现聊天列表页面
- [ ] 实现聊天房间页面
- [ ] 实现消息气泡样式
- [ ] 实现WebSocket连接
- [ ] 实现消息发送功能
- [ ] 实现消息接收实时更新
- [ ] 实现聊天记录加载
- [ ] 实现未读消息标记
- [ ] 实现消息输入和发送
- [ ] 实现图片发送功能

### 第五阶段：高级功能 (2周)

#### 第十一周：文章创建和编辑
- [ ] 实现文章创建页面
- [ ] 实现富文本编辑器基础
- [ ] 实现图片选择器
- [ ] 实现图片上传和压缩
- [ ] 实现标签输入
- [ ] 实现天气选择器
- [ ] 实现心情选择器
- [ ] 实现位置选择
- [ ] 实现可见性设置
- [ ] 实现草稿保存
- [ ] 实现文章编辑功能

#### 第十二周：通知和其他功能
- [ ] 实现通知中心页面
- [ ] 实现通知列表展示
- [ ] 实现通知点击跳转
- [ ] 实现标记已读功能
- [ ] 实现消息提示组件
- [ ] 实现系统设置页面
- [ ] 实现关于页面

### 第六阶段：管理员功能 (1周)

#### 第十三周：后台管理
- [ ] 实现管理员登录
- [ ] 实现文章管理列表
- [ ] 实现文章编辑/删除
- [ ] 实现用户管理
- [ ] 实现评论管理

### 第七阶段：优化和完善 (3周)

#### 第十四周：性能优化
- [ ] 图片加载优化
- [ ] 列表性能优化
- [ ] 内存优化
- [ ] 启动速度优化
- [ ] 网络请求优化
- [ ] 数据缓存策略

#### 第十五周：UI/UX完善
- [ ] 页面转场动画
- [ ] 列表动画效果
- [ ] 手势操作优化
- [ ] 深色模式适配
- [ ] 无障碍功能
- [ ] 错误处理优化
- [ ] 加载状态优化

#### 第十六周：测试和准备
- [ ] 单元测试
- [ ] UI测试
- [ ] 兼容性测试
- [ ] Bug修复
- [ ] 应用图标设计
- [ ] 启动页设计
- [ ] 应用截图准备
- [ ] 隐私政策编写
- [ ] 打包签名APK
- [ ] Google Play上架准备

---

## 五、关键技术实现

### 5.1 网络层实现

#### 5.1.1 Retrofit配置
```kotlin
// NetworkModule.kt
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(
        tokenInterceptor: TokenInterceptor,
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(tokenInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideMoshi(): Moshi {
        return Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .add(DateAdapter())
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, moshi: Moshi): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }
}

class TokenInterceptor @Inject constructor(
    private val userDataStore: UserDataStore
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        runBlocking {
            userDataStore.getAuthToken().collect { token ->
                if (!token.isNullOrEmpty()) {
                    val authenticatedRequest = originalRequest.newBuilder()
                        .header("Authorization", "Bearer $token")
                        .build()
                    return@collect chain.proceed(authenticatedRequest)
                }
            }
        }

        return chain.proceed(originalRequest)
    }
}
```

#### 5.1.2 API接口定义
```kotlin
// ApiService.kt
interface ApiService {
    // ==================== 认证 ====================
    @POST("/api/auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @POST("/api/users")
    suspend fun register(@Body request: RegisterRequest): User

    // ==================== 用户 ====================
    @GET("/api/users/{id}")
    suspend fun getUser(@Path("id") id: Int): User

    @GET("/api/users/username/{username}")
    suspend fun getUserByUsername(@Path("username") username: String): User

    @PUT("/api/users/{id}")
    suspend fun updateUser(@Path("id") id: Int, @Body user: User): User

    // ==================== 文章 ====================
    @GET("/api/posts")
    suspend fun getPosts(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20,
        @Query("userId") userId: Int?,
        @Query("categoryId") categoryId: Int?,
        @Query("status") status: Int?
    ): List<BlogPost>

    @GET("/api/posts/{id}")
    suspend fun getPost(@Path("id") id: Int): BlogPost

    @POST("/api/posts")
    suspend fun createPost(@Body post: CreatePostRequest): BlogPost

    @PUT("/api/posts/{id}")
    suspend fun updatePost(@Path("id") id: Int, @Body post: CreatePostRequest): BlogPost

    @DELETE("/api/posts/{id}")
    suspend fun deletePost(@Path("id") id: Int): Unit

    // ==================== 评论 ====================
    @GET("/api/comments/post/{postId}")
    suspend fun getCommentsByPost(@Path("postId") postId: Int): List<Comment>

    @POST("/api/comments")
    suspend fun createComment(@Body comment: CreateCommentRequest): Comment

    @DELETE("/api/comments/{id}")
    suspend fun deleteComment(@Path("id") id: Int): Unit

    // ==================== 好友 ====================
    @GET("/api/friendships/user/{userId}")
    suspend fun getFriendships(@Path("userId") userId: Int): List<Friendship>

    @POST("/api/friendships/request")
    suspend fun sendFriendRequest(
        @Query("userId") userId: Int,
        @Query("friendId") friendId: Int
    ): Friendship

    @PUT("/api/friendships/{friendshipId}/accept")
    suspend fun acceptFriendRequest(
        @Path("friendshipId") friendshipId: Int,
        @Query("userId") userId: Int
    ): Friendship

    @DELETE("/api/friendships/user/{userId}/friend/{friendId}")
    suspend fun deleteFriendship(
        @Path("userId") userId: Int,
        @Path("friendId") friendId: Int
    ): Unit

    // ==================== 消息 ====================
    @POST("/api/messages/send")
    suspend fun sendMessage(
        @Query("receiverId") receiverId: Int,
        @Query("content") content: String
    ): Message

    @GET("/api/messages/history")
    suspend fun getChatHistory(@Query("friendId") friendId: Int): List<Message>

    @GET("/api/messages/unread")
    suspend fun getUnreadMessages(): List<Message>

    @PUT("/api/messages/read")
    suspend fun markAsRead(@Query("senderId") senderId: Int): Unit

    // ==================== 通知 ====================
    @GET("/api/notifications/user/{userId}")
    suspend fun getNotifications(@Path("userId") userId: Int): List<Notification>

    @GET("/api/notifications/unread-count/{userId}")
    suspend fun getUnreadCount(@Path("userId") userId: Int): Int

    @PUT("/api/notifications/{id}/read")
    suspend fun markNotificationAsRead(@Path("id") id: Int): Notification

    @PUT("/api/notifications/read-all")
    suspend fun markAllAsRead(@Query("userId") userId: Int): Unit

    // ==================== 文件上传 ====================
    @Multipart
    @POST("/api/files/upload")
    suspend fun uploadImage(@Part file: MultipartBody.Part): UploadResponse

    @Multipart
    @POST("/api/files/upload/avatar")
    suspend fun uploadAvatar(@Part file: MultipartBody.Part): UploadResponse
}
```

### 5.2 WebSocket实时通信

```kotlin
// ChatWebSocketManager.kt
@Singleton
class ChatWebSocketManager @Inject constructor(
    private val okHttpClient: OkHttpClient,
    private val moshi: Moshi
) {
    private var webSocket: WebSocket? = null
    private var currentUserId: Int? = null
    private val _connectionState = MutableStateFlow<ConnectionState>(ConnectionState.Disconnected)
    val connectionState: StateFlow<ConnectionState> = _connectionState.asStateFlow()

    private val _messagesFlow = MutableSharedFlow<Message>()
    val messagesFlow: SharedFlow<Message> = _messagesFlow.asSharedFlow()

    private var reconnectJob: Job? = null
    private var retryCount = 0
    private val maxRetries = 5

    fun connect(userId: Int) {
        if (currentUserId == userId && _connectionState.value == ConnectionState.Connected) {
            return
        }

        currentUserId = userId
        disconnect()

        val request = Request.Builder()
            .url("${BuildConfig.WS_BASE_URL}/chat")
            .build()

        webSocket = okHttpClient.newWebSocket(request, ChatWebSocketListener())
        _connectionState.value = ConnectionState.Connecting
    }

    fun disconnect() {
        webSocket?.close(1000, "User disconnects")
        webSocket = null
        currentUserId = null
        _connectionState.value = ConnectionState.Disconnected
        reconnectJob?.cancel()
    }

    fun sendMessage(message: Message) {
        try {
            val json = moshi.adapter(Message::class.java).toJson(message)
            webSocket?.send(json)
        } catch (e: Exception) {
            _connectionState.value = ConnectionState.Error(e.message ?: "Send failed")
        }
    }

    private inner class ChatWebSocketListener : WebSocketListener() {
        override fun onOpen(webSocket: WebSocket, response: Response) {
            _connectionState.value = ConnectionState.Connected
            retryCount = 0
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            try {
                val message = moshi.adapter(Message::class.java).fromJson(text)
                message?.let { _messagesFlow.tryEmit(it) }
            } catch (e: Exception) {
                _connectionState.value = ConnectionState.Error("Parse error: ${e.message}")
            }
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            _connectionState.value = ConnectionState.Closing
        }

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            _connectionState.value = ConnectionState.Disconnected
            scheduleReconnect()
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            _connectionState.value = ConnectionState.Error(t.message ?: "Connection failed")
            scheduleReconnect()
        }
    }

    private fun scheduleReconnect() {
        if (retryCount >= maxRetries || currentUserId == null) {
            return
        }

        val delay = (2.0.pow(retryCount) * 1000).toLong() // 指数退避
        retryCount++

        reconnectJob = CoroutineScope(Dispatchers.IO).launch {
            delay(delay)
            currentUserId?.let { connect(it) }
        }
    }
}

sealed class ConnectionState {
    object Connecting : ConnectionState()
    object Connected : ConnectionState()
    object Closing : ConnectionState()
    object Disconnected : ConnectionState()
    data class Error(val message: String) : ConnectionState()
}
```

### 5.3 图片上传和压缩

```kotlin
// ImageUtil.kt
object ImageUtil {

    fun Compress(uri: Uri, maxWidth: Int = 1080, quality: Int = 80): Bitmap? {
        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }

        try {
            val inputStream = uri.toFile()?.inputStream()
            if (inputStream == null) return null

            BitmapFactory.decodeStream(inputStream, null, options)

            options.inSampleSize = calculateInSampleSize(options, maxWidth, maxWidth)
            options.inJustDecodeBounds = false

            val bitmap = BitmapFactory.decodeStream(uri.toFile()?.inputStream(), null, options)

            // 进一步压缩
            val outputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)

            return BitmapFactory.decodeByteArray(
                outputStream.toByteArray(),
                0,
                outputStream.size()
            )
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            val halfHeight = height / 2
            val halfWidth = width / 2

            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }

        return inSampleSize
    }

    suspend fun compressImage(uri: Uri, context: Context): File? = withContext(Dispatchers.IO) {
        try {
            val originalBitmap = Compress(uri, 1920, 90) ?: return@withContext null

            val tempfile = File(context.cacheDir, "compressed_${System.currentTimeMillis()}.jpg")
            tempfile.outputStream().use { out ->
                originalBitmap.compress(Bitmap.CompressFormat.JPEG, 85, out)
            }

            originalBitmap.recycle()
            tempfile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
```

---

## 六、测试策略

### 6.1 单元测试
```kotlin
// 示例：ViewModel测试
@Test
fun `login with valid credentials returns success`() = runTest {
    // Given
    val mockUseCase = mockk<LoginUseCase>()
    coEvery { mockUseCase(any()) } returns Result.success(mockUser)

    val viewModel = LoginViewModel(mockUseCase)

    // When
    viewModel.login("test", "password")

    // Then
    assertEquals(LoginUiState.Success(mockUser), viewModel.uiState.value)
}
```

### 6.2 UI测试
```kotlin
// 示例：Compose UI测试
@Test
fun `login button is enabled when inputs are valid`() {
    composeTestRule.setContent {
        LoginScreen(viewModel = loginViewModel)
    }

    composeTestRule.onNodeWithText("用户名")
        .performTextInput("testuser")
    composeTestRule.onNodeWithText("密码")
        .performTextInput("password123")

    composeTestRule.onNodeWithText("登录")
        .assertIsEnabled()
}
```

---

## 七、发布准备

### 7.1 应用图标
- 基于Web端logo设计
- 支持自适应图标（Adaptive Icon）
- 适配多种尺寸

### 7.2 权限配置
```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.CAMERA" />
```

### 7.3 发布清单
- [ ] 应用APK（调试版）
- [ ] 应用APK（发布版）
- [ ] 应用AAB（Google Play）
- [ ] 应用截图
- [ ] 应用图标
- [ ] 隐私政策
- [ ] 服务条款
- [ ] 应用描述

---

## 八、快速开始指南

### 8.1 克隆项目
```bash
git clone <repository-url>
cd our-moments-android
```

### 8.2 打开项目
使用Android Studio打开项目文件夹

### 8.3 配置后端地址
在 `local.properties` 中配置：
```properties
backend.url=http://your-server-ip:8080
```

### 8.4 运行应用
点击Run按钮或使用 `./gradlew installDebug` 安装到设备

---

## 九、注意事项

1. **保持与Web端一致**: UI风格、功能、数据模型都要保持一致
2. **性能优先**: 图片加载、列表滚动、网络请求都要优化
3. **用户体验**: 流畅的动画、友好的错误提示、清晰的加载状态
4. **代码质量**: 遵循Kotlin代码规范、添加必要的注释、编写测试
5. **版本兼容**: 最低支持Android 7.0 (API 24)

---

## 十、参考资源

- Web端项目地址: `D:\Workspace\our-moments\our-moments-frontend`
- 后端API文档: 待补充
- 设计规范文档: 待补充
- 技术文档: 待补充

---

**文档版本**: 1.0
**创建日期**: 2026-01-07
**最后更新**: 2026-01-07