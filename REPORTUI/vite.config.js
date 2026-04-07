import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, 'src')
    }
  },
  define: {
    'import.meta.env.VITE_API_BASE_URL': JSON.stringify('http://47.100.138.89:8110/api')
  },
  server: {
    host: '0.0.0.0',
    port: 3000,
    proxy: {
      '/api': {
        target: 'http://localhost:8110',
        changeOrigin: true
      }
    }
  },
  build: {
    // 每次构建生成不同的 hash，确保浏览器加载最新文件
    rollupOptions: {
      output: {
        manualChunks: {
          'vendor': ['vue', 'vue-router', 'pinia', 'element-plus'],
          'echarts': ['echarts']
        }
      }
    },
    // 在文件名中添加 hash，文件内容变化时文件名会变化
    assetsDir: 'assets',
    // 禁用 CSS 代码分割，避免缓存问题
    cssCodeSplit: false
  }
})
