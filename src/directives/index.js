import permission from './permission'

export function setupDirectives(app) {
  // 按钮级权限
  app.directive('permission', permission)
}
