<template>
  <section class="app-main">
    <router-view v-slot="{ Component }">
      <transition name="fade-transform" mode="out-in">
        <keep-alive :include="cachedViews">
          <component :is="Component" :key="currentKey" />
        </keep-alive>
      </transition>
    </router-view>
  </section>
</template>

<script setup>
/**
 * 主内容区（对应课程：keep-alive 页面缓存 / transition 全局过渡动画）
 * - keep-alive 按组件 name 缓存页面
 * - 通过 refreshKey 变化实现当前页强制刷新
 */
import { ref, computed, inject } from 'vue'
import { useStore } from 'vuex'
import { useRoute } from 'vue-router'

const store = useStore()
const route = useRoute()
const refreshKey = inject('refreshKey', ref(0))

const cachedViews = computed(() => store.getters.cachedViews)

// 路径 + 刷新次数 作为组件 key
const currentKey = computed(() => `${route.path}-${refreshKey.value}`)
</script>

<style scoped lang="scss">
.app-main {
  flex: 1;
  min-height: 0;
  overflow: auto;
  padding: 16px;
  background-color: #f0f2f5;
}
</style>
