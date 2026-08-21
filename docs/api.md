# 后台管理系统 API 接口文档

> 本文档定义了前后端约定的全部接口，是前端（Vue3）与后端（Java Spring Boot + MySQL）共同遵循的契约。
> 后端实现位于 `backend/` 目录，所有接口路径前缀均为 `/api`。

- Base URL（开发环境代理）：`http://localhost:5174/api`（Vite 代理到 `http://localhost:8080/api`）
- 数据格式：`JSON`（上传接口为 `multipart/form-data`）
- 认证方式：请求头 `Authorization: Bearer <token>`（登录接口除外）

---

## 1. 通用约定

### 1.1 统一响应结构

所有接口均返回以下 JSON 结构：

```json
{
  "code": 200,
  "message": "ok",
  "data": {}
}
```

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| `code` | int | 状态码，见下表 |
| `message` | string | 提示信息（成功/失败原因） |
| `data` | object/array/null | 业务数据，失败时为 `null` |

### 1.2 状态码

| code | 含义 | 前端行为 |
| --- | --- | --- |
| `200` | 成功 | 返回 `data` |
| `400` | 参数错误 | 弹出 `message` 错误提示 |
| `401` | 未登录 / 登录过期 | 弹出重新登录确认框并跳转登录页 |
| `403` | 无权限 | 弹出 `message` 错误提示 |
| `500` | 业务失败 / 服务器错误 | 弹出 `message` 错误提示 |

> HTTP 状态码：业务类错误统一返回 HTTP 200（`code` 在 body 中区分）；认证失败返回 HTTP 401。

### 1.3 分页参数与返回

列表接口通用分页参数（QueryString）：

| 参数 | 类型 | 默认值 | 说明 |
| --- | --- | --- | --- |
| `page` | number | 1 | 页码（从 1 开始） |
| `pageSize` | number | 10 | 每页条数 |

分页返回 `data`：

```json
{
  "list": [],
  "total": 0
}
```

### 1.4 时间字段

所有时间字段均为毫秒级时间戳（`Number`），如 `1724200000000`。

---

## 2. 认证模块

### 2.1 登录

`POST /api/login`

请求体：

```json
{ "username": "admin", "password": "123456" }
```

| 参数 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `username` | string | 是 | 用户名 |
| `password` | string | 是 | 密码 |

成功返回 `data`：

```json
{ "token": "eyJhbGciOiJIUzI1NiJ9..." }
```

失败场景：用户名或密码错误；账号被禁用。

### 2.2 退出登录

`POST /api/logout`

无请求体，返回 `data: null`。后端为无状态 JWT，直接返回成功即可。

### 2.3 获取当前用户信息（含菜单权限）

`GET /api/user/info`

需要认证。成功返回 `data`：

```json
{
  "user": {
    "id": 1,
    "username": "admin",
    "nickname": "超级管理员",
    "avatar": "/uploads/seed/avatar-admin.svg",
    "roleId": 1,
    "roleName": "超级管理员",
    "email": "admin@example.com",
    "phone": "13800000001",
    "perms": ["dashboard:view", "gallery:view", "notice:view", "system:admin", "system:menu", "system:role", "product:view", "*"]
  },
  "menus": [
    {
      "id": 1,
      "parentId": 0,
      "title": "仪表盘",
      "name": "Dashboard",
      "path": "dashboard",
      "component": "dashboard/index",
      "icon": "Odometer",
      "sort": 1,
      "visible": true,
      "status": 1,
      "perms": "dashboard:view",
      "children": []
    }
  ]
}
```

说明：

- `menus` 为**当前角色可见**的菜单树，前端据此生成动态路由与侧边栏。
- `perms` 为该角色收集到的按钮级权限标识；超级管理员（roleId=1）额外包含 `"*"`。

### 2.4 修改密码

`PUT /api/user/password`

请求体：

```json
{ "oldPassword": "123456", "newPassword": "1234567" }
```

| 参数 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `oldPassword` | string | 是 | 原密码 |
| `newPassword` | string | 是 | 新密码 |

失败场景：原密码错误；新密码与原密码相同。

---

## 3. 管理员管理

### 3.1 管理员列表

`GET /api/admin/list`

Query 参数：

| 参数 | 类型 | 说明 |
| --- | --- | --- |
| `page` / `pageSize` | number | 分页 |
| `keyword` | string | 按用户名/昵称模糊搜索 |
| `status` | number | 状态过滤（0/1），空则不过滤 |
| `roleId` | number | 角色过滤 |

返回 `data`：`{ list, total }`，其中 `list` 项为：

```json
{
  "id": 1,
  "username": "admin",
  "nickname": "超级管理员",
  "roleId": 1,
  "roleName": "超级管理员",
  "phone": "13800000001",
  "email": "admin@example.com",
  "avatar": "/uploads/seed/avatar-admin.svg",
  "status": 1,
  "createTime": 1724200000000
}
```

> 列表**不返回密码字段**。

### 3.2 修改管理员状态

`PUT /api/admin/status/{id}`

请求体：`{ "status": 0 }`（0=禁用，1=启用）

### 3.3 新增管理员

