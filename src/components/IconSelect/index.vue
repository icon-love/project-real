<template>
  <el-popover
    v-model:visible="visible"
    placement="bottom-start"
    :width="340"
    trigger="click"
    popper-class="icon-select-popover"
  >
    <template #reference>
      <div class="icon-select-trigger">
        <el-input
          :model-value="modelValue"
          placeholder="请选择图标"
          readonly
          clearable
          @clear="$emit('update:modelValue', '')"
        >
          <template #prefix>
            <el-icon v-if="modelValue" class="mr-1">
              <component :is="resolveIcon(modelValue)" />
            </el-icon>
          </template>
        </el-input>
        <el-icon class="trigger-arrow"><ArrowDown /></el-icon>
      </div>
    </template>

    <div>
      <el-input
        v-model="keyword"
        placeholder="搜索图标名称"
        clearable
        size="small"
        class="mb-2"
      />
      <div class="icon-grid">
        <div
          v-for="icon in filteredIcons"
          :key="icon"
          class="icon-item"
          :class="{ active: icon === modelValue }"
          :title="icon"
          @click="handleSelect(icon)"
        >
          <el-icon :size="18"><component :is="resolveIcon(icon)" /></el-icon>
        </div>
      </div>
      <div v-if="!filteredIcons.length" class="text-gray-400 text-center py-3">
        未找到匹配的图标
      </div>
    </div>
  </el-popover>
</template>

<script setup>
/**
 * 自定义图标下拉选择组件（对应课程：自定义图标下拉选择组件）
 * 用法：<IconSelect v-model="form.icon" />
 */
import { ref, computed } from 'vue'
import * as Icons from '@element-plus/icons-vue'
import { resolveIcon } from '@/utils/icon'

defineProps({
  modelValue: { type: String, default: '' }
})
const emit = defineEmits(['update:modelValue'])

const visible = ref(false)
const keyword = ref('')

const icons = Object.keys(Icons)

const filteredIcons = computed(() => {
  const kw = keyword.value.trim().toLowerCase()
  if (!kw) return icons
  return icons.filter((i) => i.toLowerCase().includes(kw))
})

function handleSelect(icon) {
  emit('update:modelValue', icon)
  visible.value = false
}
</script>

<style scoped lang="scss">
.icon-select-trigger {
  position: relative;
  cursor: pointer;
  :deep(.el-input) {
    padding-right: 24px;
  }
  .trigger-arrow {
    position: absolute;
    right: 8px;
    top: 50%;
    transform: translateY(-50%);
    color: #909399;
    pointer-events: none;
  }
}

.icon-grid {
  display: grid;
  grid-template-columns: repeat(8, 1fr);
  gap: 6px;
  max-height: 240px;
  overflow-y: auto;

  .icon-item {
    display: flex;
    align-items: center;
    justify-content: center;
    height: 34px;
    border: 1px solid #e4e7ed;
    border-radius: 4px;
    cursor: pointer;
    transition: all 0.2s;

    &:hover {
      border-color: #409eff;
      color: #409eff;
    }
    &.active {
      border-color: #409eff;
      background: #ecf5ff;
      color: #409eff;
    }
  }
}
</style>
