import store from '@/store'

/**
 * 按钮级权限指令
 * 用法：<el-button v-permission="['system:admin']">删除</el-button>
 * 超级管理员（perms 包含 '*'）默认拥有全部权限
 */
function checkPermission(el, binding) {
  const { value } = binding
  const perms = store.getters.userInfo?.perms || []
  if (perms.includes('*')) return

  if (value && Array.isArray(value) && value.length) {
    const hasPermission = value.some((p) => perms.includes(p))
    if (!hasPermission) {
      el.parentNode && el.parentNode.removeChild(el)
    }
  } else {
    throw new Error('请设置操作权限标签值，如 v-permission="[\'system:admin\']"')
  }
}

export default {
  mounted(el, binding) {
    checkPermission(el, binding)
  },
  updated(el, binding) {
    checkPermission(el, binding)
  }
}
