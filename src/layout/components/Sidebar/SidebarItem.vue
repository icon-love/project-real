<template>
  <template v-if="!item.meta?.hidden">
    <!-- 有子菜单 -> 分组 -->
    <el-sub-menu v-if="hasChildren" :index="resolvePath('')">
      <template #title>
        <el-icon v-if="icon"><component :is="resolveIcon(icon)" /></el-icon>
        <span>{{ item.meta?.title }}</span>
      </template>
      <SidebarItem
        v-for="child in item.children"
        :key="child.path"
        :item="child"
        :base-path="resolvePath(child.path)"
      />
    </el-sub-menu>

    <!-- 叶子菜单 -->
    <el-menu-item v-else :index="basePath">
      <el-icon v-if="icon"><component :is="resolveIcon(icon)" /></el-icon>
      <template #title>{{ item.meta?.title }}</template>
    </el-menu-item>
  </template>
</template>

<script setup>
/**
 * 递归侧边栏菜单项（对应课程：侧边菜单开发）
 */
import { computed } from 'vue'
import { resolveIcon } from '@/utils/icon'

const props = defineProps({
  item: { type: Object, required: true },
  basePath: { type: String, default: '' }
})

const hasChildren = computed(
  () => props.item.children && props.item.children.length > 0
)

const icon = computed(() => props.item.meta?.icon)

function pathJoin(base, p) {
  if (!p) return base
  const b = base.endsWith('/') ? base.slice(0, -1) : base
  return `${b}/${p}`
}

function resolvePath(childPath) {
  return pathJoin(props.basePath, childPath)
}
</script>
