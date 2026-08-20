<template>
  <aside class="sidebar" :class="{ collapsed: !opened }">
    <Logo :collapsed="!opened" />
    <el-scrollbar class="sidebar-scroll">
      <el-menu
        :default-active="activeMenu"
        :collapse="!opened"
        :collapse-transition="false"
        :unique-opened="false"
        mode="vertical"
        background-color="#001529"
        text-color="#bfcbd9"
        active-text-color="#409eff"
        router
      >
        <SidebarItem
          v-for="route in routes"
          :key="route.path"
          :item="route"
          :base-path="'/' + route.path"
        />
      </el-menu>
    </el-scrollbar>
  </aside>
</template>

<script setup>
/**
 * 侧边菜单（对应课程：展开和收起菜单 / 菜单选中和路由关联）
 */
import { computed } from 'vue'
import { useStore } from 'vuex'
import { useRoute } from 'vue-router'
import Logo from './Logo.vue'
import SidebarItem from './SidebarItem.vue'

const store = useStore()
const route = useRoute()

const opened = computed(() => store.state.app.sidebar.opened)
const routes = computed(() => store.getters.permissionRoutes)

// 当前选中菜单（跟随路由）
const activeMenu = computed(() => {
  const { meta, path } = route
  return meta.activeMenu || path
})
</script>

<style scoped lang="scss">
.sidebar {
  width: var(--sidebar-width);
  height: 100%;
  background-color: var(--sidebar-bg);
  display: flex;
  flex-direction: column;
  transition: width 0.28s;
  overflow: hidden;

  &.collapsed {
    width: var(--sidebar-collapsed-width);
  }

  .sidebar-scroll {
    flex: 1;
    overflow: hidden;
  }

  :deep(.el-menu) {
    border-right: none;
    width: 100%;
  }

  :deep(.el-menu-item.is-active) {
    background-color: #409eff20;
    border-right: 3px solid #409eff;
  }
}
</style>
