<template>
  <div class="app-wrapper" :class="{ 'is-collapse': !sidebar.opened }">
    <Sidebar />
    <div class="main-container">
      <Navbar />
      <TagsView />
      <AppMain />
    </div>
  </div>
</template>

<script setup>
/**
 * 后台主布局（对应课程：后台主布局实现）
 * 结构：侧边栏 + (头部导航 + 标签导航 + 主内容区)
 */
import { ref, provide, computed } from 'vue'
import { useStore } from 'vuex'
import Sidebar from './components/Sidebar/index.vue'
import Navbar from './components/Navbar/index.vue'
import TagsView from './components/TagsView/index.vue'
import AppMain from './components/AppMain.vue'

const store = useStore()
const sidebar = computed(() => store.state.app.sidebar)

// 页面刷新机制：通过递增 refreshKey 强制当前路由组件重新挂载
const refreshKey = ref(0)
function refreshPage() {
  refreshKey.value++
}
provide('refreshPage', refreshPage)
provide('refreshKey', refreshKey)
</script>

<style scoped lang="scss">
.app-wrapper {
  height: 100%;
  width: 100%;
  display: flex;
  overflow: hidden;

  .main-container {
    flex: 1;
    min-width: 0;
    height: 100%;
    display: flex;
    flex-direction: column;
    background-color: #f0f2f5;
  }
}
</style>
