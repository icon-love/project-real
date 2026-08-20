<template>
  <div class="role-page">
    <div class="app-card p-4">
      <div class="flex-between mb-4">
        <div class="flex gap-10px">
          <el-button :icon="Refresh" @click="getList">刷新</el-button>
        </div>
        <el-button type="primary" :icon="Plus" @click="openAdd">
          新增角色
        </el-button>
      </div>

      <el-table v-loading="loading" :data="roleList" stripe>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="name" label="角色名称" min-width="140">
          <template #default="{ row }">
            <el-tag :type="row.id === 1 ? 'danger' : 'primary'">
              {{ row.name }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" min-width="220" show-overflow-tooltip />
        <el-table-column label="权限数" width="90" align="center">
          <template #default="{ row }">
            {{ (row.permissionIds || []).length }}
          </template>
        </el-table-column>
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
        <el-table-column label="操作" width="230" fixed="right">
          <template #default="{ row }">
            <el-button
              link
              type="primary"
              :icon="Lock"
              v-permission="['system:role']"
              @click="openPermission(row)"
            >
              配置权限
            </el-button>
            <el-button link type="primary" :icon="Edit" @click="openEdit(row)">
              编辑
            </el-button>
            <el-button
              link
              type="danger"
              :icon="Delete"
              :disabled="row.id === 1"
              v-permission="['system:role']"
              @click="handleDelete(row)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <EditDialog ref="editDialogRef" @success="getList" />
    <PermissionDialog ref="permissionDialogRef" @success="getList" />
  </div>
</template>

<script setup>
/**
 * 角色模块（对应课程：复用代码快速实现角色模块开发 / 配置权限）
 */
import { ref, onMounted, onActivated } from 'vue'
import { Refresh, Plus, Lock, Edit, Delete } from '@element-plus/icons-vue'
import { getRoleList, updateRole, deleteRole } from '@/api/role'
import { useCrud } from '@/composables/useCrud'
import { formatTime } from '@/utils'
import EditDialog from './components/EditDialog.vue'
import PermissionDialog from './components/PermissionDialog.vue'

defineOptions({ name: 'Role' })

const loading = ref(false)
const roleList = ref([])
const editDialogRef = ref(null)
const permissionDialogRef = ref(null)

async function getList() {
  loading.value = true
  try {
    roleList.value = await getRoleList()
  } finally {
    loading.value = false
  }
}

function openAdd() {
  editDialogRef.value.openAdd()
}

function openEdit(row) {
  editDialogRef.value.openEdit(row)
}

function openPermission(row) {
  permissionDialogRef.value.open(row)
}

const { handleStatusChange, handleDelete } = useCrud({
  statusApi: (id, status) => updateRole({ id, status }),
  deleteApi: deleteRole,
  afterStatus: getList,
  afterDelete: getList
})

onMounted(getList)
onActivated(() => {
  if (roleList.value.length) getList()
})
</script>
