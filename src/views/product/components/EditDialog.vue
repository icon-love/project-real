<template>
  <DialogForm
    v-model="dialogVisible"
    :title="dialogType === 'add' ? '新增商品' : '修改商品'"
    width="620px"
    :loading="submitting"
    @confirm="submitForm"
  >
    <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
      <el-form-item label="商品名称" prop="name">
        <el-input v-model="form.name" placeholder="请输入商品名称" maxlength="30" />
      </el-form-item>
      <el-form-item label="商品分类" prop="category">
        <el-select v-model="form.category" placeholder="请选择分类" style="width: 100%">
          <el-option v-for="c in categories" :key="c" :label="c" :value="c" />
        </el-select>
      </el-form-item>
      <div class="flex gap-16px">
        <el-form-item label="价格" prop="price" class="flex-1">
          <el-input-number
            v-model="form.price"
            :min="0"
            :precision="2"
            :step="10"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="库存" prop="stock" class="flex-1">
          <el-input-number
            v-model="form.stock"
            :min="0"
            :step="10"
            style="width: 100%"
          />
        </el-form-item>
      </div>
      <el-form-item label="商品图片">
        <ImageSelect v-model="form.image" />
      </el-form-item>
      <el-form-item label="商品描述">
        <el-input
          v-model="form.description"
          type="textarea"
          :rows="3"
          maxlength="200"
          show-word-limit
          placeholder="请输入商品描述"
        />
      </el-form-item>
      <el-form-item label="状态">
        <el-radio-group v-model="form.status">
          <el-radio :value="1">上架</el-radio>
          <el-radio :value="0">下架</el-radio>
        </el-radio-group>
      </el-form-item>
    </el-form>
  </DialogForm>
</template>

<script setup>
/**
 * 商品编辑弹窗：图片通过 ImageSelect 从图库中选取
 */
import { ref } from 'vue'
import DialogForm from '@/components/DialogForm.vue'
import ImageSelect from '@/components/ImageSelect/index.vue'
import { useForm } from '@/composables/useForm'
import {
  addProduct,
  updateProduct,
  getProductCategoryList
} from '@/api/product'

const emit = defineEmits(['success'])

const categories = ref([])

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
  addApi: addProduct,
  updateApi: updateProduct,
  defaultForm: {
    id: null,
    name: '',
    category: '',
    price: 0,
    stock: 0,
    image: '',
    description: '',
    status: 1
  },
  rules: {
    name: [{ required: true, message: '请输入商品名称', trigger: 'blur' }],
    category: [{ required: true, message: '请选择商品分类', trigger: 'change' }],
    price: [{ required: true, message: '请输入价格', trigger: 'blur' }],
    stock: [{ required: true, message: '请输入库存', trigger: 'blur' }]
  },
  afterSubmit: () => emit('success')
})

async function loadCategories() {
  categories.value = (await getProductCategoryList()) || []
}

function handleOpenAdd() {
  loadCategories()
  openAdd()
}

function handleOpenEdit(row) {
  loadCategories()
  openEdit(row)
}

defineExpose({
  openAdd: handleOpenAdd,
  openEdit: handleOpenEdit
})
</script>
