import service from '@/utils/request'

// 登录
export function login(data) {
  return service.post('/login', data)
}

// 退出登录
export function logout() {
  return service.post('/logout')
}
