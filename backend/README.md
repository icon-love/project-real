# 后台管理系统后端（Spring Boot 3 + MyBatis-Plus + MySQL）

## 技术栈

| 技术 | 说明 |
| --- | --- |
| Spring Boot 3.3.5 | Web / 参数校验 |
| MyBatis-Plus 3.5.7 | ORM + 分页插件 |
| MySQL 8.0 | 数据库（库名 `admin_system`，自动建库建表） |
| JJWT 0.12.6 | 登录 token（有效期 24h） |
| spring-security-crypto | BCrypt 密码加密（未引入完整安全框架） |
| Java 17 | 运行环境 |

## 环境要求

- JDK 17+
- Maven 3.6+
- MySQL 8.0（本机 3306，需可创建数据库的账号）

## 快速开始

```bash
# 方式一（推荐）：一键启动（自动设置数据库密码，无需手动 export）
#   Windows: 双击 start-dev.cmd 或右键 start-dev.ps1 -> 使用 PowerShell 运行
.\start-dev.ps1

# 方式二：手动设置环境变量后启动
#    Windows PowerShell:
$env:DB_HOST = "localhost"
$env:DB_PORT = "3306"
$env:DB_NAME = "admin_system"
$env:DB_USERNAME = "root"
$env:DB_PASSWORD = "123456"        # ← 必填，按实际修改

mvn spring-boot:run
```

> 注意：每次**新开终端**都需要重新设置 `DB_PASSWORD`（环境变量不会跨终端保留），
> 否则会因空密码连不上 MySQL 而启动失败（`Application run failed`）。
> 建议直接用 `start-dev.cmd` / `start-dev.ps1` 一键启动。

> 国内网络下若依赖下载慢，可在 `~/.m2/settings.xml` 配置阿里云镜像：
> `<mirror><id>aliyun</id><mirrorOf>central</mirrorOf><url>https://maven.aliyun.com/repository/public</url></mirror>`

启动日志出现 `种子数据初始化完成` 即代表初始化成功；若 `sys_admin` 表已有数据则自动跳过。

## 演示账号（种子数据，密码均为 123456）

| 账号 | 角色 | 说明 |
| --- | --- | --- |
| admin | 超级管理员 | 全部菜单 + 按钮权限 `*` |
| editor | 运营人员 | 仪表盘 / 图库 / 公告 / 商品 |
| guest | 访客 | 仅仪表盘（默认禁用，无法登录） |

## 种子数据说明

后端在**空库**启动时通过 `com.admin.config.DataInitializer` 自动写入演示数据（幂等，非空则跳过）：

- 菜单权限树（8 条，与前端 mock 完全一致，id 1-8）
- 角色（超级管理员 / 运营人员 / 访客）+ 角色-菜单关联
- 管理员（admin / editor / guest，BCrypt 加密）
- 图库分类 + SVG 占位图片（写入 `uploads/` 目录并入库）
- 公告 / 商品 / 会员 / 店铺
- 交易订单（覆盖最近 12 个月，含今日，保证仪表盘趋势与今日统计有数据）
- 商品评论

## 目录结构

```
backend/
├── pom.xml
├── sql/                          # 建表脚本（已移除，见 src/main/resources/sql）
└── src/main/
    ├── java/com/admin/
    │   ├── common/               # Result / 异常 / JWT / 拦截器 / UserContext
    │   ├── config/               # Web / MyBatis-Plus / Bean / DataInitializer
    │   ├── controller/           # 接口层（/api/**）
    │   ├── dto/                  # 请求参数
    │   ├── entity/               # 实体
    │   ├── mapper/               # MyBatis-Plus Mapper
    │   └── service/              # 业务层
    └── resources/
        ├── application.yml
        └── sql/schema.sql        # 建表脚本（classpath，启动自动执行）
```

## 与前端联调

前端默认启用 mock。接入真实后端时：

1. `src/main.js` 中 `setupMock()` 改为按环境变量控制（`VITE_USE_MOCK=false` 时关闭）；
2. `vite.config.js` 取消 `server.proxy` 中 `/api → http://localhost:8080` 的注释；
3. 请求头携带 `Authorization: Bearer <token>`（登录接口除外）。

接口契约见根目录 `docs/api.md`。
