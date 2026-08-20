/**
 * 通用工具函数库
 */

/**
 * 时间格式化
 * @param {Date|string|number} time 时间
 * @param {string} cFormat 格式，默认 '{y}-{m}-{d} {h}:{i}:{s}'
 */
export function formatTime(time, cFormat = '{y}-{m}-{d} {h}:{i}:{s}') {
  if (!time) return ''
  const date = typeof time === 'object' ? time : new Date(time)
  const formatObj = {
    y: date.getFullYear(),
    m: date.getMonth() + 1,
    d: date.getDate(),
    h: date.getHours(),
    i: date.getMinutes(),
    s: date.getSeconds(),
    a: date.getDay()
  }
  const timeStr = cFormat.replace(/{(y|m|d|h|i|s|a)+}/g, (result, key) => {
    let value = formatObj[key]
    if (key === 'a') {
      return ['日', '一', '二', '三', '四', '五', '六'][value]
    }
    if (result.length > 0 && value < 10) {
      value = '0' + value
    }
    return value || 0
  })
  return timeStr
}

/** 深拷贝 */
export function deepClone(source) {
  if (source === null || typeof source !== 'object') return source
  return JSON.parse(JSON.stringify(source))
}

/** 是否为空（'' / null / undefined / [] / {}） */
export function isEmpty(value) {
  if (value === null || value === undefined || value === '') return true
  if (Array.isArray(value) && value.length === 0) return true
  if (typeof value === 'object' && Object.keys(value).length === 0) return true
  return false
}

/** 生成唯一 id */
export function generateId(prefix = '') {
  return (
    prefix +
    Date.now().toString(36) +
    Math.random().toString(36).slice(2, 8)
  )
}

/** 数字千分位 */
export function thousand(num) {
  return String(num || 0).replace(/\B(?=(\d{3})+(?!\d))/g, ',')
}

/** 触发浏览器下载 */
export function downloadFile(content, filename) {
  const blob =
    content instanceof Blob
      ? content
      : new Blob([content], { type: 'application/octet-stream' })
  const url = URL.createObjectURL(blob)
  const link = document.createElement('a')
  link.href = url
  link.download = filename
  link.click()
  URL.revokeObjectURL(url)
}
