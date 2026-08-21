<template>
  <DialogForm
    v-model="visible"
    :title="`配置权限 - ${roleName}`"
    width="480px"
    :loading="submitting"
    @confirm="handleConfirm"
  >
    <el-alert
      type="info"
      :closable="false"
      show-icon
      title="勾选该角色可访问的菜单权限，保存后对应管理员重新登录即可生效"
      class="mb-3"
    />
    <div v-loading="loading" class="tree-wrap">
      <el-tree
        ref="treeRef"
        :data="menuTree"
        :props="{ label: 'title', children: 'children' }"
        node-key="id"
        show-checkbox
        check-strictly
        default-expand-all
        :default-checked-keys="checkedKeys"
        :expand-on-click-node="false"
      >
        <template #default="{ data }">
          <span class="flex items-center gap-6px">
            <el-icon><component :is="resolveIcon(data.icon)" /></el-icon>
            <span>{{ data.title }}</span>
            <el-tag v-if="data.perms" size="small" type="info">{{ data.perms }}</el-tag>
          </span>
        </template>
      </el-tree>
    </div>
  </DialogForm>
</template>

<script setup>
/**
 * 配置角色权限弹窗（对应课程：配置权限表单-虚拟化树形控件/默认选中渲染/功能实现）
 */
import { ref, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import DialogForm from '@/components/DialogForm.vue'
import { resolveIcon } from '@/utils/icon'
import { getMenuList } from '@/api/menu'
import { getRolePermissionIds, setRolePermissions } from '@/api/role'

const visible = ref(false)
const loading = ref(false)
const submitting = ref(false)
const treeRef = ref(null)
const menuTree = ref([])
const checkedKeys = ref([])
const roleId = ref(null)
const roleName = ref('')

const emit = defineEmits(['success'])

async function open(row) {
  roleId.value = row.id
  roleName.value = row.name
  checkedKeys.value = []
  menuTree.value = await getMenuList()
  visible.value = true
  loading.value = true
  try {
    const ids = await getRolePermissionIds(row.id)
    checkedKeys.value = ids || []
    // 等待树渲染后设置勾选
    await nextTick()
    treeRef.value?.setCheckedKeys(checkedKeys.value)
  } finally {
    loading.value = false
  }
}

async function handleConfirm() {
  const checked = treeRef.value?.getCheckedKeys() || []
  const half = treeRef.value?.getHalfCheckedKeys() || []
  const ids = [...new Set([...checked, ...half])]
  submitting.value = true
  try {
    await setRolePermissions(roleId.value, ids)
    ElMessage.success('权限配置成功')
    visible.value = false
    emit('success')
  } catch (e) {
    // 错误已在拦截器提示
  } finally {
    submitting.value = false
  }
}

defineExpose({ open })
</script>

<style scoped lang="scss">
.tree-wrap {
  max-height: 420px;
  overflow: auto;
  border: 1px solid #ebeef5;
  border-radius: 6px;
  padding: 8px;
}
</style>
