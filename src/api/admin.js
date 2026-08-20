import service from '@/utils/request'

// 管理员列表（分页 + 搜索）
export function getAdminList(params) {
  return service.get('/admin/list', { params })
}

// 修改状态
export function updateAdminStatus(id, status) {
  return service.put(`/admin/status/${id}`, { status })
}

export function addAdmin(data) {
  return service.post('/admin', data)
}

export function updateAdmin(data) {
  return service.put('/admin', data)
}

export function deleteAdmin(id) {
  return service.delete(`/admin/${id}`)
}
