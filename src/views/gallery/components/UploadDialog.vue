<template>
  <DialogForm
    v-model="visible"
    title="上传图片"
    width="620px"
    :loading="submitting"
    @confirm="submit"
  >
    <el-form label-width="80px">
      <el-form-item label="选择分类">
        <el-select v-model="categoryId" style="width: 100%">
          <el-option
            v-for="c in categories"
            :key="c.id"
            :label="c.name"
            :value="c.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="选择图片">
        <el-upload
          ref="uploadRef"
          :auto-upload="false"
          :limit="20"
          multiple
          list-type="picture-card"
          accept="image/*"
          :on-exceed="handleExceed"
        >
          <el-icon :size="20"><Plus /></el-icon>
          <template #tip>
            <div class="el-upload__tip">
              支持 jpg/png/webp 格式，单次最多 20 张（演示环境不会真正上传文件）
            </div>
          </template>
        </el-upload>
      </el-form-item>
    </el-form>
  </DialogForm>
</template>

<script setup>
/**
 * 上传多图弹窗（对应课程：上传多图功能实现）
 */
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import DialogForm from '@/components/DialogForm.vue'
import { uploadImages, getGalleryCategoryList } from '@/api/gallery'

const emit = defineEmits(['success'])

const visible = ref(false)
const submitting = ref(false)
const uploadRef = ref(null)
const categoryId = ref('')
const categories = ref([])

async function open(categoryId) {
  const data = await getGalleryCategoryList({ page: 1, pageSize: 100 })
  categories.value = data.list || []
  categoryId.value =
    categoryId || (categories.value[0] && categories.value[0].id) || ''
  visible.value = true
}

function handleExceed() {
  ElMessage.warning('单次最多上传 20 张图片')
}

async function submit() {
  const files = (uploadRef.value?.uploadFiles || []).filter((f) => f.raw)
  if (!files.length) {
    ElMessage.warning('请先选择图片')
    return
  }
  const formData = new FormData()
  formData.append('categoryId', categoryId.value)
  files.forEach((f) => formData.append('files', f.raw))

  submitting.value = true
  try {
    const res = await uploadImages(formData)
    ElMessage.success(res.message || '上传成功')
    visible.value = false
    uploadRef.value?.clearFiles()
    emit('success')
  } catch (e) {
    // 错误已在拦截器提示
  } finally {
    submitting.value = false
  }
}

defineExpose({ open })
</script>
