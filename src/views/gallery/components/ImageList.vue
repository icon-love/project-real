<template>
  <div v-loading="loading" class="image-list" element-loading-text="加载中...">
    <el-empty v-if="!loading && !list.length" description="暂无图片" />
    <div v-else class="image-grid">
      <div v-for="img in list" :key="img.id" class="image-item">
        <el-image
          :src="img.url"
          fit="cover"
          class="thumb"
          :preview-src-list="list.map((i) => i.url)"
          :initial-index="list.findIndex((i) => i.id === img.id)"
          preview-teleported
        />
        <span class="size">{{ img.size }}KB</span>
        <div class="mask">
          <span class="name" :title="img.name">{{ img.name }}</span>
          <div class="actions">
            <el-tooltip content="重命名" placement="top">
              <el-icon class="action" @click="$emit('rename', img)"><EditPen /></el-icon>
            </el-tooltip>
            <el-tooltip content="删除" placement="top">
              <el-icon class="action danger" @click="$emit('delete', img)"><Delete /></el-icon>
            </el-tooltip>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
/**
 * 图片列表组件（对应课程：图片列表组件开发）
 * 支持预览 / 重命名 / 删除
 */
defineProps({
  loading: { type: Boolean, default: false },
  list: { type: Array, default: () => [] }
})

defineEmits(['rename', 'delete'])
</script>

<style scoped lang="scss">
.image-list {
  min-height: 300px;
}

.image-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(160px, 1fr));
  gap: 12px;

  .image-item {
    position: relative;
    aspect-ratio: 4 / 3;
    border-radius: 8px;
    overflow: hidden;
    background: #f5f7fa;
    cursor: pointer;

    .thumb {
      width: 100%;
      height: 100%;
      display: block;
    }

    .size {
      position: absolute;
      right: 6px;
      top: 6px;
      padding: 1px 6px;
      font-size: 11px;
      color: #fff;
      background: rgba(0, 0, 0, 0.5);
      border-radius: 4px;
      pointer-events: none;
    }

    .mask {
      position: absolute;
      inset: 0;
      display: flex;
      flex-direction: column;
      justify-content: space-between;
      padding: 10px;
      background: linear-gradient(to top, rgba(0, 0, 0, 0.65), rgba(0, 0, 0, 0.15) 60%, transparent);
      opacity: 0;
      transition: opacity 0.2s;

      .name {
        color: #fff;
        font-size: 12px;
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
      }

      .actions {
        display: flex;
        justify-content: flex-end;
        gap: 8px;

        .action {
          width: 26px;
          height: 26px;
          border-radius: 50%;
          background: rgba(255, 255, 255, 0.25);
          color: #fff;
          display: flex;
          align-items: center;
          justify-content: center;

          &:hover {
            background: #409eff;
          }
          &.danger:hover {
            background: #f56c6c;
          }
        }
      }
    }

    &:hover .mask {
      opacity: 1;
    }
  }
}
</style>
