import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import VConsole from 'vconsole'

import App from './App.vue'
import router from './router'

const app = createApp(App)
const pinia = createPinia()

// 初始化 vConsole（全局启用）
const vConsole = new VConsole({
  defaultPlugins: ['system', 'network', 'element', 'storage'],
  maxLogNumber: 1000,
  onReady: () => {
    console.log('vConsole 已初始化')
  }
})

// 注册 Element Plus 图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

app.use(pinia)
app.use(router)
app.use(ElementPlus, { locale: zhCn })

app.mount('#app')
