import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import 'element-plus/dist/index.css'
import zhCn from 'element-plus/es/locale/lang/zh-cn'
import VConsole from 'vconsole'

import App from './App.vue'
import router from './router'
import './styles/index.scss'

// 初始化 vConsole（仅在非生产环境显示）
if (import.meta.env.DEV) {
  new VConsole({
    defaultPlugins: ['system', 'network', 'element', 'storage'],
    maxLogNumber: 1000,
    onReady: () => {
      console.log('vConsole 已初始化')
    }
  })
}

const app = createApp(App)
const pinia = createPinia()

// 注册所有 Element Plus 图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

app.use(pinia)
app.use(router)
app.use(ElementPlus, { locale: zhCn })

app.mount('#app')
