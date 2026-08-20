# 后台管理系统（Vue3 + Vite + Element Plus）

基于课程《Vue3 后台管理系统开发实战》实现的前端项目，覆盖课程 89 讲中的核心功能模块。

## 技术栈

| 技术 | 说明 |
| --- | --- |
| Vue 3.5 | Composition API + `<script setup>` 语法糖 |
| Vite 5 | 构建工具（选用 5.x 以保证 windicss 插件兼容） |
| Vuex 4 | 状态管理（用户 / 应用 / 权限） |
| Vue Router 4 | 动态路由 + 全局守卫 + 404 捕获 |
| Element Plus 2.14 | UI 组件库 + 图标 |
| WindiCSS | 原子化 CSS + `@apply` |
| @vueuse/core | 全屏、常用工具 |
| ECharts 5 | 数据可视化图表 |
| Axios | 请求封装（拦截器 / token / 错误处理） |
| axios-mock-adapter | 前端 Mock（无后端也能完整运行） |

## 快速开始

```bash
pnpm install      # 安装依赖
pnpm dev          # 启动开发服务器（http://localhost:5173）
pnpm build        # 生产构建
pnpm preview      # 预览构建产物
```

> 国内网络已配置 npmmirror 镜像（`.npmrc`）。

## 演示账号

| 账号 | 密码 | 权限 |
| --- | --- | --- |
| admin | 123456 | 超级管理员（全部菜单 + 按钮级权限 `*`） |
| editor | 123456 | 运营人员（仪表盘 / 图库 / 公告） |
| guest | 123456 | 访客（仅仪表盘，默认禁用） |

## 功能清单（对应课程章节）

### 基础架构（第 2 章）
- 创建 Vite 项目、引入 Element Plus 与 WindiCSS、`@apply` 简化样式
- 引入 vue-router 4、路由配置与 404 捕获

### 登录模块（第 3 章）
- 登录页 + 响应式处理（`[3.1]` `[3.2]`）
- 全局引入图标（`[3.3]`）、`@apply` 样式抽离（`[3.4]`）
- setup 语法糖与组合式 API（`[3.5]`）、登录表单验证（`[3.6]`）
- axios 请求库与登录接口交互（`[3.7]`）、cookie 存储 token（`[3.8]`）
- 请求/响应拦截器（`[3.9]`）、常用工具库封装（`[3.10]`）
- vuex 用户信息（`[3.11]`）、全局路由拦截登录判断（`[3.12]`）
- 退出登录（`[3.14]`）、全局 loading 进度条 nprogress（`[3.15]`）
- 动态页面标题（`[3.16]`）

### 主布局（第 4 章）
- 后台主布局（`[4.1]`）、公共头部：刷新 / 全屏 / 修改密码（`[4.2]`~`[4.4]`）
- 通用弹框表单组件封装（`[4.5]`~`[4.6]`）、组合式 API 简化代码（`[4.7]`）
- 侧边菜单：布局、路由跳转、折叠、选中关联（`[4.8]`~`[4.10]`）
- 菜单数据前后端交互（`[4.11]`）、根据菜单动态添加路由（`[4.12]`）
- 标签导航组件：同步路由与存储、关闭当前/其他/全部（`[4.13]`~`[4.17]`）
- keep-alive 页面缓存（`[4.19]`）、transition 全局过渡动画（`[4.20]`）

### 仪表盘（第 5 章）
- 统计面板组件 + 骨架屏优化（`[5.1]`~`[5.2]`）、数字滚动动画（`[5.3]`）
- ECharts 图表组件（`[5.5]`~`[5.6]`）、店铺与交易提示组件（`[5.7]`）
- `v-permission` 按钮级权限指令（`[5.8]`~`[5.9]`）

### 业务模块
- **图库管理**（第 6 章）：分类管理、图片列表分页、重命名/删除、多图上传
- **公告管理**（第 7 章）：列表分页、新增、删除、修改
- **管理员管理**（第 8 章）：分页搜索、状态切换、增删改、全局选中图库组件、组合式 API 封装
- **菜单权限**（第 9 章）：树形表格、自定义节点渲染、新增/修改、图标下拉选择、状态与删除
- **角色管理**（第 10 章）：复用代码、配置权限树形控件、默认选中渲染

## 项目结构

```
src/
├── api/             # 接口层（按模块拆分）
├── components/      # 通用组件（DialogForm / Pagination / BaseChart / ImageSelect / IconSelect / ChangePassword / ParentView）
├── composables/     # 组合式封装（useTable / useForm / useCrud）
├── directives/      # 自定义指令（v-permission）
├── layout/          # 主布局（Sidebar / Navbar / TagsView / AppMain）
├── mock/            # 前端 Mock（内存数据库 + localStorage 持久化）
├── router/          # 路由（静态 + 动态转换）
├── store/           # Vuex（user / app / permission）
├── styles/          # 全局样式
├── utils/           # 工具（request / auth / validate / index / title）
├── views/           # 页面（login / dashboard / gallery / notice / admin / menu / role / error）
├── permission.js    # 全局路由守卫
└── main.js
```

## Mock 说明

- 开发环境自动启用 `src/mock/index.js`，基于 `axios-mock-adapter` 拦截 `/api` 请求。
- 数据存于 localStorage（key: `admin_mock_db_v1`），刷新后保留；「菜单权限」页提供「重置数据库」按钮。
- 图片为本地生成的 SVG data URI，不依赖外网，离线可用。

### 接入真实后端

1. 删除 `src/main.js` 中的 `setupMock()` 调用；
2. 在 `vite.config.js` 中配置 `server.proxy`，将 `/api` 代理到后端地址；
3. 删除 `src/mock` 目录。

## 注意事项

- **WindiCSS + Vite 5**：`vite-plugin-windicss` 为老插件，与 Vite 5 兼容性最佳（Vite 6+ 存在风险）。
- **ECharts 5**：6.x 压缩包较大，在国内慢网络下安装易超时，故选用 5.6.0（API 完全兼容）。
- **动态路由**：菜单来自后端，新增/修改菜单后需**重新登录**才会刷新动态路由与侧边栏。
- **页面缓存**：keep-alive 按路由 `name` 缓存，需确保组件 `defineOptions({ name })` 与菜单路由 `name` 一致。
