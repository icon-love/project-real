import Cookies from 'js-cookie'

// 基于 js-cookie 的统一封装，便于后续扩展
const Cookie = {
  get: (key) => Cookies.get(key),
  set: (key, value, options = {}) => Cookies.set(key, value, options),
  remove: (key, options = {}) => Cookies.remove(key, options)
}

export default Cookie
