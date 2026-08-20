<template>
  <div class="admin-page">
    <div class="app-card p-4">
      <!-- 搜索栏 -->
      <div class="flex-between mb-4 flex-wrap gap-10px">
        <div class="flex gap-10px flex-wrap">
          <el-input
            v-model="query.keyword"
            placeholder="搜索用户名 / 昵称"
            clearable
            style="width: 200px"
            @keyup.enter="handleSearch"
            @clear="handleSearch"
          />
          <el-select
            v-model="query.status"
            placeholder="状态"
            clearable
            style="width: 130px"
            @change="handleSearch"
          >
            <el-option label="启用" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
          <el-button type="primary" :icon="Search" @click="handleSearch">
            搜索
          </el-button>
          <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        </div>
        <el-button type="primary" :icon="Plus" @click="openAdd">
          新增管理员
        </el-button>
      </div>

      <!-- 表格 -->
      <el-table v-loading="loading" :data="list" stripe>
        <el-table-column label="头像" width="70">
          <template #default="{ row }">
            <el-avatar :size="36" :src="row.avatar">
              {{ (row.nickname || 'U').slice(0, 1) }}
            </el-avatar>
          </template>
        </el-table-column>
        <el-table-column prop="username" label="用户名" width="130" />
        <el-table-column prop="nickname" label="昵称" width="130" />
        <el-table-column prop="roleName" label="角色" width="130">
          <template #default="{ row }">
            <el-tag size="small" :type="row.roleId === 1 ? 'danger' : 'primary'">
              {{ row.roleName }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="phone" label="手机号" width="140" />
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-switch
              v-model="row.status"
              :active-value="1"
              :inactive-value="0"
              :disabled="row.id === 1"
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
            <el-button
              link
              type="danger"
              :icon="Delete"
              :disabled="row.id === 1"
              v-permission="['system:admin']"
              @click="handleDelete(row)"
            >
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
 * 管理员模块（对应课程：管理员列表分页交互 / 搜索 / 状态 / 新增修改删除）
 * 完整演示 useTable + useForm + useCrud 组合式封装
 */
import { ref } from 'vue'
import { Search, Refresh, Plus, Edit, Delete } from '@element-plus/icons-vue'
import {
  getAdminList,
  updateAdminStatus,
  deleteAdmin
} from '@/api/admin'
import { useTable } from '@/composables/useTable'
import { useCrud } from '@/composables/useCrud'
import { formatTime } from '@/utils'
import Pagination from '@/components/Pagination.vue'
import EditDialog from './components/EditDialog.vue'

defineOptions({ name: 'Admin' })

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
} = useTable(getAdminList, { defaultQuery: { keyword: '', status: '' } })

const editDialogRef = ref(null)

function openAdd() {
  editDialogRef.value.openAdd()
}

function openEdit(row) {
  editDialogRef.value.openEdit(row)
}

const { handleStatusChange, handleDelete } = useCrud({
  statusApi: updateAdminStatus,
  deleteApi: deleteAdmin,
  afterStatus: getList,
  afterDelete: getList
})
</script>
