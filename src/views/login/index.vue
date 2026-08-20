<template>
  <div class="login-page">
    <div class="login-left hidden lg:flex">
      <div class="brand">
        <img src="/favicon.svg" alt="logo" class="brand-logo" />
        <h1>后台管理系统</h1>
        <p>高效 · 简洁 · 优雅的权限管理解决方案</p>
        <ul>
          <li>✅ 动态路由 · 菜单权限</li>
          <li>✅ 按钮级权限控制</li>
          <li>✅ 数据可视化看板</li>
          <li>✅ 图库 / 公告 / 管理员管理</li>
        </ul>
      </div>
    </div>

    <div class="login-right">
      <div class="login-card">
        <h2 class="title">欢迎登录</h2>
        <p class="subtitle">请输入账号信息登录系统</p>

        <el-form
          ref="formRef"
          :model="form"
          :rules="rules"
          size="large"
          @keyup.enter="handleLogin"
        >
          <el-form-item prop="username">
            <el-input
              v-model="form.username"
              placeholder="请输入用户名"
              :prefix-icon="User"
              clearable
            />
          </el-form-item>
          <el-form-item prop="password">
            <el-input
              v-model="form.password"
              type="password"
              show-password
              placeholder="请输入密码"
              :prefix-icon="Lock"
            />
          </el-form-item>
          <el-form-item>
            <div class="flex-between w-full">
              <el-checkbox v-model="form.remember">记住用户名</el-checkbox>
              <el-link type="primary" :underline="'never'">忘记密码？</el-link>
            </div>
          </el-form-item>
          <el-form-item>
            <el-button
              type="primary"
              class="login-btn"
              :loading="loading"
              @click="handleLogin"
            >
              {{ loading ? '登录中...' : '登 录' }}
            </el-button>
          </el-form-item>
        </el-form>

        <div class="tips">演示账号：admin &nbsp;/&nbsp; 密码：123456</div>
      </div>
    </div>
  </div>
</template>

<script setup>
/**
 * 登录页（对应课程：登录页开发 / 响应式处理 / 表单验证处理）
 */
import { ref, reactive, onMounted } from 'vue'
import { useStore } from 'vuex'
import { useRoute, useRouter } from 'vue-router'
import { User, Lock } from '@element-plus/icons-vue'

const store = useStore()
const route = useRoute()
const router = useRouter()

const formRef = ref(null)
const loading = ref(false)
const form = reactive({
  username: 'admin',
  password: '123456',
  remember: true
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于 6 位', trigger: 'blur' }
  ]
}

onMounted(() => {
  // 记住用户名
  const saved = localStorage.getItem('admin_remember_username')
  if (saved) {
    form.username = saved
    form.remember = true
  }
})

async function handleLogin() {
  try {
    await formRef.value.validate()
  } catch (e) {
    return
  }
  loading.value = true
  try {
    if (form.remember) {
      localStorage.setItem('admin_remember_username', form.username)
    } else {
      localStorage.removeItem('admin_remember_username')
    }
    await store.dispatch('user/login', {
      username: form.username,
      password: form.password
    })
    const redirect = route.query.redirect || '/'
    router.push(redirect)
  } catch (e) {
    // 错误已在拦截器提示
  } finally {
    loading.value = false
  }
}
</script>

<style scoped lang="scss">
.login-page {
  height: 100%;
  width: 100%;
  display: flex;
  background: linear-gradient(135deg, #1f2d3d 0%, #001529 100%);
}

// 左侧品牌区
.login-left {
  flex: 1;
  align-items: center;
  justify-content: center;
  color: #fff;
  background: linear-gradient(
    160deg,
    rgba(64, 158, 255, 0.25) 0%,
    rgba(54, 207, 201, 0.15) 100%
  );

  .brand {
    max-width: 460px;
    padding: 0 40px;

    .brand-logo {
      width: 56px;
      height: 56px;
    }

    h1 {
      font-size: 30px;
      margin: 18px 0 10px;
    }

    p {
      color: rgba(255, 255, 255, 0.75);
      margin-bottom: 30px;
    }

    ul {
      list-style: none;
      padding: 0;
      color: rgba(255, 255, 255, 0.85);
      font-size: 15px;
      line-height: 2.2;
    }
  }
}

// 右侧登录卡片
.login-right {
  width: 480px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 24px;
}

.login-card {
  width: 100%;
  max-width: 380px;
  background: #fff;
  border-radius: 12px;
  padding: 40px 36px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);

  .title {
    margin: 0 0 6px;
    font-size: 24px;
    color: #303133;
  }

  .subtitle {
    margin: 0 0 28px;
    font-size: 14px;
    color: #909399;
  }

  .login-btn {
    width: 100%;
  }

  .tips {
    margin-top: 12px;
    text-align: center;
    font-size: 12px;
    color: #a8abb2;
  }
}

// 小屏适配（对应课程：登录页响应式处理）
@media (max-width: 768px) {
  .login-right {
    width: 100%;
    padding: 16px;
  }
}
</style>
