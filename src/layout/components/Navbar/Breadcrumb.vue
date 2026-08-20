<template>
  <el-breadcrumb separator="/" class="breadcrumb">
    <transition-group name="breadcrumb">
      <el-breadcrumb-item v-for="(item, index) in levelList" :key="item.path">
        <span
          v-if="index === levelList.length - 1"
          class="no-redirect"
        >{{ item.meta.title }}</span>
        <a v-else class="redirect" @click.prevent="handleLink(item)">
          {{ item.meta.title }}
        </a>
      </el-breadcrumb-item>
    </transition-group>
  </el-breadcrumb>
</template>

<script setup>
import { ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()

const levelList = ref([])

function getBreadcrumb() {
  let matched = route.matched.filter(
    (item) => item.meta && item.meta.title && !item.meta.hidden
  )
  // 过滤掉根布局（无标题）
  matched = matched.filter((item) => item.path !== '/')
  levelList.value = matched
}

function handleLink(item) {
  const { redirect, path } = item
  if (redirect) {
    router.push(redirect)
  } else {
    router.push(path)
  }
}

watch(
  () => route.path,
  () => getBreadcrumb(),
  { immediate: true }
)
</script>

<style scoped lang="scss">
.breadcrumb {
  font-size: 14px;
  line-height: 1;
  .no-redirect {
    color: #97a8be;
  }
  .redirect {
    color: #666;
    font-weight: normal;
  }
}
</style>
