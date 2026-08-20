import service from '@/utils/request'

// 菜单权限树
export function getMenuList() {
  return service.get('/menu/list')
}

// 新增菜单
export function addMenu(data) {
  return service.post('/menu', data)
}

// 修改菜单
export function updateMenu(data) {
  return service.put('/menu', data)
}

// 删除菜单
export function deleteMenu(id) {
  return service.delete(`/menu/${id}`)
}

// 修改菜单状态
export function updateMenuStatus(id, status) {
  return service.put(`/menu/status/${id}`, { status })
}
