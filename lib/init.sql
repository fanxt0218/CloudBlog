drop database if exists cloud_blog;

create database cloud_blog;
use cloud_blog;

drop table if exists user;
create table user (
    id bigint primary key auto_increment comment '用户id',
    user_account varchar(32) comment '用户账号',
    phone varchar(32) comment '手机号',
    email varchar(64) comment '邮箱',
    password varchar(255) comment '用户密码',
    status tinyint default 0 comment '用户状态,0:正常 1：锁定 2：删除 3：失效',
    last_login_time datetime comment '最后登录时间',
    permission_id int comment '权限id',
    create_time datetime comment '创建时间',

    index idx_status (status),
    unique index uk_user_account (user_account)
)comment '用户表';

drop table if exists user_info;
create table user_info (
    id bigint primary key auto_increment comment '主键',
    user_id bigint comment '用户id',
    user_name varchar(32) comment '用户名',
    sex tinyint comment '性别,0:女 1:男 2:未指定',
    image varchar(255) comment '用户头像',
    introduction text comment '用户简介',
    region varchar(64) comment '所在地区',
    birth_date date comment '生日',
    profession varchar(64) comment '职业',
    is_vip tinyint default 0 comment '是否是会员,0:不是 1:是',
    exp int default 0 comment '经验值',
    create_time datetime comment '创建时间',
    update_time datetime comment '更新时间',

    unique index uk_user_id (user_id),
    index idx_user_name (user_name),
    index idx_is_vip (is_vip),
    index idx_exp (exp)
)comment '用户信息表';

drop table if exists level;
create table level (
    id int primary key auto_increment comment '主键',
    level tinyint comment '等级',
    level_name varchar(32) comment '等级名称',
    exp_threshold int comment '经验值阈值',

    unique index uk_level (level)
)comment '等级表';

insert into level (level, level_name, exp_threshold) values
(1,'等级一',0),
(2,'等级二',1000),
(3,'等级三',2000),
(4,'等级四',3000),
(5,'等级五',4000),
(6,'等级六',5000),
(7,'等级七',6000),
(8,'等级八',7000),
(9,'等级九',8000),
(10,'等级十',10000);


drop table if exists user_focus;
create table user_focus (
    id bigint primary key auto_increment comment '主键',
    user_id bigint comment '用户id',
    focus_user_id bigint comment '关注用户id',
    create_time datetime comment '创建时间',
    source varchar(270) comment '来源',

    unique index uk_user_focus (user_id, focus_user_id),
    index idx_user_id (user_id),
    index idx_focus_user_id (focus_user_id)
)comment '用户关注表';

drop table if exists user_interest;
create table user_interest (
    id int primary key auto_increment comment '主键',
    user_id bigint comment '用户id',
    tag_id int comment '标签id',
    weight decimal(10,2) default 0.00 comment '权重',
    create_time datetime comment '创建时间',
    update_time datetime comment '更新时间',

    unique index uk_user_tag (user_id, tag_id),
    index idx_user_id (user_id),
    index idx_tag_id (tag_id),
    index idx_weight (weight)
)comment '用户兴趣表';

drop table if exists tag;
create table tag (
    id int primary key auto_increment comment '主键',
    tag_name varchar(32) comment '标签名称',
    class_id int comment '分类id',
    description varchar(64) comment '标签描述',
    create_time datetime comment '创建时间',
    update_time datetime comment '更新时间',
    status tinyint default 0 comment '标签状态,0:正常 1:删除',

    unique index uk_tag_name (tag_name)
)comment '标签表';

insert into tag (tag_name, description, create_time) values
('java', 'java语言', now()),
('python', 'python语言', now()),
('c++', 'c++语言', now()),
('javascript', 'javascript语言', now()),
('html', 'html语言', now()),
('css', 'css语言', now()),
('mysql', 'mysql数据库', now()),
('redis', 'redis数据库', now()),
('mongodb', 'mongodb数据库', now()),
('前端', '前端技术', now()),
('后端', '后端技术', now()),
('数据库', '数据库技术', now()),
('大数据', '大数据技术', now()),
('人工智能', '人工智能技术', now()),
('机器学习', '机器学习技术', now()),
('爬虫', '爬虫技术', now()),
('测试', '测试', now()),
('开发工具', '开发工具', now()),
('运维', '运维技术', now()),
('区块链', '区块链技术', now()),
('物联网', '物联网技术', now()),
('游戏开发', '游戏开发技术', now()),
('移动开发', '移动开发技术', now()),
('AIGC', 'AI创作', now());

