import { ElMessage, ElMessageBox } from 'element-plus'

/**
 * 修改状态 / 删除 封装（对应课程：组合式API特性封装-修改状态和删除）
 *
 * @param {Object} options
 *  - statusApi   (id, status) => Promise
 *  - deleteApi   (id) => Promise
 *  - afterStatus 状态修改成功回调（如刷新列表）
 *  - afterDelete 删除成功回调（如刷新列表）
 */
export function useCrud(options = {}) {
  const { statusApi, deleteApi, afterStatus, afterDelete } = options

  // 状态切换
  async function handleStatusChange(row) {
    try {
      await statusApi(row.id, row.status)
      ElMessage.success('状态修改成功')
      afterStatus && afterStatus()
    } catch (e) {
      // 失败时回滚开关状态
      row.status = row.status === 1 ? 0 : 1
    }
  }

  // 删除（带确认框）
  async function handleDelete(row, text = '此操作将删除该数据，是否继续？') {
    try {
      await ElMessageBox.confirm(text, '系统提示', {
        type: 'warning',
        confirmButtonText: '确定',
        cancelButtonText: '取消'
      })
    } catch (e) {
      return
    }
    try {
      await deleteApi(row.id)
      ElMessage.success('删除成功')
      afterDelete && afterDelete()
    } catch (e) {
      // 错误已在拦截器中提示
    }
  }

  return { handleStatusChange, handleDelete }
}