`POST /api/admin`

请求体：

```json
{
  "username": "zhangsan",
  "password": "123456",
  "nickname": "张三",
  "roleId": 2,
  "phone": "13800000000",
  "email": "zs@example.com",
  "avatar": "/uploads/seed/avatar-default.svg",
  "status": 1
}
```

失败场景：用户名已存在；用户名或密码为空。

### 3.4 修改管理员

`PUT /api/admin`

请求体：在 3.3 基础上增加 `id`；`password` 留空表示不修改。

### 3.5 删除管理员

`DELETE /api/admin/{id}`

失败场景：id=1（超级管理员）不可删除。

---

## 4. 角色管理

角色字段：`name`、`remark`、`permissionIds[]`、`status`、`createTime`。

### 4.1 角色列表

`GET /api/role/list`

返回 `data`：角色数组（含 `permissionIds`）。

```json
[
  {
    "id": 1,
    "name": "超级管理员",
    "remark": "拥有系统全部权限",
    "permissionIds": [1, 2, 3, 4, 5, 6, 7, 8],
    "status": 1,
    "createTime": 1724200000000
  }
]
```

### 4.2 新增角色

`POST /api/role`

```json
{ "name": "运营", "remark": "备注", "permissionIds": [1, 2], "status": 1 }
```

失败场景：角色名称已存在。

### 4.3 修改角色

`PUT /api/role`

支持部分字段更新：

```json
{ "id": 2, "name": "运营人员", "remark": "新备注", "status": 1 }
```

### 4.4 删除角色

`DELETE /api/role/{id}`

失败场景：id=1 不可删除；该角色下存在管理员。

### 4.5 获取角色已分配权限 id

`GET /api/role/{id}/permission-ids`

返回 `data`：菜单 id 数组，如 `[1, 2, 3, 8]`。

### 4.6 配置角色权限

`PUT /api/role/{id}/permission`

```json
{ "permissionIds": [1, 2, 3, 8] }
```

---

## 5. 菜单权限

菜单字段：`parentId`、`title`、`name`、`path`、`component`、`icon`、`sort`、`visible`、`status`、`perms`。

### 5.1 菜单树

`GET /api/menu/list`

返回 `data`：完整菜单树（嵌套 `children`，无子菜单的节点 `children: []`）。

### 5.2 新增菜单

`POST /api/menu`

```json
{
  "parentId": 0,
  "title": "订单管理",
  "name": "Order",
  "path": "order",
  "component": "order/index",
  "icon": "Tickets",
  "sort": 6,
  "visible": true,
  "status": 1,
  "perms": "order:view"
}
```

失败场景：未填写标题；父级菜单不存在。

### 5.3 修改菜单

`PUT /api/menu`

请求体：在 5.2 基础上增加 `id`。

### 5.4 删除菜单

`DELETE /api/menu/{id}`

失败场景：菜单不存在；存在子菜单（需先删除子菜单）。

### 5.5 修改菜单状态

`PUT /api/menu/status/{id}`

请求体：`{ "status": 0 }`

---

## 6. 图库管理

### 6.1 图库分类列表

`GET /api/gallery/category/list`

Query 参数：`page`、`pageSize`、`name`（模糊搜索）。

返回 `data`：`{ list, total }`，`list` 项：

```json
{
  "id": 1,
  "name": "首页轮播",
  "sort": 1,
  "remark": "首页轮播图",
  "createTime": 1724200000000
}
```

### 6.2 新增图库分类

`POST /api/gallery/category`

```json
{ "name": "营销素材", "sort": 5, "remark": "备注" }
```

失败场景：未填写名称；分类名称已存在。

### 6.3 修改图库分类

`PUT /api/gallery/category`

```json
{ "id": 5, "name": "营销素材", "sort": 5, "remark": "新备注" }
```

### 6.4 删除图库分类

`DELETE /api/gallery/category/{id}`

失败场景：该分类下存在图片。

### 6.5 图片列表

`GET /api/gallery/list`

Query 参数：`page`、`pageSize`、`categoryId`、`name`（模糊搜索）。按创建时间倒序。

返回 `data`：`{ list, total }`，`list` 项：

```json
{
  "id": 1,
  "categoryId": 1,
  "name": "banner-home-01",
  "url": "/uploads/seed/img-01.svg",
  "size": 128,
  "createTime": 1724200000000
}
```

### 6.6 上传图片

`POST /api/gallery/upload`

请求类型：`multipart/form-data`

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| `files` | file[] | 图片文件（支持多选） |
| `categoryId` | string | 分类 id |

返回 `data`：新图片记录数组（结构同 6.5 的 `list` 项）。

### 6.7 删除图片

`DELETE /api/gallery/image/{id}`

### 6.8 重命名图片

`PUT /api/gallery/image/{id}`

```json
{ "name": "新名称" }
```

---

## 7. 公告管理

公告字段：`title`、`content`、`author`、`status`、`createTime`。

### 7.1 公告列表

`GET /api/notice/list`

Query 参数：`page`、`pageSize`、`keyword`（按标题/作者模糊搜索）。按创建时间倒序。

