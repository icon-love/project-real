import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getToken } from '@/utils/auth'

// 创建 axios 实例
const service = axios.create({
  baseURL: '/api',
  timeout: 15000
})

// ---------- 请求拦截器 ----------
service.interceptors.request.use(
  (config) => {
    const token = getToken()
    if (token) {
      config.headers['Authorization'] = 'Bearer ' + token
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// ---------- 响应拦截器 ----------
let isRelogin = false

function handleTokenExpired() {
  if (isRelogin) return
  isRelogin = true
  ElMessageBox.confirm('登录状态已过期，请重新登录', '系统提示', {
    confirmButtonText: '重新登录',
    cancelButtonText: '取消',
    type: 'warning'
  })
    .then(() => {
      localStorage.removeItem('admin_user_info')
      window.location.href = '/login'
    })
    .finally(() => {
      isRelogin = false
    })
}

service.interceptors.response.use(
  (response) => {
    const res = response.data
    // 二进制流直接返回
    if (response.request.responseType === 'blob') {
      return response
    }
    // 业务码
    if (res.code !== 200) {
      if (res.code === 401) {
        handleTokenExpired()
      } else {
        ElMessage.error(res.message || '请求出错')
      }
      return Promise.reject(new Error(res.message || 'Error'))
    }
    return res.data
  },
  (error) => {
    const status = error.response?.status
    if (status === 401) {
      handleTokenExpired()
    } else {
      ElMessage.error(error.response?.data?.message || error.message || '网络错误')
    }
    return Promise.reject(error)
  }
)

export default service
