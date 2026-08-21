-- =====================================================================
-- 后台管理系统 数据库结构（MySQL 8.0）
-- 库名：admin_system  字符集：utf8mb4
-- 说明：该文件由 Spring Boot 启动时自动执行（幂等，可重复执行）；
--       也可手动执行：mysql -u root -p < schema.sql
-- =====================================================================

SET NAMES utf8mb4;

-- ---------------- 管理员 ----------------
CREATE TABLE IF NOT EXISTS sys_admin (
    id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    username    VARCHAR(50)  NOT NULL COMMENT '用户名',
    password    VARCHAR(100) NOT NULL COMMENT '密码（BCrypt 加密）',
    nickname    VARCHAR(50)  NOT NULL DEFAULT '' COMMENT '昵称',
    role_id     BIGINT       NOT NULL DEFAULT 2 COMMENT '角色 id',
    phone       VARCHAR(20)  NOT NULL DEFAULT '' COMMENT '手机号',
    email       VARCHAR(100) NOT NULL DEFAULT '' COMMENT '邮箱',
    avatar      VARCHAR(255) NOT NULL DEFAULT '' COMMENT '头像地址',
    status      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态 0禁用 1启用',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_username (username)
) ENGINE = InnoDB COMMENT = '管理员表';

-- ---------------- 角色 ----------------
CREATE TABLE IF NOT EXISTS sys_role (
    id          BIGINT      NOT NULL AUTO_INCREMENT COMMENT '主键',
    name        VARCHAR(50) NOT NULL COMMENT '角色名称',
    remark      VARCHAR(255) NOT NULL DEFAULT '' COMMENT '备注',
    status      TINYINT     NOT NULL DEFAULT 1 COMMENT '状态 0禁用 1启用',
    create_time DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_name (name)
) ENGINE = InnoDB COMMENT = '角色表';

-- ---------------- 菜单 ----------------
CREATE TABLE IF NOT EXISTS sys_menu (
    id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    parent_id   BIGINT       NOT NULL DEFAULT 0 COMMENT '父级 id，0 为根',
    title       VARCHAR(50)  NOT NULL COMMENT '菜单标题',
    name        VARCHAR(50)  NOT NULL DEFAULT '' COMMENT '路由 name',
    path        VARCHAR(100) NOT NULL DEFAULT '' COMMENT '路由 path',
    component   VARCHAR(100) NOT NULL DEFAULT '' COMMENT '组件路径',
    icon        VARCHAR(50)  NOT NULL DEFAULT '' COMMENT '图标',
    sort        INT          NOT NULL DEFAULT 0 COMMENT '排序',
    visible     TINYINT(1)   NOT NULL DEFAULT 1 COMMENT '是否显示 0否 1是',
    status      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态 0禁用 1启用',
    perms       VARCHAR(100) NOT NULL DEFAULT '' COMMENT '按钮权限标识',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id)
) ENGINE = InnoDB COMMENT = '菜单权限表';

-- ---------------- 角色-菜单 关联 ----------------
CREATE TABLE IF NOT EXISTS sys_role_menu (
    role_id BIGINT NOT NULL COMMENT '角色 id',
    menu_id BIGINT NOT NULL COMMENT '菜单 id',
    PRIMARY KEY (role_id, menu_id)
) ENGINE = InnoDB COMMENT = '角色菜单关联表';

-- ---------------- 图库分类 ----------------
CREATE TABLE IF NOT EXISTS gallery_category (
    id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    name        VARCHAR(50)  NOT NULL COMMENT '分类名称',
    sort        INT          NOT NULL DEFAULT 0 COMMENT '排序',
    remark      VARCHAR(255) NOT NULL DEFAULT '' COMMENT '备注',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_name (name)
) ENGINE = InnoDB COMMENT = '图库分类表';

