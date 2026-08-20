<template>
  <div class="notice-page">
    <div class="app-card p-4">
      <!-- 搜索栏 -->
      <div class="flex-between mb-4 flex-wrap gap-10px">
        <div class="flex gap-10px">
          <el-input
            v-model="query.keyword"
            placeholder="搜索标题 / 发布人"
            clearable
            style="width: 240px"
            @keyup.enter="handleSearch"
            @clear="handleSearch"
          />
          <el-button type="primary" :icon="Search" @click="handleSearch">
            搜索
          </el-button>
          <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        </div>
        <el-button type="primary" :icon="Plus" @click="openAdd">
          新增公告
        </el-button>
      </div>

      <!-- 表格 -->
      <el-table v-loading="loading" :data="list" stripe>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="title" label="公告标题" min-width="200" show-overflow-tooltip />
        <el-table-column prop="content" label="公告内容" min-width="260" show-overflow-tooltip />
        <el-table-column prop="author" label="发布人" width="120" />
        <el-table-column label="发布时间" width="170">
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
 * 公告模块（对应课程：公告模块布局实现 / 列表分页交互 / 新增删除修改）
 */
import { ref } from 'vue'
import { Search, Refresh, Plus, Edit, Delete } from '@element-plus/icons-vue'
import { getNoticeList, deleteNotice } from '@/api/notice'
import { useTable } from '@/composables/useTable'
import { useCrud } from '@/composables/useCrud'
import { formatTime } from '@/utils'
import Pagination from '@/components/Pagination.vue'
import EditDialog from './components/EditDialog.vue'

defineOptions({ name: 'Notice' })

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
} = useTable(getNoticeList, { defaultQuery: { keyword: '' } })

const editDialogRef = ref(null)

function openAdd() {
  editDialogRef.value.openAdd()
}

function openEdit(row) {
  editDialogRef.value.openEdit(row)
}

const { handleDelete } = useCrud({
  deleteApi: deleteNotice,
  afterDelete: getList
})
</script>
