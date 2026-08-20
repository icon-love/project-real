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

// 开发环境启用 mock（接入真实后端时移除即可）
if (import.meta.env.DEV) {
  setupMock()
}

app.mount('#app')
