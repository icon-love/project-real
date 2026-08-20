import { login, logout } from '@/api/login'
import { getUserInfo } from '@/api/user'
import { getToken, setToken, removeToken } from '@/utils/auth'
import { resetRouter } from '@/router'

const state = {
  token: getToken(),
  userInfo: {}
}

const mutations = {
  SET_TOKEN: (state, token) => {
    state.token = token
  },
  SET_USER_INFO: (state, info) => {
    state.userInfo = info
  }
}

const actions = {
  // 登录
  login({ commit }, userInfo) {
    return new Promise((resolve, reject) => {
      login(userInfo)
        .then((data) => {
          commit('SET_TOKEN', data.token)
          setToken(data.token)
          resolve()
        })
        .catch(reject)
    })
  },

  // 获取用户信息与菜单
  getInfo({ commit }) {
    return new Promise((resolve, reject) => {
      getUserInfo()
        .then((data) => {
          commit('SET_USER_INFO', data.user)
          resolve(data)
        })
        .catch(reject)
    })
  },

  // 退出登录
  logout({ commit }) {
    return new Promise((resolve, reject) => {
      logout()
        .then(() => {
          commit('SET_TOKEN', '')
          commit('SET_USER_INFO', {})
          removeToken()
          resetRouter()
          // 清理动态路由相关的权限状态（否则重新登录会跳过路由重建）
          commit('permission/RESET', null, { root: true })
          // 清理标签导航
          commit('app/DEL_ALL_VISITED_VIEWS', null, { root: true })
          commit('app/DEL_ALL_CACHED_VIEWS', null, { root: true })
          resolve()
        })
        .catch(reject)
    })
  },

  // 重置 token（例如密码过期等场景）
  resetToken({ commit }) {
    return new Promise((resolve) => {
      commit('SET_TOKEN', '')
      commit('SET_USER_INFO', {})
      removeToken()
      // 同步清理动态路由状态，避免重新登录时跳过路由重建
      commit('permission/RESET', null, { root: true })
      resolve()
    })
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}
