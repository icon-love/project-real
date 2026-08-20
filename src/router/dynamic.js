import ParentView from '@/components/ParentView.vue'

// 批量引入 views 下所有页面（注意：glob 不支持别名，需用相对路径）
const viewModules = import.meta.glob('../views/**/*.vue')

/**
 * 根据后端返回的 component 路径加载对应组件
 * @param {string} view 例如 'dashboard/index'
 */
export function loadView(view) {
  const key = `../views/${view}.vue`
  return viewModules[key] || (() => import('../views/error/404.vue'))
}

/**
 * 将后端菜单树转换为路由表
 * - 含子菜单的节点 -> 分组路由（ParentView 容器）
 * - 叶子节点 -> 页面路由（懒加载组件）
 */
export function convertMenusToRoutes(menus = []) {
  const routes = []
  for (const menu of menus) {
    if (menu.status === 0) continue
    const route = {
      path: menu.path || '',
      name: menu.name || `Menu_${menu.id}`,
      meta: {
        title: menu.title || '未命名',
        icon: menu.icon || '',
        visible: menu.visible !== false,
        keepAlive: true,
        perms: menu.perms || ''
      },
      children: []
    }
    if (menu.children && menu.children.length) {
      route.component = ParentView
      route.children = convertMenusToRoutes(menu.children)
      if (!route.children.length) continue
    } else {
      route.component = menu.component ? loadView(menu.component) : () => import('../views/error/404.vue')
    }
    routes.push(route)
  }
  return routes
}
