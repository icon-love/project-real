import { reactive, ref } from 'vue'

/**
 * 列表分页搜索封装（对应课程：组合式API特性封装-列表分页搜索）
 *
 * @param {Function} api 请求函数 (params) => Promise<{ list, total }>
 * @param {Object} options
 *  - defaultQuery 默认查询条件
 *  - immediate    初始化是否立即请求（默认 true）
 */
export function useTable(api, options = {}) {
  const { defaultQuery = {}, immediate = true } = options

  const loading = ref(false)
  const list = ref([])
  const total = ref(0)
  const query = reactive({ page: 1, pageSize: 10, ...defaultQuery })

  async function getList() {
    loading.value = true
    try {
      const data = await api({ ...query })
      list.value = data.list || []
      total.value = data.total || 0
    } catch (e) {
      // 错误已在响应拦截器中提示
    } finally {
      loading.value = false
    }
  }

  // 搜索（回到第一页）
  function handleSearch() {
    query.page = 1
    getList()
  }

  // 重置查询条件
  function handleReset() {
    Object.keys(query).forEach((key) => {
      if (key !== 'page' && key !== 'pageSize') {
        query[key] = defaultQuery[key] ?? ''
      }
    })
    handleSearch()
  }

  function handlePageChange(page) {
    query.page = page
    getList()
  }

  function handleSizeChange(size) {
    query.pageSize = size
    query.page = 1
    getList()
  }

  if (immediate) {
    getList()
  }

  return {
    loading,
    list,
    total,
    query,
    getList,
    handleSearch,
    handleReset,
    handlePageChange,
    handleSizeChange
  }
}
