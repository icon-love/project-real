import { createApp } from 'vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import zhCn from 'element-plus/es/locale/lang/zh-cn'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import 'virtual:windi.css'
import '@/styles/index.scss'

import App from './App.vue'
import router from './router'
import store from './store'
import { setupDirectives } from '@/directives'
import { setupMock } from '@/mock'
import '@/permission'

const app = createApp(App)

// 全局注册 Element Plus 图标组件
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

app.use(store)
app.use(router)
app.use(ElementPlus, { locale: zhCn })
setupDirectives(app)

// 默认启用 mock（开发/生产环境均生效，纯前端演示可正常部署上线）。
// 接入真实后端时，构建命令带上 VITE_USE_MOCK=false 即可关闭，
// 并在服务器（如 Nginx）上把 /api 代理到后端地址。
if (import.meta.env.VITE_USE_MOCK !== 'false') {
  setupMock()
}

app.mount('#app')
