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