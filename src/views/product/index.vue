<template>
  <div class="product-page">
    <div class="app-card p-4">
      <!-- 搜索栏 -->
      <div class="flex-between mb-4 flex-wrap gap-10px">
        <div class="flex gap-10px flex-wrap">
          <el-input
            v-model="query.name"
            placeholder="搜索商品名称"
            clearable
            style="width: 200px"
            @keyup.enter="handleSearch"
            @clear="handleSearch"
          />
          <el-select
            v-model="query.category"
            placeholder="商品分类"
            clearable
            style="width: 140px"
            @change="handleSearch"
          >
            <el-option v-for="c in categories" :key="c" :label="c" :value="c" />
          </el-select>
          <el-select
            v-model="query.status"
            placeholder="状态"
            clearable
            style="width: 120px"
            @change="handleSearch"
          >
            <el-option label="上架" :value="1" />
            <el-option label="下架" :value="0" />
          </el-select>
          <el-button type="primary" :icon="Search" @click="handleSearch">
            搜索
          </el-button>
          <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        </div>
        <el-button type="primary" :icon="Plus" @click="openAdd">
          新增商品
        </el-button>
      </div>

      <!-- 表格 -->
      <el-table v-loading="loading" :data="list" stripe>
        <el-table-column label="图片" width="80">
          <template #default="{ row }">
            <el-image
              :src="row.image"
              fit="cover"
              class="product-thumb"
              :preview-src-list="row.image ? [row.image] : []"
              preview-teleported
            />
          </template>
        </el-table-column>
        <el-table-column prop="name" label="商品名称" min-width="160" show-overflow-tooltip />
        <el-table-column prop="category" label="分类" width="110" />
        <el-table-column label="价格" width="130">
          <template #default="{ row }">
            <span class="price">¥{{ Number(row.price).toLocaleString() }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="stock" label="库存" width="90" align="center" />
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-switch
              v-model="row.status"
              :active-value="1"
              :inactive-value="0"
              @change="handleStatusChange(row)"
            />
          </template>
        </el-table-column>
        <el-table-column label="创建时间" width="170">
          <template #default="{ row }">
            {{ formatTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" :icon="Edit" @click="openEdit(row)">
              编辑
            </el-button>
            <el-button link type="danger" :icon="Delete" @click="handleDelete(row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="mt-4">
        <Pagination
          :page="query.page"
          :page-size="query.pageSize"
          :total="total"
          @page-change="handlePageChange"
          @size-change="handleSizeChange"
        />
      </div>
    </div>

    <EditDialog ref="editDialogRef" @success="getList" />
  </div>
</template>

<script setup>
/**
 * 商品管理模块：商品信息的增删改查，商品图片从图库中选取
 */
import { ref, onMounted, onActivated } from 'vue'
import { Search, Refresh, Plus, Edit, Delete } from '@element-plus/icons-vue'
import {
  getProductList,
  getProductCategoryList,
  updateProductStatus,
  deleteProduct
} from '@/api/product'
import { useTable } from '@/composables/useTable'
import { useCrud } from '@/composables/useCrud'
import { formatTime } from '@/utils'
import Pagination from '@/components/Pagination.vue'
import EditDialog from './components/EditDialog.vue'

defineOptions({ name: 'Product' })

const categories = ref([])

const {
  loading,
  list,
  total,
  query,
  getList,
  handleSearch,
  handleReset,
  handlePageChange,
  handleSizeChange
} = useTable(getProductList, { defaultQuery: { name: '', category: '', status: '' } })

const editDialogRef = ref(null)

async function loadCategories() {
  categories.value = (await getProductCategoryList()) || []
}

function openAdd() {
  editDialogRef.value.openAdd()
}

function openEdit(row) {
  editDialogRef.value.openEdit(row)
}

const { handleStatusChange, handleDelete } = useCrud({
  statusApi: updateProductStatus,
  deleteApi: deleteProduct,
  afterStatus: getList,
  afterDelete: getList
})

onMounted(loadCategories)
onActivated(() => {
  if (list.value.length) getList()
})
</script>

<style scoped lang="scss">
.product-thumb {
  width: 56px;
  height: 42px;
  border-radius: 4px;
  display: block;
}

.price {
  color: #f56c6c;
  font-weight: 600;
}
</style>