drop table if exists tag_class;
create table tag_class (
    id int primary key auto_increment comment '主键',
    class_name varchar(32) comment '分类名称',
    description varchar(255) comment '分类描述',
    create_time datetime comment '创建时间',
    update_time datetime comment '更新时间',
    status tinyint default 0 comment '分类状态,0:正常 1:删除',

    unique index uk_class_name (class_name)
)comment '标签分类表';

insert into tag_class (class_name, create_time) values
('Java', now()),
('Python', now()),
('前端', now()),
('后端', now()),
('人工智能', now()),
('开发工具', now());

drop table if exists user_vip;
create table user_vip (
    id bigint primary key auto_increment comment '主键',
    user_id bigint comment '用户id',
    vip_id int comment '会员id(预留字段)',
    expires_time datetime comment '会员到期时间',
    status tinyint default 0 comment '会员状态,0:正常 1:失效',
    create_time datetime comment '创建时间',
    update_time datetime comment '更新时间',

    index idx_user_id (user_id),
    index idx_expires_time (expires_time),
    index idx_status (status)
)comment '用户会员表';

drop table if exists posts;
create table posts (
    id bigint primary key auto_increment comment '主键',
    author_id bigint comment '作者id',
    title varchar(255) comment '标题',
    introduction text comment '简介',
    image varchar(255) comment '封面',
    status tinyint default 0 comment '状态,0:草稿 1:待审核 2:已发布 3:已删除',
    content_id bigint comment '内容id',
    type int default 0 comment '可见范围,0:公开 1:自己可见 2:粉丝可见',
    post_type tinyint default 0 comment '文章类型(0:普通文章/博客 1:新闻/资讯)',
    is_vip tinyint default 0 comment '是否会员可见,0:否 1:是',
    category_id int comment '分类id',
    create_time datetime comment '创建时间',
    update_time datetime comment '更新时间',

    index idx_author_id (author_id),
    index idx_status (status),
    index idx_category_id (category_id),
    index idx_is_vip (is_vip),
    index idx_create_time (create_time)
)comment '文章表';

drop table if exists post_tag;
create table post_tag (
    id bigint primary key auto_increment comment '主键',
    post_id bigint comment '文章id',
    tag_id int comment '标签id',
    create_time datetime comment '创建时间',

    unique index uk_post_tag (post_id, tag_id),
    index idx_post_id (post_id),
    index idx_tag_id (tag_id)
);

drop table if exists posts_content;
create table posts_content (
    id bigint primary key auto_increment comment '主键',
    content_type tinyint comment '内容类型,0:markdown 1:html',
    content longtext comment '内容'
)comment '文章内容表';

drop table if exists category;
create table category (
    id int primary key auto_increment comment '主键',
    user_id bigint comment '用户id',
    category_name varchar(32) comment '分类名称',
    image varchar(255) comment '分类封面',
    description varchar(255) comment '分类描述',
    create_time datetime comment '创建时间',
    update_time datetime comment '更新时间',

    unique index uk_category_name (category_name),
    index idx_user_id (user_id)
)comment '分类表';

drop table if exists comments;
create table comments (
    id bigint primary key auto_increment comment '主键',
    post_id bigint comment '文章id',
    user_id bigint comment '用户id',
    parent_id bigint default 0 comment '父级评论id',
    create_time datetime comment '创建时间',
    type tinyint default 0 comment '评论类型(0：文章，1：动态)',
    status tinyint default 0 comment '状态,0:正常 1:删除',
    user_name varchar(32) comment '用户名(冗余)',
    user_image varchar(255) comment '用户头像(冗余)',

    index idx_post_id (post_id),
    index idx_user_id (user_id),
    index idx_parent_id (parent_id),
    index idx_status (status)
)comment '评论表';

drop table if exists comment_content;
create table comment_content (
    id bigint primary key auto_increment comment '主键',
    comment_id bigint comment '评论id',
    content longtext comment '内容',

    unique index uk_comment_id (comment_id)
)comment '评论内容表';

