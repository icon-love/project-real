<template>
  <DialogForm
    v-model="dialogVisible"
    :title="dialogType === 'add' ? '新增菜单' : '修改菜单'"
    width="560px"
    :loading="submitting"
    @confirm="submitForm"
  >
    <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
      <el-form-item label="父级菜单" prop="parentId">
        <el-tree-select
          v-model="form.parentId"
          :data="parentOptions"
          :props="{ label: 'title', children: 'children' }"
          node-key="id"
          check-strictly
          default-expand-all
          placeholder="请选择父级菜单（顶级菜单可不选）"
          style="width: 100%"
        />
      </el-form-item>
      <el-form-item label="菜单名称" prop="title">
        <el-input v-model="form.title" placeholder="如：图库管理" maxlength="20" />
      </el-form-item>
      <el-form-item label="路由名称" prop="name">
        <el-input v-model="form.name" placeholder="如：Gallery（用于 keep-alive 缓存）" maxlength="30" />
      </el-form-item>
      <el-form-item label="路由路径" prop="path">
        <el-input v-model="form.path" placeholder="如：gallery" maxlength="50" />
      </el-form-item>
      <el-form-item label="组件路径" prop="component">
        <el-input
          v-model="form.component"
          placeholder="如：gallery/index（目录节点可留空）"
          maxlength="50"
        />
      </el-form-item>
      <el-form-item label="菜单图标" prop="icon">
        <IconSelect v-model="form.icon" />
      </el-form-item>
      <el-form-item label="权限标识" prop="perms">
        <el-input v-model="form.perms" placeholder="如：gallery:view" maxlength="50" />
      </el-form-item>
      <div class="flex gap-16px">
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="form.sort" :min="0" :max="999" />
        </el-form-item>
        <el-form-item label="显示">
          <el-radio-group v-model="form.visible">
            <el-radio :value="true">显示</el-radio>
            <el-radio :value="false">隐藏</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">启用</el-radio>
            <el-radio :value="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </div>
    </el-form>
  </DialogForm>
</template>

<script setup>
/**
 * 菜单编辑弹窗（对应课程：新增和修改菜单权限功能 / 自定义图标下拉选择组件）
 */
import { computed, ref } from 'vue'
import DialogForm from '@/components/DialogForm.vue'
import IconSelect from '@/components/IconSelect/index.vue'
import { useForm } from '@/composables/useForm'
import { addMenu, updateMenu } from '@/api/menu'

const props = defineProps({
  // 当前菜单树（用于父级选择）
  menuTree: { type: Array, default: () => [] }
})
const emit = defineEmits(['success'])

const {
  dialogVisible,
  dialogType,
  submitting,
  formRef,
  form,
  rules,
  openAdd,
  openEdit,
  submitForm
} = useForm({
  addApi: addMenu,
  updateApi: updateMenu,
  defaultForm: {
    id: null,
    parentId: 0,
    title: '',
    name: '',
    path: '',
    component: '',
    icon: 'Menu',
    sort: 0,
    perms: '',
    visible: true,
    status: 1
  },
  rules: {
    title: [{ required: true, message: '请输入菜单名称', trigger: 'blur' }],
    name: [{ required: true, message: '请输入路由名称', trigger: 'blur' }],
    path: [{ required: true, message: '请输入路由路径', trigger: 'blur' }]
  },
  afterSubmit: () => emit('success')
})

// 收集节点及其所有后代 id（编辑时排除自身，避免形成环）
function collectIds(nodes, acc = []) {
  nodes.forEach((n) => {
    acc.push(n.id)
    if (n.children && n.children.length) collectIds(n.children, acc)
  })
  return acc
}

// 过滤掉被编辑节点及其后代
const parentOptions = computed(() => {
  let tree = props.menuTree
  if (dialogType.value === 'edit' && form.id) {
    const excluded = collectIds([{ ...form, children: findChildren(form.id) }])
    tree = filterTree(props.menuTree, excluded)
  }
  return [{ id: 0, title: '顶级菜单', children: tree }]
})

function findChildren(id) {
  const find = (nodes) => {
    for (const n of nodes) {
      if (n.id === id) return n.children || []
      if (n.children) {
        const r = find(n.children)
        if (r) return r
      }
    }
    return null
  }
  return find(props.menuTree) || []
}

function filterTree(nodes, excluded) {
  return nodes
    .filter((n) => !excluded.includes(n.id))
    .map((n) => ({
      ...n,
      children: n.children ? filterTree(n.children, excluded) : []
    }))
}

function handleOpenAdd(parentId) {
  openAdd()
  form.parentId = parentId || 0
}

function handleOpenEdit(row) {
  openEdit(row)
}

defineExpose({
  openAdd: handleOpenAdd,
  openEdit: handleOpenEdit
})
</script>
