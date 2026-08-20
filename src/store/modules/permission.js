import { convertMenusToRoutes } from '@/router/dynamic'

const state = {
  // 已生成的动态路由表（用于侧边栏渲染）
  routes: [],
  // 后端返回的原始菜单树
  menus: []
}

const mutations = {
  SET_ROUTES: (state, routes) => {
    state.routes = routes
  },
  SET_MENUS: (state, menus) => {
    state.menus = menus
  },
  // 退出登录/重置 token 时清空，确保重新登录会重新生成动态路由
  RESET: (state) => {
    state.routes = []
    state.menus = []
  }
}

const actions = {
  /**
   * 根据菜单生成动态路由
   * @returns {Promise<Array>} 路由表
   */
  generateRoutes({ commit }, menus) {
    return new Promise((resolve) => {
      const routes = convertMenusToRoutes(menus)
      commit('SET_ROUTES', routes)
      commit('SET_MENUS', menus)
      resolve(routes)
    })
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}
