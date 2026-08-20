<template>
  <div class="logo" @click="goHome">
    <img src="/favicon.svg" alt="logo" class="logo-img" />
    <transition name="fade">
      <span v-if="!collapsed" class="logo-title">后台管理系统</span>
    </transition>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

defineProps({
  collapsed: { type: Boolean, default: false }
})

const router = useRouter()

function goHome() {
  router.push('/dashboard')
}
</script>

<style scoped lang="scss">
.logo {
  height: 50px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  overflow: hidden;
  cursor: pointer;
  background-color: #002140;

  .logo-img {
    width: 30px;
    height: 30px;
    flex-shrink: 0;
  }

  .logo-title {
    color: #fff;
    font-size: 16px;
    font-weight: 600;
    white-space: nowrap;
  }
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