drop table if exists browse;
create table browse (
    id bigint primary key auto_increment comment '主键',
    post_id bigint comment '文章id',
    user_id bigint comment '用户id',
    create_time datetime comment '浏览时间',
    type tinyint comment '类型(0:文章 1:动态)',

    index idx_post_id (post_id),
    index idx_user_id (user_id),
    index idx_create_time (create_time)
)comment '浏览记录表';

drop table if exists likes;
create table likes (
    id bigint primary key auto_increment comment '主键',
    target_id bigint comment '目标id',
    user_id bigint comment '用户id',
    type tinyint comment '目标类型,0:文章 1:动态 2:评论',
    create_time datetime comment '点赞时间',
    status tinyint default 0 comment '点赞状态,0:正常 1:取消',

    index idx_target_id (target_id),
    index idx_user_id (user_id),
    index idx_type (type),
    index idx_status (status)
)comment '点赞记录表';

drop table if exists collect;
create table collect (
    id bigint primary key auto_increment comment '主键',
    post_id bigint comment '文章id',
    user_id bigint comment '用户id',
    favorites_id int comment '收藏夹id',
    create_time datetime comment '收藏时间',
    status tinyint default 0 comment '收藏状态,0:正常 1:取消',

    index idx_post_id (post_id),
    index idx_user_id (user_id),
    index idx_status (status)
)comment '收藏记录表';

drop table if exists favorites;
create table favorites (
    id int primary key auto_increment comment '主键',
    user_id bigint comment '用户id',
    favorites_name varchar(32) comment '收藏夹名称',
    description varchar(255) comment '收藏夹描述',
    create_time datetime comment '创建时间',
    update_time datetime comment '更新时间',
    status tinyint default 0 comment '收藏夹状态,0:正常 1:删除',

    index idx_user_id (user_id)
)comment '收藏夹表';

drop table if exists notification;
create table notification (
    id bigint primary key auto_increment comment '主键',
    recipient_id bigint comment '接收者id',
    sender_id bigint comment '发送者id',
    type tinyint comment '通知类型,关联类型id',
    object_type tinyint comment '对象类型,0:文章 1:动态 2:评论 3:文本 4:图片 5:文件 6:视频 7:音频',
    object_id bigint comment '关联对象id',
    content text comment '通知内容',
#     relationship tinyint comment '关系类型,0:陌生人 1:已关注 2:粉丝 3:好友 4:官方',
    is_read tinyint default 0 comment '是否已读,0:未读 1:已读',
    is_aggregated tinyint default 0 comment '是否已聚合,0:否 1:是',
    aggregated_count int comment '聚合数量',
    expires_time datetime comment '过期时间',
    create_time datetime comment '创建时间',
    update_time datetime comment '更新时间',
    user_name varchar(32) comment '用户名(冗余)',
    user_image varchar(255) comment '用户头像(冗余)',

    KEY idx_recipient_id (recipient_id),
    KEY idx_recipient_read (recipient_id, is_read),
    KEY idx_create_time (create_time),
    KEY idx_object (object_type, object_id)
)comment '通知表';

drop table if exists notification_type;
create table notification_type (
    id tinyint primary key auto_increment comment '主键',
    type_name varchar(32) comment '类型名称',
    type_code varchar(32) comment '类型编码',
    template varchar(255) comment '通知类型模板',
    description varchar(64) comment '类型描述',
    is_active tinyint default 1 comment '是否启用,0:否 1:是',
    create_time datetime comment '创建时间',
    update_time datetime comment '更新时间'
)comment '通知类型表';

insert into notification_type (type_name, type_code, template, description) values
    ('聊天', 'user_chat', '**发来消息：***: ', '聊天信息'),
    ('评论', 'comment', '评论了你', '用户评论'),
    ('新增粉丝', 'new_fan', '***关注了你', '新增粉丝'),
    ('点赞', 'new_like', '**点赞了你的***', '点赞信息'),
    ('关注', 'new_follow', '**关注了你的***', '关注信息');

