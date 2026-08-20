<template>
  <DialogForm
    v-model="dialogVisible"
    :title="dialogType === 'add' ? '新增管理员' : '修改管理员'"
    width="560px"
    :loading="submitting"
    @confirm="submitForm"
  >
    <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
      <el-form-item label="头像">
        <ImageSelect v-model="form.avatar" />
      </el-form-item>
      <el-form-item label="用户名" prop="username">
        <el-input v-model="form.username" placeholder="登录用户名" maxlength="20" />
      </el-form-item>
      <el-form-item label="昵称" prop="nickname">
        <el-input v-model="form.nickname" placeholder="显示昵称" maxlength="20" />
      </el-form-item>
      <el-form-item :label="dialogType === 'add' ? '密码' : '新密码'" prop="password">
        <el-input
          v-model="form.password"
          type="password"
          show-password
          :placeholder="dialogType === 'add' ? '请输入密码' : '留空则不修改'"
        />
      </el-form-item>
      <el-form-item label="角色" prop="roleId">
        <el-select v-model="form.roleId" placeholder="请选择角色" style="width: 100%">
          <el-option
            v-for="r in roles"
            :key="r.id"
            :label="r.name"
            :value="r.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="手机号" prop="phone">
        <el-input v-model="form.phone" placeholder="请输入手机号" maxlength="11" />
      </el-form-item>
      <el-form-item label="邮箱" prop="email">
        <el-input v-model="form.email" placeholder="请输入邮箱" maxlength="50" />
      </el-form-item>
      <el-form-item label="状态">
        <el-radio-group v-model="form.status">
          <el-radio :value="1">启用</el-radio>
          <el-radio :value="0">禁用</el-radio>
        </el-radio-group>
      </el-form-item>
    </el-form>
  </DialogForm>
</template>

<script setup>
/**
 * 管理员编辑弹窗（对应课程：新增修改和删除管理员功能）
 * 头像使用全局选中图库组件 ImageSelect
 */
import { ref } from 'vue'
import DialogForm from '@/components/DialogForm.vue'
import ImageSelect from '@/components/ImageSelect/index.vue'
import { useForm } from '@/composables/useForm'
import { addAdmin, updateAdmin } from '@/api/admin'
import { getRoleList } from '@/api/role'

const emit = defineEmits(['success'])

const roles = ref([])

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
  addApi: addAdmin,
  updateApi: updateAdmin,
  defaultForm: {
    id: null,
    username: '',
    nickname: '',
    password: '',
    roleId: '',
    phone: '',
    email: '',
    avatar: '',
    status: 1
  },
  rules: {
    username: [
      { required: true, message: '请输入用户名', trigger: 'blur' },
      { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
    ],
    nickname: [{ required: true, message: '请输入昵称', trigger: 'blur' }],
    password: [
      {
        validator: (rule, value, callback) => {
          if (dialogType.value === 'add' && !value) {
            callback(new Error('请输入密码'))
          } else if (value && value.length < 6) {
            callback(new Error('密码长度不能少于 6 位'))
          } else {
            callback()
          }
        },
        trigger: 'blur'
      }
    ],
    roleId: [{ required: true, message: '请选择角色', trigger: 'change' }],
    phone: [
      {
        pattern: /^1[3-9]\d{9}$/,
        message: '手机号格式不正确',
        trigger: 'blur'
      }
    ],
    email: [{ type: 'email', message: '邮箱格式不正确', trigger: 'blur' }]
  },
  afterSubmit: () => emit('success')
})

async function loadRoles() {
  const data = await getRoleList()
  roles.value = data || []
}

function handleOpenAdd() {
  loadRoles()
  openAdd()
}

function handleOpenEdit(row) {
  loadRoles()
  // 编辑时不回显密码
  openEdit({ ...row, password: '' })
}

defineExpose({
  openAdd: handleOpenAdd,
  openEdit: handleOpenEdit
})
</script>
