-- 数据库初始化脚本
-- 项目名称：手绘风生活记录网站 (Hand-drawn Life Logger)
-- 技术栈：MySQL, SpringBoot, MyBatis
-- 字符集：utf8mb4



-- ----------------------------
-- 1. 用户表 (sys_user)
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `user_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(64) NOT NULL COMMENT '用户名',
  `password` varchar(100) NOT NULL COMMENT '密码',
  `nickname` varchar(64) DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像地址(OSS)',
  `bio` varchar(255) DEFAULT NULL COMMENT '个人简介',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱地址',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- ----------------------------
-- 2. 日志分类表 (sys_category)
-- ----------------------------
DROP TABLE IF EXISTS `sys_category`;
CREATE TABLE `sys_category` (
  `category_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `name` varchar(50) NOT NULL COMMENT '分类名称',
  `icon_url` varchar(255) DEFAULT NULL COMMENT '分类手绘图标(OSS)',
  `sort_order` int(11) DEFAULT '0' COMMENT '显示顺序',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`category_id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COMMENT='日志分类表';

-- ----------------------------
-- 3. 标签表 (sys_tag)
-- ----------------------------
DROP TABLE IF EXISTS `sys_tag`;
CREATE TABLE `sys_tag` (
  `tag_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '标签ID',
  `name` varchar(50) NOT NULL COMMENT '标签名称',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`tag_id`),
  UNIQUE KEY `uk_name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8mb4 COMMENT='标签表';

-- ----------------------------
-- 4. 生活日志主表 (blog_post)
-- ----------------------------
DROP TABLE IF EXISTS `blog_post`;
CREATE TABLE `blog_post` (
  `post_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `user_id` bigint(20) NOT NULL COMMENT '作者ID',
  `category_id` bigint(20) DEFAULT NULL COMMENT '分类ID',
  `title` varchar(100) DEFAULT NULL COMMENT '标题(可以是日期或心情)',
  `content` text COMMENT '文字内容',
  `weather` varchar(50) DEFAULT NULL COMMENT '天气(晴/雨/多云)',
  `mood` varchar(50) DEFAULT NULL COMMENT '心情(开心/平淡/难过)',
  `location` varchar(100) DEFAULT NULL COMMENT '地点',
  `status` tinyint(4) DEFAULT '1' COMMENT '状态(0:草稿 1:发布)',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `visibility` varchar(20) DEFAULT 'PUBLIC',
  PRIMARY KEY (`post_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_category_id` (`category_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1000 DEFAULT CHARSET=utf8mb4 COMMENT='生活日志主表';

-- ----------------------------
-- 5. 日志媒体资源表 (blog_media)
-- 用于存储图片和视频，支持手绘贴纸式编辑的元数据
-- ----------------------------
DROP TABLE IF EXISTS `blog_media`;
CREATE TABLE `blog_media` (
  `media_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '媒体ID',
  `post_id` bigint(20) NOT NULL COMMENT '关联日志ID',
  `media_url` varchar(500) NOT NULL COMMENT '资源地址(OSS)',
  `media_type` varchar(20) NOT NULL COMMENT '类型(IMAGE/VIDEO)',
  `rotation` int(11) DEFAULT '0' COMMENT '旋转角度',
  `scale` decimal(3,2) DEFAULT '1.00' COMMENT '缩放比例',
  `position_x` int(11) DEFAULT '0' COMMENT 'X坐标偏移',
  `position_y` int(11) DEFAULT '0' COMMENT 'Y坐标偏移',
  `filter_style` varchar(50) DEFAULT NULL COMMENT '手绘滤镜风格(sketch/pencil/watercolor)',
  `z_index` int(11) DEFAULT '0' COMMENT '层级',
  `sort_order` int(11) DEFAULT '0' COMMENT '排序',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`media_id`),
  KEY `idx_post_id` (`post_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5000 DEFAULT CHARSET=utf8mb4 COMMENT='日志媒体资源表';

-- ----------------------------
-- 6. 日志标签关联表 (blog_post_tag)
-- ----------------------------
DROP TABLE IF EXISTS `blog_post_tag`;
CREATE TABLE `blog_post_tag` (
  `post_id` bigint(20) NOT NULL COMMENT '日志ID',
  `tag_id` bigint(20) NOT NULL COMMENT '标签ID',
  PRIMARY KEY (`post_id`,`tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='日志标签关联表';

-- ----------------------------
-- 插入测试数据
-- ----------------------------

-- 插入用户
INSERT INTO `sys_user` (`user_id`, `username`, `password`, `nickname`, `bio`, `avatar`) VALUES
(1, 'her_admin', 'e10adc3949ba59abbe56e057f20f883e', '暖暖', '记录每一个温暖的瞬间', 'https://oss.example.com/avatars/user1.png');

-- 插入分类
INSERT INTO `sys_category` (`category_id`, `name`, `icon_url`, `sort_order`) VALUES
(1, '日常碎碎念', 'https://oss.example.com/icons/daily.png', 1),
(2, '旅行手账', 'https://oss.example.com/icons/travel.png', 2),
(3, '美食探店', 'https://oss.example.com/icons/food.png', 3),
(4, '铲屎官日记', 'https://oss.example.com/icons/cat.png', 4);

-- 插入标签
INSERT INTO `sys_tag` (`tag_id`, `name`) VALUES
(101, '治愈'),
(102, '周末'),
(103, '咖啡'),
(104, '猫咪'),
(105, '手绘');

-- 插入日志 1: 周末探店
INSERT INTO `blog_post` (`post_id`, `user_id`, `category_id`, `title`, `content`, `weather`, `mood`, `location`, `status`, `create_time`) VALUES
(1001, 1, 3, '转角遇到的手冲咖啡店', '今天天气真好，在街角发现了一家超级复古的咖啡店。老板养了一只橘猫，咖啡豆的香气混合着阳光的味道，太治愈了。忍不住画了几笔店里的角落。', 'Sunny', 'Happy', 'Dream Coffee Shop', 1, '2023-10-15 14:30:00');

-- 插入日志 1 的媒体资源 (模拟手绘风格贴图参数)
INSERT INTO `blog_media` (`post_id`, `media_url`, `media_type`, `rotation`, `scale`, `filter_style`, `position_x`, `position_y`, `sort_order`) VALUES
(1001, 'https://oss.example.com/posts/coffee1.jpg', 'IMAGE', 5, 0.95, 'watercolor', 10, 20, 1),
(1001, 'https://oss.example.com/posts/cat_video.mp4', 'VIDEO', 0, 1.00, 'none', 0, 0, 2),
(1001, 'https://oss.example.com/posts/sketch_drawing.png', 'IMAGE', -10, 0.80, 'pencil', -15, 50, 3);

-- 插入日志 1 的标签关联
INSERT INTO `blog_post_tag` (`post_id`, `tag_id`) VALUES
(1001, 102), -- 周末
(1001, 103), -- 咖啡
(1001, 105); -- 手绘

-- 插入日志 2: 铲屎官日常
INSERT INTO `blog_post` (`post_id`, `user_id`, `category_id`, `title`, `content`, `weather`, `mood`, `location`, `status`, `create_time`) VALUES
(1002, 1, 4, '今天的咪咪也很高冷', '给咪咪买了新的猫罐头，结果它看都不看一眼，果然是主子。试图给它画张像，结果它一直动！', 'Cloudy', 'Funny', 'Home', 1, '2023-10-16 09:00:00');

-- 插入日志 2 的媒体资源
INSERT INTO `blog_media` (`post_id`, `media_url`, `media_type`, `rotation`, `scale`, `filter_style`, `sort_order`) VALUES
(1002, 'https://oss.example.com/posts/cat_angry.jpg', 'IMAGE', 0, 1.0, 'sketch', 1);

-- 插入日志 2 的标签关联
INSERT INTO `blog_post_tag` (`post_id`, `tag_id`) VALUES
(1002, 104); -- 猫咪

-- Insert sample users (ignore if already exist)
INSERT IGNORE INTO sys_user (username, password, nickname, avatar, bio, create_time, update_time) VALUES
('admin', 'admin123', '管理员', 'https://api.dicebear.com/7.x/adventurer/svg?seed=admin', '系统管理员', NOW(), NOW()),
('xiaoming', '123456', '小明', 'https://api.dicebear.com/7.x/adventurer/svg?seed=xiaoming', '喜欢记录生活的点点滴滴，用文字和图片留住美好的瞬间。', NOW(), NOW()),
('xiaohong', '123456', '小红', 'https://api.dicebear.com/7.x/adventurer/svg?seed=xiaohong', '热爱生活，热爱分享。', NOW(), NOW());

-- Insert sample categories
INSERT IGNORE INTO sys_category (name, icon_url, sort_order, create_time) VALUES
('日常', '', 1, NOW()),
('美食', '', 2, NOW()),
('旅行', '', 3, NOW()),
('心情', '', 4, NOW());

-- Insert sample tags
INSERT IGNORE INTO sys_tag (name, create_time) VALUES
('周末', NOW()),
('美好', NOW()),
('阳光', NOW()),
('咖啡', NOW()),
('读书', NOW()),
('散步', NOW()),
('感动', NOW()),
('开心', NOW()),
('惬意', NOW()),
('思念', NOW());

-- Create friendship table
CREATE TABLE IF NOT EXISTS sys_friendship (
                                              friendship_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                              user_id BIGINT NOT NULL,
                                              friend_id BIGINT NOT NULL,
                                              status VARCHAR(20) NOT NULL DEFAULT 'PENDING', -- PENDING, ACCEPTED, REJECTED
    create_time DATETIME NOT NULL,
    update_time DATETIME NOT NULL,
    FOREIGN KEY (user_id) REFERENCES sys_user(user_id),
    FOREIGN KEY (friend_id) REFERENCES sys_user(user_id),
    UNIQUE KEY unique_friendship (user_id, friend_id)
    );


-- Create comment table
CREATE TABLE IF NOT EXISTS blog_comment (
                                            comment_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                            post_id BIGINT NOT NULL,
                                            user_id BIGINT NOT NULL,
                                            content TEXT NOT NULL,
                                            position INT NOT NULL DEFAULT 0, -- 评论在文章中的位置
                                            create_time DATETIME NOT NULL,
                                            update_time DATETIME NOT NULL,
                                            FOREIGN KEY (post_id) REFERENCES blog_post(post_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES sys_user(user_id)
    );

-- Create notification table
CREATE TABLE IF NOT EXISTS sys_notification (
    notification_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    type VARCHAR(50) NOT NULL, -- COMMENT, FRIEND_REQUEST, NEW_POST
    content TEXT,
    related_id BIGINT, -- 关联ID（评论ID、好友关系ID、文章ID等）
    is_read BOOLEAN DEFAULT FALSE,
    create_time DATETIME NOT NULL,
    FOREIGN KEY (user_id) REFERENCES sys_user(user_id),
    INDEX idx_user_id (user_id),
    INDEX idx_is_read (is_read),
    INDEX idx_create_time (create_time)
);

-- Create message table
CREATE TABLE IF NOT EXISTS sys_message (
    message_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    sender_id BIGINT NOT NULL,
    receiver_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    is_read BOOLEAN DEFAULT FALSE,
    create_time DATETIME NOT NULL,
    update_time DATETIME NOT NULL,
    FOREIGN KEY (sender_id) REFERENCES sys_user(user_id),
    FOREIGN KEY (receiver_id) REFERENCES sys_user(user_id),
    INDEX idx_sender_receiver (sender_id, receiver_id),
    INDEX idx_receiver_time (receiver_id, create_time),
    INDEX idx_unread (receiver_id, is_read)
);

-- Add email column to sys_user if not exists
ALTER TABLE sys_user ADD COLUMN IF NOT EXISTS `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱地址' AFTER `bio`;
