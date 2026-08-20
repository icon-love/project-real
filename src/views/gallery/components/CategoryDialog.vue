<template>
  <DialogForm
    v-model="visible"
    :title="type === 'add' ? '新增图库分类' : '修改图库分类'"
    width="440px"
    :loading="submitting"
    @confirm="submit"
  >
    <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
      <el-form-item label="分类名称" prop="name">
        <el-input v-model="form.name" placeholder="请输入分类名称" maxlength="20" />
      </el-form-item>
      <el-form-item label="排序" prop="sort">
        <el-input-number v-model="form.sort" :min="0" :max="999" />
        <span class="ml-2 text-gray-400 text-12px">数字越小越靠前</span>
      </el-form-item>
      <el-form-item label="备注" prop="remark">
        <el-input
          v-model="form.remark"
          type="textarea"
          :rows="3"
          maxlength="100"
          show-word-limit
          placeholder="选填"
        />
      </el-form-item>
    </el-form>
  </DialogForm>
</template>

<script setup>
/**
 * 图库分类表单（对应课程：新增/修改图库分类功能交互）
 */
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import DialogForm from '@/components/DialogForm.vue'
import {
  addGalleryCategory,
  updateGalleryCategory
} from '@/api/gallery'

const emit = defineEmits(['success'])

const visible = ref(false)
const submitting = ref(false)
const formRef = ref(null)
const type = ref('add')
const form = reactive({ id: null, name: '', sort: 0, remark: '' })

const rules = {
  name: [{ required: true, message: '请输入分类名称', trigger: 'blur' }]
}

function reset() {
  form.id = null
  form.name = ''
  form.sort = 0
  form.remark = ''
  formRef.value?.clearValidate()
}

function openAdd() {
  type.value = 'add'
  reset()
  visible.value = true
}

function openEdit(row) {
  type.value = 'edit'
  reset()
  Object.assign(form, row)
  visible.value = true
}

async function submit() {
  try {
    await formRef.value.validate()
  } catch (e) {
    return
  }
  submitting.value = true
  try {
    if (type.value === 'add') {
      await addGalleryCategory({
        name: form.name,
        sort: form.sort,
        remark: form.remark
      })
      ElMessage.success('新增成功')
    } else {
      await updateGalleryCategory({
        id: form.id,
        name: form.name,
        sort: form.sort,
        remark: form.remark
      })
      ElMessage.success('修改成功')
    }
    visible.value = false
    emit('success')
  } catch (e) {
    // 错误已在拦截器提示
  } finally {
    submitting.value = false
  }
}

defineExpose({ openAdd, openEdit })
</script>
