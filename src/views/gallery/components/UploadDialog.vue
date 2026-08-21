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
          :on-change="handleFileChange"
          :on-remove="handleFileRemove"
          :on-exceed="handleExceed"
        >
          <el-icon :size="20"><Plus /></el-icon>
          <template #tip>
            <div class="el-upload__tip">
              支持 jpg/png/webp/svg 格式，单次最多 20 张，图片内容将存入数据库
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
// 本地维护已选文件（el-upload 未暴露 uploadFiles，不能依赖 ref 读取）
const selectedFiles = ref([])

// 注意：参数不能命名为 categoryId，否则会与 ref 同名冲突导致赋值崩溃
async function open(presetCategoryId) {
  const data = await getGalleryCategoryList({ page: 1, pageSize: 100 })
  categories.value = data.list || []
  categoryId.value =
    presetCategoryId || (categories.value[0] && categories.value[0].id) || ''
  visible.value = true
}

function handleExceed() {
  ElMessage.warning('单次最多上传 20 张图片')
}

// 选择文件时加入本地列表
function handleFileChange(file) {
  if (file.raw && !selectedFiles.value.includes(file.raw)) {
    selectedFiles.value.push(file.raw)
  }
}

// 移除文件时从本地列表删除
function handleFileRemove(file) {
  selectedFiles.value = selectedFiles.value.filter((f) => f !== file.raw)
}

async function submit() {
  const files = selectedFiles.value
  if (!files.length) {
    ElMessage.warning('请先选择图片')
    return
  }
  const formData = new FormData()
  formData.append('categoryId', categoryId.value)
  files.forEach((f) => formData.append('files', f))

  submitting.value = true
  try {
    const res = await uploadImages(formData)
    ElMessage.success(res.message || '上传成功')
    visible.value = false
    uploadRef.value?.clearFiles()
    selectedFiles.value = []
    emit('success')
  } catch (e) {
    // 错误已在拦截器提示
  } finally {
    submitting.value = false
  }
}

defineExpose({ open })
</script>
