import service from '@/utils/request'

// 公告列表（分页）
export function getNoticeList(params) {
  return service.get('/notice/list', { params })
}

export function addNotice(data) {
  return service.post('/notice', data)
}

export function updateNotice(data) {
  return service.put('/notice', data)
}

export function deleteNotice(id) {
  return service.delete(`/notice/${id}`)
}
