<template>
  <div class="menu-page">
    <div class="app-card p-4">
      <div class="flex-between mb-4">
        <el-alert
          type="info"
          :closable="false"
          show-icon
          title="菜单变更后需重新登录，动态路由与侧边栏才会刷新（也可点击「重置数据库」恢复初始菜单）"
        />
        <div class="flex gap-10px">
          <el-button :icon="Refresh" @click="getList">刷新</el-button>
          <el-button
            type="warning"
            plain
            :icon="RefreshLeft"
            @click="handleResetDb"
          >
            重置数据库
          </el-button>
          <el-button type="primary" :icon="Plus" @click="openAdd(0)">
            新增菜单
          </el-button>
        </div>
      </div>

      <el-table
        v-loading="loading"
        :data="menuList"
        row-key="id"
        default-expand-all
        :tree-props="{ children: 'children' }"
        :header-cell-style="{ background: '#f5f7fa' }"
      >
        <el-table-column label="菜单名称" min-width="220">
          <template #default="{ row }">
            <el-icon class="mr-1" style="vertical-align: -2px">
              <component :is="row.icon || 'Menu'" />
            </el-icon>
            <span>{{ row.title }}</span>
            <el-tag v-if="row.component" size="small" class="ml-2">页面</el-tag>
            <el-tag v-else size="small" type="info" class="ml-2">目录</el-tag>
            <el-tag v-if="row.visible === false" size="small" type="warning" class="ml-2">隐藏</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="path" label="路由路径" min-width="140" />
        <el-table-column prop="perms" label="权限标识" min-width="130">
          <template #default="{ row }">
            <span v-if="row.perms" class="text-gray-500">{{ row.perms }}</span>
            <span v-else class="text-gray-300">-</span>
          </template>
        </el-table-column>
        <el-table-column prop="sort" label="排序" width="70" align="center" />
        <el-table-column label="状态" width="90" align="center">
          <template #default="{ row }">
            <el-switch
              v-model="row.status"
              :active-value="1"
              :inactive-value="0"
              @change="handleStatusChange(row)"
            />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" :icon="Plus" @click="openAdd(row.id)">
              新增
            </el-button>
            <el-button link type="primary" :icon="Edit" @click="openEdit(row)">
              编辑
            </el-button>
            <el-button link type="danger" :icon="Delete" @click="handleDelete(row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <EditDialog
      ref="editDialogRef"
      :menu-tree="menuList"
      @success="getList"
    />
  </div>
</template>

<script setup>
/**
 * 菜单权限模块（对应课程：菜单权限列表实现 / 树形控件自定义节点渲染 / 新增修改状态删除）
 */
import { ref, onMounted, onActivated } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Refresh, RefreshLeft, Plus, Edit, Delete } from '@element-plus/icons-vue'
import { getMenuList, updateMenuStatus, deleteMenu } from '@/api/menu'
import { resetDb } from '@/mock/db'
import { useCrud } from '@/composables/useCrud'
import EditDialog from './components/EditDialog.vue'

defineOptions({ name: 'Menu' })

const loading = ref(false)
const menuList = ref([])
const editDialogRef = ref(null)

async function getList() {
  loading.value = true
  try {
    menuList.value = await getMenuList()
  } finally {
    loading.value = false
  }
}

function openAdd(parentId) {
  editDialogRef.value.openAdd(parentId)
}

function openEdit(row) {
  editDialogRef.value.openEdit(row)
}

function handleResetDb() {
  ElMessageBox.confirm(
    '重置后将恢复初始菜单/管理员/角色等数据，确定继续吗？',
    '系统提示',
    { type: 'warning' }
  )
    .then(() => {
      resetDb()
      ElMessage.success('已重置数据库')
      getList()
    })
    .catch(() => {})
}

const { handleStatusChange, handleDelete } = useCrud({
  statusApi: updateMenuStatus,
  deleteApi: deleteMenu,
  afterStatus: getList,
  afterDelete: getList
})

onMounted(getList)
onActivated(() => {
  if (menuList.value.length) getList()
})
</script>
