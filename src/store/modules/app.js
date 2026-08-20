const TAGS_KEY = 'admin_tags_views'

function loadTags() {
  try {
    return JSON.parse(localStorage.getItem(TAGS_KEY)) || []
  } catch (e) {
    return []
  }
}

function saveTags(views) {
  try {
    localStorage.setItem(TAGS_KEY, JSON.stringify(views))
  } catch (e) {
    /* ignore */
  }
}

const state = {
  // 侧边栏
  sidebar: { opened: true, withoutAnimation: false },
  device: 'desktop',
  // 标签导航
  visitedViews: loadTags(),
  cachedViews: []
}

const mutations = {
  TOGGLE_SIDEBAR: (state) => {
    state.sidebar.opened = !state.sidebar.opened
    state.sidebar.withoutAnimation = false
  },
  CLOSE_SIDEBAR: (state, withoutAnimation) => {
    state.sidebar.opened = false
    state.sidebar.withoutAnimation = withoutAnimation
  },
  TOGGLE_DEVICE: (state, device) => {
    state.device = device
  },

  ADD_VISITED_VIEW: (state, view) => {
    if (state.visitedViews.some((v) => v.path === view.path)) return
    // 仅保留可序列化字段（避免把路由组件等写入 localStorage）
    const tag = {
      path: view.path,
      fullPath: view.fullPath || view.path,
      name: view.name || '',
      query: view.query || {},
      meta: view.meta || {}
    }
    tag.title = tag.meta.title || '未命名'
    state.visitedViews.push(tag)
    saveTags(state.visitedViews)
  },
  ADD_CACHED_VIEW: (state, view) => {
    if (!view.name) return
    if (view.meta?.keepAlive && !state.cachedViews.includes(view.name)) {
      state.cachedViews.push(view.name)
    }
  },
  DEL_VISITED_VIEW: (state, view) => {
    for (let i = 0; i < state.visitedViews.length; i++) {
      if (state.visitedViews[i].path === view.path) {
        state.visitedViews.splice(i, 1)
        break
      }
    }
    saveTags(state.visitedViews)
  },
  DEL_CACHED_VIEW: (state, view) => {
    const index = state.cachedViews.indexOf(view.name)
    if (index > -1) state.cachedViews.splice(index, 1)
  },
  DEL_OTHERS_VISITED_VIEWS: (state, view) => {
    state.visitedViews = state.visitedViews.filter(
      (v) => v.path === view.path || v.meta?.affix
    )
    saveTags(state.visitedViews)
  },
  DEL_OTHERS_CACHED_VIEWS: (state, view) => {
    const index = state.cachedViews.indexOf(view.name)
    if (index > -1) {
      state.cachedViews = state.cachedViews.slice(index, index + 1)
    } else {
      state.cachedViews = []
    }
  },
  DEL_ALL_VISITED_VIEWS: (state) => {
    state.visitedViews = state.visitedViews.filter((v) => v.meta?.affix)
    saveTags(state.visitedViews)
  },
  DEL_ALL_CACHED_VIEWS: (state) => {
    state.cachedViews = []
  }
}

const actions = {
  toggleSidebar({ commit }) {
    commit('TOGGLE_SIDEBAR')
  },
  closeSidebar({ commit }, { withoutAnimation }) {
    commit('CLOSE_SIDEBAR', withoutAnimation)
  },
  toggleDevice({ commit }, device) {
    commit('TOGGLE_DEVICE', device)
  },

  // ---------- 标签导航 ----------
  addView({ dispatch }, view) {
    dispatch('addVisitedView', view)
    dispatch('addCachedView', view)
  },
  addVisitedView({ commit }, view) {
    commit('ADD_VISITED_VIEW', view)
  },
  addCachedView({ commit }, view) {
    commit('ADD_CACHED_VIEW', view)
  },

  delView({ dispatch, state }, view) {
    return new Promise((resolve) => {
      dispatch('delVisitedView', view)
      dispatch('delCachedView', view)
      resolve({
        visitedViews: [...state.visitedViews],
        cachedViews: [...state.cachedViews]
      })
    })
  },
  delVisitedView({ commit, state }, view) {
    return new Promise((resolve) => {
      commit('DEL_VISITED_VIEW', view)
      resolve([...state.visitedViews])
    })
  },
  delCachedView({ commit, state }, view) {
    return new Promise((resolve) => {
      commit('DEL_CACHED_VIEW', view)
      resolve([...state.cachedViews])
    })
  },

  delOthersViews({ dispatch, state }, view) {
    return new Promise((resolve) => {
      dispatch('delOthersVisitedViews', view)
      dispatch('delOthersCachedViews', view)
      resolve({
        visitedViews: [...state.visitedViews],
        cachedViews: [...state.cachedViews]
      })
    })
  },
  delOthersVisitedViews({ commit, state }, view) {
    return new Promise((resolve) => {
      commit('DEL_OTHERS_VISITED_VIEWS', view)
      resolve([...state.visitedViews])
    })
  },
  delOthersCachedViews({ commit, state }, view) {
    return new Promise((resolve) => {
      commit('DEL_OTHERS_CACHED_VIEWS', view)
      resolve([...state.cachedViews])
    })
  },

  delAllViews({ dispatch, state }) {
    return new Promise((resolve) => {
      dispatch('delAllVisitedViews')
      dispatch('delAllCachedViews')
      resolve({
        visitedViews: [...state.visitedViews],
        cachedViews: [...state.cachedViews]
      })
    })
  },
  delAllVisitedViews({ commit, state }) {
    return new Promise((resolve) => {
      commit('DEL_ALL_VISITED_VIEWS')
      resolve([...state.visitedViews])
    })
  },
  delAllCachedViews({ commit, state }) {
    return new Promise((resolve) => {
      commit('DEL_ALL_CACHED_VIEWS')
      resolve([...state.cachedViews])
    })
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}
