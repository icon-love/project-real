import service from '@/utils/request'

// 统计面板数据
export function getDashboardStats() {
  return service.get('/dashboard/stats')
}

// 销售趋势（折线图）
export function getSalesTrend() {
  return service.get('/dashboard/sales-trend')
}

// 商品分类占比（饼图）
export function getCategoryRatio() {
  return service.get('/dashboard/category-ratio')
}

// 店铺与交易提示
export function getStoreTrade() {
  return service.get('/dashboard/store-trade')
}
