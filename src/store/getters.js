const getters = {
  token: (state) => state.user.token,
  userInfo: (state) => state.user.userInfo,
  sidebar: (state) => state.app.sidebar,
  device: (state) => state.app.device,
  visitedViews: (state) => state.app.visitedViews,
  cachedViews: (state) => state.app.cachedViews,
  permissionRoutes: (state) => state.permission.routes,
  permissionMenus: (state) => state.permission.menus
}

export default getters
