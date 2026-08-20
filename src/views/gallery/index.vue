<template>
  <div class="gallery-page">
    <!-- 左侧分类面板 -->
    <div class="category-panel app-card">
      <div class="panel-header flex-between">
        <span class="panel-title">图库分类</span>
        <el-button text type="primary" :icon="Plus" @click="openCategoryAdd" />
      </div>
      <el-scrollbar class="category-list">
        <div
          class="category-item"
          :class="{ active: query.categoryId === '' }"
          @click="selectCategory('')"
        >
          <el-icon class="cat-icon"><Folder /></el-icon>
          <span class="flex-1">全部图片</span>
          <span class="count">{{ total }}</span>
        </div>
        <div
          v-for="c in categories"
          :key="c.id"
          class="category-item"
          :class="{ active: query.categoryId === c.id }"
          @click="selectCategory(c.id)"
        >
          <el-icon class="cat-icon"><Picture /></el-icon>
          <span class="flex-1 truncate">{{ c.name }}</span>
          <el-dropdown trigger="click" @command="(cmd) => handleCategoryCommand(cmd, c)">
            <el-icon class="more" @click.stop><MoreFilled /></el-icon>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="edit">编辑</el-dropdown-item>
                <el-dropdown-item command="delete" divided>删除</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-scrollbar>
    </div>

    <!-- 右侧图片列表 -->
    <div class="image-panel app-card p-4">
      <div class="flex-between mb-3 flex-wrap gap-10px">
        <div class="flex gap-10px">
          <el-input
            v-model="query.name"
            placeholder="搜索图片名称"
            clearable
            style="width: 220px"
            @keyup.enter="handleSearch"
            @clear="handleSearch"
          >
            <template #append>
              <el-button :icon="Search" @click="handleSearch" />
            </template>
          </el-input>
          <el-button :icon="Refresh" @click="getList">刷新</el-button>
        </div>
        <el-button type="primary" :icon="Upload" @click="openUpload">
          上传图片
        </el-button>
      </div>

      <ImageList
        :loading="loading"
        :list="list"
        @rename="handleRename"
        @delete="handleDeleteImage"
      />

      <div class="mt-3">
        <Pagination
          :page="query.page"
          :page-size="query.pageSize"
          :total="total"
          @page-change="query.page = $event; getList()"
          @size-change="query.pageSize = $event; query.page = 1; getList()"
        />
      </div>
    </div>

    <CategoryDialog ref="categoryDialogRef" @success="handleCategorySuccess" />
    <UploadDialog ref="uploadDialogRef" @success="handleUploadSuccess" />
  </div>
</template>

<script setup>
/**
 * 图库模块（对应课程：图库模块开发-布局/分类/列表分页/上传）
 */
import { ref, reactive, onMounted, onActivated } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Plus,
  Folder,
  Picture,
  MoreFilled,
  Search,
  Refresh,
  Upload
} from '@element-plus/icons-vue'
import {
  getGalleryCategoryList,
  deleteGalleryCategory,
  getGalleryList,
  deleteGalleryImage,
  renameGalleryImage
} from '@/api/gallery'
import ImageList from './components/ImageList.vue'
import CategoryDialog from './components/CategoryDialog.vue'
import UploadDialog from './components/UploadDialog.vue'
import Pagination from '@/components/Pagination.vue'

defineOptions({ name: 'Gallery' })

const loading = ref(false)
const list = ref([])
const total = ref(0)
const categories = ref([])
const query = reactive({ page: 1, pageSize: 12, categoryId: '', name: '' })

const categoryDialogRef = ref(null)
const uploadDialogRef = ref(null)

// ---------- 分类 ----------
async function getCategories() {
  const data = await getGalleryCategoryList({ page: 1, pageSize: 100 })
  categories.value = data.list || []
}

function openCategoryAdd() {
  categoryDialogRef.value.openAdd()
}

function handleCategoryCommand(command, c) {
  if (command === 'edit') {
    categoryDialogRef.value.openEdit(c)
  } else if (command === 'delete') {
    ElMessageBox.confirm(`确定删除分类「${c.name}」吗？`, '系统提示', {
      type: 'warning'
    })
      .then(async () => {
        await deleteGalleryCategory(c.id)
        ElMessage.success('删除成功')
        if (query.categoryId === c.id) query.categoryId = ''
        getCategories()
        getList()
      })
      .catch(() => {})
  }
}

function handleCategorySuccess() {
  getCategories()
  getList()
}

function selectCategory(id) {
  query.categoryId = id
  handleSearch()
}

// ---------- 图片 ----------
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

function handleRename(img) {
  ElMessageBox.prompt('请输入新的图片名称', '重命名', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    inputValue: img.name,
    inputValidator: (v) => (v && v.trim() ? true : '请输入图片名称')
  })
    .then(async ({ value }) => {
      await renameGalleryImage(img.id, value.trim())
      ElMessage.success('重命名成功')
      getList()
    })
    .catch(() => {})
}

function handleDeleteImage(img) {
  ElMessageBox.confirm(`确定删除图片「${img.name}」吗？`, '系统提示', {
    type: 'warning'
  })
    .then(async () => {
      await deleteGalleryImage(img.id)
      ElMessage.success('删除成功')
      getList()
    })
    .catch(() => {})
}

function openUpload() {
  uploadDialogRef.value.open(query.categoryId || '')
}

function handleUploadSuccess() {
  getList()
  getCategories()
}

onMounted(() => {
  getCategories()
  getList()
})

// keep-alive 恢复时刷新
onActivated(() => {
  if (list.value.length) getList()
})
</script>

<style scoped lang="scss">
.gallery-page {
  display: grid;
  grid-template-columns: 220px 1fr;
  gap: 16px;
  align-items: start;
}

// 左侧分类
.category-panel {
  padding: 14px 10px;

  .panel-header {
    padding: 0 6px 10px;

    .panel-title {
      font-size: 15px;
      font-weight: 600;
      color: #303133;
    }
  }

  .category-list {
    max-height: calc(100vh - 220px);
  }

  .category-item {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 9px 10px;
    margin-bottom: 2px;
    border-radius: 6px;
    cursor: pointer;
    color: #606266;
    transition: all 0.2s;

    .cat-icon {
      color: #909399;
    }

    .count {
      font-size: 12px;
      color: #a8abb2;
    }

    .more {
      color: #a8abb2;
      visibility: hidden;
    }

    &:hover {
      background: #f5f7fa;
      .more {
        visibility: visible;
      }
    }

    &.active {
      background: #ecf5ff;
      color: #409eff;
      font-weight: 500;

      .cat-icon {
        color: #409eff;
      }
    }
  }
}

.image-panel {
  min-width: 0;
}

@media (max-width: 768px) {
  .gallery-page {
    grid-template-columns: 1fr;
  }
  .category-list {
    max-height: 200px !important;
  }
}
</style>
