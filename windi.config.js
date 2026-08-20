import { defineConfig } from 'windicss/helpers'

export default defineConfig({
  darkMode: 'class',
  theme: {
    extend: {
      colors: {
        primary: '#409EFF',
        success: '#67C23A',
        warning: '#E6A23C',
        danger: '#F56C6C',
        info: '#909399'
      },
      boxShadow: {
        card: '0 1px 4px rgba(0, 21, 41, 0.08)'
      }
    }
  },
  shortcuts: {
    'flex-center': 'flex items-center justify-center',
    'flex-between': 'flex items-center justify-between',
    'app-card': 'bg-white rounded-10px shadow-card'
  },
  plugins: []
})
