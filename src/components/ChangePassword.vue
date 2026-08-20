<template>
  <DialogForm
    v-model="visible"
    title="修改密码"
    width="460px"
    :loading="submitting"
    @confirm="handleConfirm"
  >
    <el-form ref="formRef" :model="form" :rules="formRules" label-width="90px">
      <el-form-item label="原密码" prop="oldPassword">
        <el-input
          v-model="form.oldPassword"
          type="password"
          show-password
          placeholder="请输入原密码"
        />
      </el-form-item>
      <el-form-item label="新密码" prop="newPassword">
        <el-input
          v-model="form.newPassword"
          type="password"
          show-password
          placeholder="请输入新密码"
        />
      </el-form-item>
      <el-form-item label="确认密码" prop="confirmPassword">
        <el-input
          v-model="form.confirmPassword"
          type="password"
          show-password
          placeholder="请再次输入新密码"
        />
      </el-form-item>
    </el-form>
  </DialogForm>
</template>

<script setup>
/**
 * 修改密码弹窗（对应课程：公共头部开发-修改密码）
 */
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import DialogForm from '@/components/DialogForm.vue'
import { changePassword } from '@/api/user'
import { useRouter } from 'vue-router'

const visible = defineModel({ type: Boolean, default: false })

const router = useRouter()
const submitting = ref(false)
const formRef = ref(null)
const form = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const validateConfirm = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请再次输入新密码'))
  } else if (value !== form.newPassword) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const formRules = {
  oldPassword: [
    { required: true, message: '请输入原密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  confirmPassword: [{ validator: validateConfirm, trigger: 'blur' }]
}

async function handleConfirm() {
  try {
    await formRef.value.validate()
  } catch (e) {
    return
  }
  submitting.value = true
  try {
    await changePassword({ ...form })
    ElMessage.success('密码修改成功，请重新登录')
    visible.value = false
    router.push('/login')
  } catch (e) {
    // 错误已在拦截器提示
  } finally {
    submitting.value = false
  }
}
</script>
