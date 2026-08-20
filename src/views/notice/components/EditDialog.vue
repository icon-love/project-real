<template>
  <DialogForm
    v-model="dialogVisible"
    :title="dialogType === 'add' ? '新增公告' : '修改公告'"
    width="600px"
    :loading="submitting"
    @confirm="submitForm"
  >
    <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
      <el-form-item label="公告标题" prop="title">
        <el-input v-model="form.title" placeholder="请输入公告标题" maxlength="50" show-word-limit />
      </el-form-item>
      <el-form-item label="公告内容" prop="content">
        <el-input
          v-model="form.content"
          type="textarea"
          :rows="6"
          placeholder="请输入公告内容"
          maxlength="500"
          show-word-limit
        />
      </el-form-item>
      <el-form-item label="发布人">
        <el-input v-model="form.author" placeholder="发布人（默认当前用户）" />
      </el-form-item>
    </el-form>
  </DialogForm>
</template>

<script setup>
/**
 * 公告编辑弹窗（对应课程：公告新增功能实现 / 删除和修改功能实现）
 * 复用组合式封装 useForm
 */
import DialogForm from '@/components/DialogForm.vue'
import { useForm } from '@/composables/useForm'
import { addNotice, updateNotice } from '@/api/notice'
import { useStore } from 'vuex'

const emit = defineEmits(['success'])
const store = useStore()

const { dialogVisible, dialogType, submitting, formRef, form, rules, openAdd, openEdit, submitForm } =
  useForm({
    addApi: addNotice,
    updateApi: updateNotice,
    defaultForm: { id: null, title: '', content: '', author: '' },
    rules: {
      title: [{ required: true, message: '请输入公告标题', trigger: 'blur' }],
      content: [{ required: true, message: '请输入公告内容', trigger: 'blur' }]
    },
    afterSubmit: () => emit('success')
  })

function handleOpenAdd() {
  form.author = store.getters.userInfo.nickname || store.getters.userInfo.username || ''
  openAdd()
}

defineExpose({
  openAdd: handleOpenAdd,
  openEdit
})
</script>
