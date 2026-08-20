import { fileURLToPath, URL } from 'node:url'
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import WindiCSS from 'vite-plugin-windicss'

export default defineConfig({
  plugins: [vue(), WindiCSS()],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  },
  css: {
    preprocessorOptions: {
      scss: {
        additionalData: `@use "@/styles/variables.scss" as *;`
      }
    }
  },
  server: {
    host: true,
    port: 5174,
    open: false,
    // 若接入真实后端，把 /api 代理到后端地址即可
    proxy: {
      // '/api': {
      //   target: 'http://localhost:8080',
      //   changeOrigin: true
      // }
    }
  },
  build: {
    chunkSizeWarningLimit: 1500,
    rollupOptions: {
      output: {
        manualChunks: {
          vendor: ['vue', 'vue-router', 'vuex', 'axios', 'element-plus', '@element-plus/icons-vue'],
          echarts: ['echarts']
        }
      }
    }
  }
})
