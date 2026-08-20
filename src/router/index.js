import { createRouter, createWebHistory } from 'vue-router'
import Layout from '@/layout/index.vue'

/**
 * 静态路由：登录、404、以及承载动态子路由的根布局
 */
export const constantRoutes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { title: '登录', hidden: true }
  },
  {
    path: '/404',
    name: 'NotFound',
    component: () => import('@/views/error/404.vue'),
    meta: { title: '404', hidden: true }
  },
  {
    path: '/',
    name: 'Layout',
    component: Layout,
    redirect: '/dashboard',
    // 注意：不要在这里设置 meta.hidden = true。
    // route.meta 是匹配记录 meta 的合并结果，会导致所有页面被误判为 hidden。
    children: [] // 动态路由挂载到此处
  }
]

function createRouterInstance() {
  return createRouter({
    history: createWebHistory(),
    routes: constantRoutes,
    scrollBehavior: () => ({ top: 0 })
  })
}

const router = createRouterInstance()

// 静态路由 name 集合，resetRouter 时用于识别需保留的路由
const STATIC_ROUTE_NAMES = new Set(['Login', 'NotFound', 'Layout'])

// 记录所有动态添加路由的移除函数
// 注意：vue-router4 中 matcher 是 createRouter 内部的闭包变量，
// router.matcher 不存在（undefined），不能像 vue-router3 那样通过
// router.matcher = xxx 重建路由表。必须用 addRoute 返回的移除函数。
const dynamicRouteRemovers = []

/**
 * 动态添加路由（挂载到 Layout 下，并追加兜底 404）
 * 返回 addRoute 生成的移除函数，供 resetRouter 清理
 * @param {Array} routes 由 convertMenusToRoutes 生成的动态路由
 */
export function addDynamicRoutes(routes) {
  routes.forEach((route) => {
    dynamicRouteRemovers.push(router.addRoute('Layout', route))
  })
  // 兜底 404（未命名路由无法用 removeRoute(name)，需用移除函数）
  dynamicRouteRemovers.push(
    router.addRoute({
      path: '/:pathMatch(.*)*',
      redirect: '/404',
      meta: { hidden: true }
    })
  )
}

// 退出登录时重置路由，清除所有动态添加的路由
export function resetRouter() {
  // 1. 调用 addRoute 返回的移除函数，精确移除动态路由
  while (dynamicRouteRemovers.length) {
    const remove = dynamicRouteRemovers.pop()
    remove && remove()
  }
  // 2. 兜底：移除所有非静态命名路由，防止残留
  router.getRoutes().forEach((route) => {
    if (route.name && !STATIC_ROUTE_NAMES.has(route.name)) {
      router.removeRoute(route.name)
    }
  })
}

export default router
