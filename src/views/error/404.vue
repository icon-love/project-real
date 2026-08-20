<template>
  <div class="not-found">
    <div class="code">404</div>
    <p class="text">抱歉，您访问的页面不存在或已被删除</p>
    <el-button type="primary" size="large" @click="goHome">返回首页</el-button>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { getToken } from '@/utils/auth'

const router = useRouter()

function goHome() {
  router.push(getToken() ? '/dashboard' : '/login')
}
</script>

<style scoped lang="scss">
.not-found {
  height: 100vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: #f0f2f5;

  .code {
    font-size: 120px;
    font-weight: 700;
    color: #409eff;
    line-height: 1;
  }

  .text {
    color: #909399;
    margin: 16px 0 24px;
  }
}
</style>
