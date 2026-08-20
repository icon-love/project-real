<template>
  <div class="tags-view">
    <el-scrollbar class="tags-scrollbar">
      <div class="tags-wrap">
        <router-link
          v-for="tag in visitedViews"
          :key="tag.path"
          :to="{ path: tag.path, query: tag.query }"
          class="tag-item"
          :class="{ active: isActive(tag) }"
          @contextmenu.prevent="openContextMenu(tag, $event)"
        >
          <span class="dot" />
          {{ tag.title }}
          <el-icon
            v-if="!tag.meta?.affix"
            class="tag-close"
            @click.prevent.stop="closeTag(tag)"
          >
            <Close />
          </el-icon>
        </router-link>
      </div>
    </el-scrollbar>

    <!-- 右键菜单 -->
    <ul
      v-show="menuVisible"
      class="context-menu"
      :style="{ left: menuLeft + 'px', top: menuTop + 'px' }"
    >
      <li @click="refreshSelectedTag">刷新</li>
      <li v-if="!selectedTag?.meta?.affix" @click="closeSelectedTag">关闭</li>
      <li @click="closeOthersTags">关闭其他</li>
      <li @click="closeAllTags">关闭全部</li>
    </ul>
  </div>
</template>

<script setup>
/**
 * 标签导航组件（对应课程：标签导航组件实现）
 */
import { ref, computed, watch, inject, onBeforeUnmount } from 'vue'
import { useStore } from 'vuex'
import { useRoute, useRouter } from 'vue-router'

const store = useStore()
const route = useRoute()
const router = useRouter()
const refreshPage = inject('refreshPage', () => {})

const visitedViews = computed(() => store.getters.visitedViews)

// 路由变化时新增标签
watch(
  () => route.path,
  () => {
    if (route.meta?.hidden) return
    store.dispatch('app/addView', route)
  },
  { immediate: true }
)

function isActive(tag) {
  return tag.path === route.path
}

// 关闭标签
async function closeTag(view) {
  const { visitedViews: views } = await store.dispatch('app/delView', view)
  if (isActive(view)) {
    const latest = views[views.length - 1]
    if (latest) {
      router.push(latest.fullPath)
    } else {
      router.push('/dashboard')
    }
  }
}

// ---------- 右键菜单 ----------
const menuVisible = ref(false)
const menuLeft = ref(0)
const menuTop = ref(0)
const selectedTag = ref(null)

function openContextMenu(tag, e) {
  selectedTag.value = tag
  menuVisible.value = true
  menuLeft.value = e.clientX
  menuTop.value = e.clientY
}

function closeMenu() {
  menuVisible.value = false
}

watch(menuVisible, (v) => {
  if (v) document.addEventListener('click', closeMenu)
  else document.removeEventListener('click', closeMenu)
})
onBeforeUnmount(() => document.removeEventListener('click', closeMenu))

function refreshSelectedTag() {
  refreshPage()
  closeMenu()
}

async function closeSelectedTag() {
  if (selectedTag.value) {
    await closeTag(selectedTag.value)
  }
  closeMenu()
}

async function closeOthersTags() {
  if (selectedTag.value) {
    await store.dispatch('app/delOthersViews', selectedTag.value)
    router.push(selectedTag.value.fullPath)
  }
  closeMenu()
}

async function closeAllTags() {
  await store.dispatch('app/delAllViews')
  router.push('/dashboard')
  closeMenu()
}
</script>

<style scoped lang="scss">
.tags-view {
  height: var(--tags-view-height);
  background: #fff;
  border-bottom: 1px solid #e4e7ed;
  box-shadow: 0 1px 3px rgba(0, 21, 41, 0.08);
  flex-shrink: 0;
  display: flex;
  align-items: center;
  padding: 0 8px;
  z-index: 9;

  .tags-scrollbar {
    flex: 1;
    white-space: nowrap;
  }

  .tags-wrap {
    display: inline-flex;
    align-items: center;
    gap: 6px;
    padding: 2px 0;
  }

  .tag-item {
    display: inline-flex;
    align-items: center;
    gap: 5px;
    height: 26px;
    padding: 0 8px;
    font-size: 12px;
    color: #495060;
    border: 1px solid #d8dce5;
    border-radius: 4px;
    cursor: pointer;
    transition: all 0.2s;

    .dot {
      width: 8px;
      height: 8px;
      border-radius: 50%;
      background: #d8dce5;
    }

    .tag-close {
      font-size: 12px;
      border-radius: 50%;
      padding: 1px;

      &:hover {
        background: rgba(0, 0, 0, 0.15);
        color: #fff;
      }
    }

    &.active {
      background: #409eff;
      border-color: #409eff;
      color: #fff;

      .dot {
        background: #fff;
      }
    }
  }
}

.context-menu {
  position: fixed;
  z-index: 3000;
  margin: 0;
  padding: 6px 0;
  list-style: none;
  background: #fff;
  border-radius: 6px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.15);
  font-size: 13px;

  li {
    padding: 7px 20px;
    cursor: pointer;

    &:hover {
      background: #ecf5ff;
      color: #409eff;
    }
  }
}
</style>
