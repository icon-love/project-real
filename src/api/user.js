import service from '@/utils/request'

// 获取用户信息（含菜单权限）
export function getUserInfo() {
  return service.get('/user/info')
}

// 修改密码
export function changePassword(data) {
  return service.put('/user/password', data)
}
