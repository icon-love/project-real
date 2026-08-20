<template>
  <div class="image-select">
    <!-- 触发按钮 -->
    <div class="trigger" @click="openDialog">
      <el-image
        v-if="modelValue"
        :src="modelValue"
        fit="cover"
        class="preview"
      />
      <div v-else class="placeholder">
        <el-icon :size="20"><Plus /></el-icon>
        <span>选择图片</span>
      </div>
    </div>

    <!-- 选择弹窗 -->
    <el-dialog
      v-model="visible"
      title="从图库选择图片"
      width="760px"
      append-to-body
      @open="handleOpen"
    >
      <div class="flex items-center gap-10px mb-3 flex-wrap">
        <el-radio-group v-model="query.categoryId" @change="handleSearch">
          <el-radio-button value="">全部</el-radio-button>
          <el-radio-button
            v-for="c in categories"
            :key="c.id"
            :value="c.id"
          >
            {{ c.name }}
          </el-radio-button>
        </el-radio-group>
        <el-input
          v-model="query.name"
          placeholder="搜索图片名称"
          clearable
          style="width: 200px"
          @keyup.enter="handleSearch"
          @clear="handleSearch"
        >
          <template #append>
            <el-button @click="handleSearch">
              <el-icon><Search /></el-icon>
            </el-button>
          </template>
        </el-input>
      </div>

      <!-- 图片网格 -->
      <el-skeleton :loading="loading" animated>
        <template #template>
          <div class="image-grid">
            <div v-for="i in 8" :key="i" class="image-item">
              <el-skeleton-item variant="image" style="width: 100%; height: 100%" />
            </div>
          </div>
        </template>
        <div v-if="!list.length" class="text-center text-gray-400 py-10">
          暂无图片
        </div>
        <div v-else class="image-grid">
          <div
            v-for="img in list"
            :key="img.id"
            class="image-item"
            :class="{ selected: selectedUrl === img.url }"
            @click="selectedUrl = img.url"
          >
            <el-image :src="img.url" fit="cover" class="thumb" lazy />
            <div class="mask"><el-icon><Check /></el-icon></div>
          </div>
        </div>
      </el-skeleton>

      <div class="flex justify-end mt-3">
        <Pagination
          :page="query.page"
          :page-size="query.pageSize"
          :total="total"
          @page-change="query.page = $event; getList()"
          @size-change="query.pageSize = $event; query.page = 1; getList()"
        />
      </div>

      <template #footer>
        <el-button @click="visible = false">取 消</el-button>
        <el-button type="primary" :disabled="!selectedUrl" @click="handleConfirm">
          确 定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
/**
 * 全局选中图库组件（对应课程：全局选中图库组件开发）
 * 用法：<ImageSelect v-model="form.avatar" />
 */
import { ref, reactive } from 'vue'
import {
  getGalleryList,
  getGalleryCategoryList
} from '@/api/gallery'
import Pagination from '@/components/Pagination.vue'

const props = defineProps({
  modelValue: { type: String, default: '' }
})
const emit = defineEmits(['update:modelValue'])

const visible = ref(false)
const loading = ref(false)
const list = ref([])
const total = ref(0)
const categories = ref([])
const selectedUrl = ref('')
const query = reactive({ page: 1, pageSize: 12, categoryId: '', name: '' })

async function getCategories() {
  const data = await getGalleryCategoryList({ page: 1, pageSize: 100 })
  categories.value = data.list || []
}

async function getList() {
  loading.value = true
  try {
    const data = await getGalleryList({ ...query })
    list.value = data.list || []
    total.value = data.total || 0
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  query.page = 1
  getList()
}

function openDialog() {
  selectedUrl.value = props.modelValue
  visible.value = true
}

function handleOpen() {
  getCategories()
  handleSearch()
}

function handleConfirm() {
  emit('update:modelValue', selectedUrl.value)
  visible.value = false
}
</script>

<style scoped lang="scss">
.trigger {
  width: 100px;
  height: 100px;
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  overflow: hidden;
  cursor: pointer;
  transition: border-color 0.2s;

  &:hover {
    border-color: #409eff;
  }

  .preview {
    width: 100%;
    height: 100%;
    display: block;
  }

  .placeholder {
    width: 100%;
    height: 100%;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    gap: 4px;
    color: #909399;
    font-size: 12px;
  }
}

.image-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 10px;

  .image-item {
    position: relative;
    aspect-ratio: 4 / 3;
    border-radius: 6px;
    overflow: hidden;
    cursor: pointer;
    border: 2px solid transparent;

    .thumb {
      width: 100%;
      height: 100%;
      display: block;
    }

    .mask {
      position: absolute;
      inset: 0;
      display: flex;
      align-items: center;
      justify-content: center;
      background: rgba(0, 0, 0, 0.45);
      color: #fff;
      font-size: 22px;
      opacity: 0;
      transition: opacity 0.2s;
    }

    &:hover .mask {
      opacity: 1;
    }

    &.selected {
      border-color: #409eff;
      .mask {
        opacity: 1;
      }
    }
  }
}
</style>
