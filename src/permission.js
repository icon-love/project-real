import router, { addDynamicRoutes } from '@/router'
import store from '@/store'
import { getToken, removeToken } from '@/utils/auth'
import { getPageTitle } from '@/utils/title'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'
import { ElMessage } from 'element-plus'

NProgress.configure({ showSpinner: false })

// 免登录白名单
const WHITE_LIST = ['/login', '/404']

router.beforeEach(async (to, from, next) => {
  NProgress.start()

  // 动态页面标题
  const matchedTitle = to.matched.reduce(
    (acc, record) => acc || record.meta?.title,
    ''
  )
  document.title = getPageTitle(matchedTitle)

  const token = getToken()

  if (token) {
    if (to.path === '/login') {
      // 已登录，访问登录页 -> 跳转首页
      next({ path: '/' })
      NProgress.done()
      return
    }

    if (store.getters.permissionRoutes.length === 0) {
      // 尚未加载用户信息与动态路由
      try {
        const info = await store.dispatch('user/getInfo')
        const routes = await store.dispatch(
          'permission/generateRoutes',
          info.menus
        )
        // 动态添加路由到 Layout 下（含兜底 404），并记录移除函数
        addDynamicRoutes(routes)
        next({ ...to, replace: true })
      } catch (error) {
        // 获取用户信息失败 -> 清理登录态
        store.dispatch('user/resetToken')
        ElMessage.error(error?.message || '获取用户信息失败，请重新登录')
        next(`/login?redirect=${to.path}`)
        NProgress.done()
      }
    } else {
      next()
    }
  } else {
    if (WHITE_LIST.includes(to.path)) {
      next()
    } else {
      next(`/login?redirect=${to.path}`)
      NProgress.done()
    }
  }
})

router.afterEach(() => {
  NProgress.done()
})
