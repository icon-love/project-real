import MockAdapter from 'axios-mock-adapter'
import service from '@/utils/request'
import registerLogin from './modules/login'
import registerMenu from './modules/menu'
import registerGallery from './modules/gallery'
import registerNotice from './modules/notice'
import registerAdmin from './modules/admin'
import registerRole from './modules/role'
import registerDashboard from './modules/dashboard'
import registerProduct from './modules/product'

/**
 * 开发环境 mock：拦截 axios 请求，返回模拟数据。
 * 接入真实后端时，删除 main.js 中的 setupMock() 调用并配置 vite proxy 即可。
 */
let mock = null

export function setupMock() {
  if (mock) return
  mock = new MockAdapter(service, { delayResponse: 300 })

  registerLogin(mock)
  registerMenu(mock)
  registerGallery(mock)
  registerNotice(mock)
  registerAdmin(mock)
  registerRole(mock)
  registerDashboard(mock)
  registerProduct(mock)

  // 未匹配的请求放行（例如图片等静态资源）
  mock.onAny().passThrough()
}
