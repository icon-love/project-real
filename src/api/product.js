import service from '@/utils/request'

// 商品分类列表
export function getProductCategoryList() {
  return service.get('/product/category/list')
}

// 商品列表（分页 + 搜索）
export function getProductList(params) {
  return service.get('/product/list', { params })
}

// 修改状态（上架/下架）
export function updateProductStatus(id, status) {
  return service.put(`/product/status/${id}`, { status })
}

export function addProduct(data) {
  return service.post('/product', data)
}

export function updateProduct(data) {
  return service.put('/product', data)
}

export function deleteProduct(id) {
  return service.delete(`/product/${id}`)
}