返回 `data`：`{ list, total }`。

### 7.2 新增公告

`POST /api/notice`

```json
{ "title": "公告标题", "content": "内容", "author": "超级管理员" }
```

### 7.3 修改公告

`PUT /api/notice`

```json
{ "id": 1, "title": "新标题", "content": "新内容", "author": "超级管理员" }
```

### 7.4 删除公告

`DELETE /api/notice/{id}`

---

## 8. 商品管理

商品字段：`name`、`category`、`price`、`stock`、`image`、`status`、`description`、`createTime`。

### 8.1 商品分类列表

`GET /api/product/category/list`

返回 `data`：字符串数组，如 `["手机数码", "服饰鞋包", "食品生鲜", "家居生活", "其他"]`。

### 8.2 商品列表

`GET /api/product/list`

Query 参数：`page`、`pageSize`、`name`、`category`、`status`。按 id 倒序。

返回 `data`：`{ list, total }`，`list` 项：

```json
{
  "id": 1,
  "name": "智能手机 X1",
  "category": "手机数码",
  "price": 2999,
  "stock": 200,
  "image": "/uploads/seed/img-02.svg",
  "status": 1,
  "description": "描述文字",
  "createTime": 1724200000000
}
```

### 8.3 修改商品状态（上架/下架）

`PUT /api/product/status/{id}`

请求体：`{ "status": 0 }`

### 8.4 新增商品

`POST /api/product`

```json
{
  "name": "新商品",
  "category": "手机数码",
  "price": 999,
  "stock": 100,
  "image": "/uploads/seed/img-02.svg",
  "status": 1,
  "description": "描述"
}
```

失败场景：未填写名称；商品名称已存在。

### 8.5 修改商品

`PUT /api/product`

请求体：在 8.4 基础上增加 `id`。

### 8.6 删除商品

`DELETE /api/product/{id}`

---

## 9. 仪表盘

### 9.1 统计面板

`GET /api/dashboard/stats`

返回 `data`：统计项数组：

```json
[
  {
    "key": "sales",
    "name": "今日销售额",
    "value": 126580,
    "unit": "元",
    "icon": "Money",
    "color": "#409EFF",
    "trend": 12.5,
    "desc": "较昨日"
  },
  {
    "key": "orders",
    "name": "今日订单数",
    "value": 8842,
    "unit": "单",
    "icon": "ShoppingCart",
    "color": "#67C23A",
    "trend": -3.2,
    "desc": "较昨日"
  },
  {
    "key": "products",
    "name": "商品总数",
    "value": 1203,
    "unit": "件",
    "icon": "Goods",
    "color": "#E6A23C",
    "trend": 5.6,
    "desc": "本月新增"
  },
  {
    "key": "users",
    "name": "会员总数",
    "value": 23345,
    "unit": "人",
    "icon": "User",
    "color": "#F56C6C",
    "trend": 8.9,
    "desc": "本月新增"
  }
]
```

> `trend` 为相对上期的百分比（可正可负）；`sales/orders` 来自订单表统计，`products/users` 来自商品表、会员表统计。

### 9.2 销售趋势（折线图）

`GET /api/dashboard/sales-trend`

返回 `data`：最近 12 个月的销售额与订单数：

```json
{
  "months": ["2025-09", "2025-10", "...", "2026-08"],
  "sales": [820, 932, 901, 934, 1290, 1330, 1320, 1450, 1280, 1390, 1520, 1680],
  "orders": [620, 732, 701, 734, 890, 930, 920, 1050, 980, 1090, 1120, 1280]
}
```

### 9.3 商品分类占比（饼图）

`GET /api/dashboard/category-ratio`

返回 `data`：按商品分类统计的数量：

```json
[
  { "name": "手机数码", "value": 335 },
  { "name": "服饰鞋包", "value": 548 }
]
```

### 9.4 店铺与交易提示

`GET /api/dashboard/store-trade`

返回 `data`：

```json
{
  "stores": [
    { "id": 1, "name": "旗舰店", "sales": 45820, "tips": 3260 }
  ],
  "tips": [
    { "title": "订单待发货", "value": 26, "type": "warning" },
    { "title": "售后待处理", "value": 5, "type": "danger" },
    { "title": "商品库存预警", "value": 8, "type": "warning" },
    { "title": "今日新增评论", "value": 132, "type": "success" }
  ]
}
```

> `stores.sales` 为店铺累计销售额，`stores.tips` 为店铺累计交易笔数；`tips` 各项由订单/商品/评论表实时统计。

---

## 10. 认证与权限说明

- 除「登录」「上传资源」外，所有接口需携带 `Authorization: Bearer <token>`。
- token 由后端签发（JWT，有效期 24 小时），包含用户 id。
- 菜单可见性 = 角色拥有的菜单权限（`sys_role_menu`）过滤后的菜单树；按钮权限由菜单上的 `perms` 字段收集。

## 11. 演示账号

| 账号 | 密码 | 角色 |
| --- | --- | --- |
| admin | 123456 | 超级管理员（全部权限） |
| editor | 123456 | 运营人员 |
| guest | 123456 | 访客（默认禁用） |
