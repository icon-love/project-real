import service from '@/utils/request'

// ---------- 图库分类 ----------
export function getGalleryCategoryList(params) {
  return service.get('/gallery/category/list', { params })
}

export function addGalleryCategory(data) {
  return service.post('/gallery/category', data)
}

export function updateGalleryCategory(data) {
  return service.put('/gallery/category', data)
}

export function deleteGalleryCategory(id) {
  return service.delete(`/gallery/category/${id}`)
}

// ---------- 图片 ----------
export function getGalleryList(params) {
  return service.get('/gallery/list', { params })
}

// 上传图片（multipart）
// 注意：不要手动设置 Content-Type，交由浏览器自动生成 multipart boundary，否则后端解析失败
export function uploadImages(data) {
  return service.post('/gallery/upload', data)
}

export function deleteGalleryImage(id) {
  return service.delete(`/gallery/image/${id}`)
}

export function renameGalleryImage(id, name) {
  return service.put(`/gallery/image/${id}`, { name })
}
