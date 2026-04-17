import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { resolve } from 'path'

// https://vite.dev/config/
export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': resolve(__dirname, 'src')
    }
  },
  server: {
    port: 9001,
    host: '0.0.0.0',
    open: false,
    proxy: {
      // 代理所有 DCP 服务请求
      '/dcpService': {
        target: 'http://retaildev.digiwin.com.cn',
        changeOrigin: true,
        secure: false,
        ws: true
      },
      // 代理 CRM 服务
      '/crmService': {
        target: 'http://retaildev.digiwin.com.cn',
        changeOrigin: true,
        secure: false
      },
      // 代理服务
      '/promService': {
        target: 'http://retaildev.digiwin.com.cn',
        changeOrigin: true,
        secure: false
      },
      // 代理 MES 服务
      '/mesService': {
        target: 'http://retaildev.digiwin.com.cn',
        changeOrigin: true,
        secure: false
      },
      // 代理 EIP 服务
      '/eipService': {
        target: 'http://retaildev.digiwin.com.cn',
        changeOrigin: true,
        secure: false
      },
      // 代理 CMS 服务
      '/cmsService': {
        target: 'http://retaildev.digiwin.com.cn',
        changeOrigin: true,
        secure: false
      }
    }
  },
  css: {
    preprocessorOptions: {
      scss: {
        additionalData: `@use "@/styles/variables.scss" as *;`
      }
    }
  }
})