-- ---------------- 图片 ----------------
CREATE TABLE IF NOT EXISTS gallery_image (
    id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    category_id BIGINT       NOT NULL DEFAULT 1 COMMENT '分类 id',
    name        VARCHAR(100) NOT NULL COMMENT '图片名称',
    url         VARCHAR(255) NOT NULL COMMENT '图片地址',
    size        INT          NOT NULL DEFAULT 0 COMMENT '大小(KB)',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    KEY idx_category (category_id)
) ENGINE = InnoDB COMMENT = '图片表';

-- ---------------- 公告 ----------------
CREATE TABLE IF NOT EXISTS notice (
    id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    title       VARCHAR(100) NOT NULL COMMENT '标题',
    content     TEXT         NOT NULL COMMENT '内容',
    author      VARCHAR(50)  NOT NULL DEFAULT '' COMMENT '发布人',
    status      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态 0下架 1发布',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id)
) ENGINE = InnoDB COMMENT = '公告表';

-- ---------------- 商品 ----------------
CREATE TABLE IF NOT EXISTS product (
    id          BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键',
    name        VARCHAR(100)  NOT NULL COMMENT '商品名称',
    category    VARCHAR(50)   NOT NULL DEFAULT '其他' COMMENT '分类',
    price       DECIMAL(10,2) NOT NULL DEFAULT 0 COMMENT '价格',
    stock       INT           NOT NULL DEFAULT 0 COMMENT '库存',
    image       VARCHAR(255)  NOT NULL DEFAULT '' COMMENT '图片地址',
    status      TINYINT       NOT NULL DEFAULT 1 COMMENT '状态 0下架 1上架',
    description VARCHAR(500)  NOT NULL DEFAULT '' COMMENT '描述',
    create_time DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_name (name),
    KEY idx_category (category)
) ENGINE = InnoDB COMMENT = '商品表';

-- ---------------- 会员 ----------------
CREATE TABLE IF NOT EXISTS member (
    id          BIGINT      NOT NULL AUTO_INCREMENT COMMENT '主键',
    username    VARCHAR(50) NOT NULL COMMENT '用户名',
    nickname    VARCHAR(50) NOT NULL DEFAULT '' COMMENT '昵称',
    phone       VARCHAR(20) NOT NULL DEFAULT '' COMMENT '手机号',
    create_time DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
    PRIMARY KEY (id)
) ENGINE = InnoDB COMMENT = '会员表';

-- ---------------- 店铺 ----------------
CREATE TABLE IF NOT EXISTS store (
    id          BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键',
    name        VARCHAR(50)   NOT NULL COMMENT '店铺名称',
    sales       DECIMAL(12,2) NOT NULL DEFAULT 0 COMMENT '累计销售额',
    tips        DECIMAL(12,2) NOT NULL DEFAULT 0 COMMENT '累计交易笔数',
    PRIMARY KEY (id)
) ENGINE = InnoDB COMMENT = '店铺表';

-- ---------------- 交易订单 ----------------
CREATE TABLE IF NOT EXISTS trade_order (
    id          BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键',
    order_no    VARCHAR(32)   NOT NULL COMMENT '订单号',
    store_id    BIGINT        NOT NULL DEFAULT 1 COMMENT '店铺 id',
    amount      DECIMAL(10,2) NOT NULL DEFAULT 0 COMMENT '订单金额',
    status      TINYINT       NOT NULL DEFAULT 3 COMMENT '状态 1待发货 2售后 3已完成',
    create_time DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '下单时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_order_no (order_no),
    KEY idx_create_time (create_time),
    KEY idx_status (status)
) ENGINE = InnoDB COMMENT = '交易订单表';

-- ---------------- 商品评论 ----------------
CREATE TABLE IF NOT EXISTS product_review (
    id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    product_id  BIGINT       NOT NULL DEFAULT 0 COMMENT '商品 id',
    content     VARCHAR(500) NOT NULL DEFAULT '' COMMENT '评论内容',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '评论时间',
    PRIMARY KEY (id),
    KEY idx_create_time (create_time)
) ENGINE = InnoDB COMMENT = '商品评论表';
