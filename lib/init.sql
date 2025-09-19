drop database if exists cloud_blog;

create database cloud_blog;
use cloud_blog;

drop table if exists user;
create table user (
    id bigint primary key auto_increment comment '用户id',
    user_account varchar(32) comment '用户账号',
    password varchar(255) comment '用户密码',
    status tinyint default 0 comment '用户状态,0:正常 1：失效',
    last_login_time datetime comment '最后登录时间',
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
    phone varchar(32) comment '手机号',
    email varchar(64) comment '邮箱',
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

drop table if exists user_focus;
create table user_focus (
    id bigint primary key auto_increment comment '主键',
    user_id bigint comment '用户id',
    focus_user_id bigint comment '关注用户id',
    create_time datetime comment '创建时间',

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
    description varchar(64) comment '标签描述',
    create_time datetime comment '创建时间',
    update_time datetime comment '更新时间',

    unique index uk_tag_name (tag_name)
)comment '标签表';

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
    status tinyint default 0 comment '状态,0:正常 1:删除',
    content_id bigint comment '内容id',
    type int default 0 comment '类型(扩展)',
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
    category_name varchar(32) comment '分类名称',
    image varchar(255) comment '分类封面',
    description varchar(255) comment '分类描述',
    create_time datetime comment '创建时间',
    update_time datetime comment '更新时间',

    unique index uk_category_name (category_name)
)comment '分类表';

drop table if exists comments;
create table comments (
    id bigint primary key auto_increment comment '主键',
    post_id bigint comment '文章id',
    user_id bigint comment '用户id',
    parent_id bigint default 0 comment '父级评论id',
    create_time datetime comment '创建时间',
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

    index idx_post_id (post_id),
    index idx_user_id (user_id),
    index idx_create_time (create_time)
)comment '浏览记录表';

drop table if exists likes;
create table likes (
    id bigint primary key auto_increment comment '主键',
    target_id bigint comment '目标id',
    user_id bigint comment '用户id',
    type tinyint comment '目标类型,0:文章 1:评论',
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
    create_time datetime comment '收藏时间',
    status tinyint default 0 comment '收藏状态,0:正常 1:取消',

    index idx_post_id (post_id),
    index idx_user_id (user_id),
    index idx_status (status)
)comment '收藏记录表';