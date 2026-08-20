<template>
  <div class="navbar">
    <div class="navbar-left">
      <!-- 折叠按钮 -->
      <div class="action hamburger" @click="toggleSidebar">
        <el-icon :size="20">
          <Expand v-if="sidebar.opened" />
          <Fold v-else />
        </el-icon>
      </div>
      <Breadcrumb />
    </div>

    <div class="navbar-right">
      <!-- 刷新 -->
      <el-tooltip content="刷新" placement="bottom">
        <div class="action" @click="handleRefresh">
          <el-icon><Refresh /></el-icon>
        </div>
      </el-tooltip>

      <!-- 全屏 -->
      <el-tooltip :content="isFullscreen ? '退出全屏' : '全屏'" placement="bottom">
        <div class="action" @click="toggle">
          <el-icon><FullScreen /></el-icon>
        </div>
      </el-tooltip>

      <!-- 用户下拉 -->
      <el-dropdown trigger="click" @command="handleCommand">
        <div class="user-info">
          <el-avatar :size="30" :src="userInfo.avatar">
            {{ (userInfo.nickname || 'U').slice(0, 1) }}
          </el-avatar>
          <span class="nickname">{{ userInfo.nickname || userInfo.username }}</span>
          <el-icon class="text-gray-400"><ArrowDown /></el-icon>
        </div>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item command="password">
              <el-icon><Key /></el-icon>修改密码
            </el-dropdown-item>
            <el-dropdown-item command="logout" divided>
              <el-icon><SwitchButton /></el-icon>退出登录
            </el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>

    <ChangePassword v-model="passwordVisible" />
  </div>
</template>

<script setup>
/**
 * 公共头部（对应课程：公共头部开发-样式布局/刷新和全屏/修改密码）
 */
import { ref, computed, inject } from 'vue'
import { useStore } from 'vuex'
import { useRouter } from 'vue-router'
import { ElMessageBox } from 'element-plus'
import { useFullscreen } from '@vueuse/core'
import Breadcrumb from './Breadcrumb.vue'
import ChangePassword from '@/components/ChangePassword.vue'

const store = useStore()
const router = useRouter()
const refreshPage = inject('refreshPage', () => {})

const sidebar = computed(() => store.state.app.sidebar)
const userInfo = computed(() => store.getters.userInfo)

// 全屏
const { isFullscreen, toggle } = useFullscreen()

const passwordVisible = ref(false)

function toggleSidebar() {
  store.dispatch('app/toggleSidebar')
}

function handleRefresh() {
  refreshPage()
}

async function handleCommand(command) {
  if (command === 'password') {
    passwordVisible.value = true
  } else if (command === 'logout') {
    try {
      await ElMessageBox.confirm('确定退出登录吗？', '系统提示', {
        type: 'warning',
        confirmButtonText: '确定',
        cancelButtonText: '取消'
      })
    } catch (e) {
      return
    }
    await store.dispatch('user/logout')
    router.push('/login')
  }
}
</script>

<style scoped lang="scss">
.navbar {
  height: var(--navbar-height);
  background: #fff;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 16px;
  flex-shrink: 0;
  z-index: 10;

  .navbar-left {
    display: flex;
    align-items: center;
    gap: 14px;
  }

  .navbar-right {
    display: flex;
    align-items: center;
    gap: 6px;
  }

  .action {
    width: 36px;
    height: 36px;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 6px;
    cursor: pointer;
    color: #5a5e66;
    transition: background 0.2s;

    &:hover {
      background: rgba(0, 0, 0, 0.04);
    }
  }

  .user-info {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 4px 10px;
    border-radius: 6px;
    cursor: pointer;
    outline: none;

    &:hover {
      background: rgba(0, 0, 0, 0.04);
    }

    .nickname {
      font-size: 14px;
      color: #303133;
    }
  }
}
</style>