drop table if exists share;
create table share (
    id bigint primary key auto_increment comment '主键',
    author_id bigint comment '作者id',
    content text comment '动态内容',
    brief varchar(255) comment '简略描述',
    image varchar(255) comment '图片地址(可能)',
    video varchar(255) comment '视频地址(可能)',
    topic_id int comment '话题id',
    content_type tinyint comment '内容类型,0:Markdown 1:HTML 2:TEXT',
    status tinyint default 0 comment '状态,0:草稿 1:待审核 2:已发布 3:已删除',
    create_time datetime comment '创建时间',
    update_time datetime comment '更新时间',

    index idx_post_id (author_id),
    index idx_status (status),
    index idx_topic_id (topic_id)
)comment '动态表';

drop table if exists topic;
create table topic (
    id int primary key auto_increment comment '主键',
    topic_name varchar(32) comment '话题名称',
    image varchar(255) comment '话题封面',
    description varchar(255) comment '话题描述',
    create_time datetime comment '创建时间',
    update_time datetime comment '更新时间',
    status tinyint default 0 comment '状态，0:正常，1:删除',

    unique index uk_topic_name (topic_name)
)comment '话题表';

insert into topic (topic_name, image, description, create_time) values
    ('技术', 'https://picsum.photos/200/300', '技术类', now()),
    ('生活', 'https://picsum.photos/200/300', '生活类', now()),
    ('游戏', 'https://picsum.photos/200/300', '游戏类', now()),
    ('电影', 'https://picsum.photos/200/300', '电影类', now()),
    ('音乐', 'https://picsum.photos/200/300', '音乐类', now());

drop table if exists err_log;
create table err_log (
    id bigint primary key auto_increment comment '主键',
    err_type varchar(64) comment '错误类型',
    err_msg text comment '错误信息',
    err_stack text comment '错误堆栈',
    create_time datetime comment '创建时间',

    index idx_create_time (create_time),
    index idx_err_type (err_type)
)comment '错误日志表';

drop table if exists conversation;
create table conversation (
    id bigint primary key auto_increment comment '主键',
    user_id bigint comment '用户id',
    conversation_id varchar(36) comment '会话id',
    create_time datetime comment '创建时间',
    update_time datetime comment '更新时间',
    title varchar(255) comment '会话标题',

    unique index uk_user_friend (user_id, conversation_id),
    index idx_user_id (user_id),
    index idx_conversation_id (conversation_id)
)comment '会话表';

drop table if exists chat_message;
create table chat_message (
    id bigint primary key auto_increment comment '主键',
    conversation_id varchar(36) comment '会话id',
    type varchar(32) comment '消息类型 USER ASSISTANT SYSTEM TOOL',
    content text comment '消息内容',
    timestamp datetime comment '创建时间',
    file varchar(255) comment '文件地址',

    index idx_conversation_id (conversation_id)
)comment 'AI聊天消息表';

drop table if exists user_search_history;
CREATE TABLE user_search_history (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    user_id         BIGINT NOT NULL COMMENT '用户ID',
    keyword         VARCHAR(255) NOT NULL COMMENT '搜索关键词',
    search_type     VARCHAR(50) DEFAULT NULL COMMENT '搜索类型（文章 / 用户 ）',
    ip_address      VARCHAR(50) DEFAULT NULL COMMENT '用户IP',
    device          VARCHAR(50) DEFAULT NULL COMMENT '设备类型',
    result_count    INT DEFAULT 0 COMMENT '搜索结果数量',
    create_time     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '搜索时间',
    is_deleted      TINYINT DEFAULT 0 COMMENT '逻辑删除(0正常 1删除)',

    INDEX idx_user_id (user_id),
    INDEX idx_keyword (keyword),
    INDEX idx_time (create_time)
) COMMENT='用户搜索历史表';

drop table if exists work_order;
create table work_order (
    id bigint primary key auto_increment comment '主键',
    order_id char(32) comment '工单id',
    user_id bigint comment '用户id',
    target_id bigint comment '目标id',
    target_type tinyint comment '目标类型,0:文章 1:动态 2:评论 3:账号 4:建议',
    order_type tinyint comment '工单类型,枚举类型',
    reason varchar(255) comment '理由/备注',
    file_path varchar(255) comment '附件',
    status tinyint default 0 comment '状态,0:待处理 1:处理中 2:处理完成',
    create_time datetime comment '创建时间',
    update_time datetime comment '更新时间',

    index idx_report_id (order_id),
    index idx_content_id (target_id),
    index idx_type (target_type),
    index idx_order_type (order_type),
    index idx_status (status)
) comment '工单表';
