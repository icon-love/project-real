/**
 * mock 响应辅助函数
 */

// 成功响应
export function ok(data = null, message = 'ok') {
  return [200, { code: 200, data, message }]
}

// 失败响应（业务错误码）
export function fail(message = '操作失败') {
  return [200, { code: 500, data: null, message }]
}

// 数字转换
export function num(value, defaultValue = 1) {
  const n = Number(value)
  return Number.isNaN(n) ? defaultValue : n
}

// 解析 JSON body
export function body(config) {
  try {
    return JSON.parse(config.data || '{}')
  } catch (e) {
    return {}
  }
}

// 解析 query 参数（axios 会放在 config.params）
export function parseParams(config) {
  return { ...(config.params || {}) }
}
