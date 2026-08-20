import service from '@/utils/request'

// 角色列表
export function getRoleList(params) {
  return service.get('/role/list', { params })
}

export function addRole(data) {
  return service.post('/role', data)
}

export function updateRole(data) {
  return service.put('/role', data)
}

export function deleteRole(id) {
  return service.delete(`/role/${id}`)
}

// 获取角色已分配的权限 id 集合
export function getRolePermissionIds(id) {
  return service.get(`/role/${id}/permission-ids`)
}

// 配置角色权限
export function setRolePermissions(id, permissionIds) {
  return service.put(`/role/${id}/permission`, { permissionIds })
}
