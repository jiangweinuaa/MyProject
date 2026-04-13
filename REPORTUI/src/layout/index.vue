<template>
  <div class="layout-container">
    <!-- 左侧菜单 (PC 端) -->
    <el-aside width="200px" class="sidebar pc-sidebar" :class="{ 'sidebar-collapse': sidebarCollapse }">
      <div class="logo">
        <h2 v-show="!sidebarCollapse">🦞 REPORT</h2>
        <h2 v-show="sidebarCollapse">🦞</h2>
      </div>
      <el-menu
        :default-active="activeMenu"
        :collapse="sidebarCollapse"
        :collapse-transition="false"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
        @select="handleMenuSelect"
      >
        <el-menu-item
          v-for="menu in menus"
          :key="menu.path"
          :index="menu.path"
        >
          <el-icon><component :is="menu.icon" /></el-icon>
          <span>{{ menu.title }}</span>
        </el-menu-item>
      </el-menu>
      <!-- 版本号 -->
      <div class="version-info" :class="{ 'version-collapse': sidebarCollapse }">
        <span v-if="!sidebarCollapse">v{{ version }}</span>
        <span v-else>🦞</span>
      </div>
    </el-aside>

    <!-- 移动端侧边栏 -->
    <el-drawer
      v-model="mobileDrawerVisible"
      title="菜单"
      direction="ltr"
      size="200px"
      class="mobile-drawer"
    >
      <div class="mobile-logo">
        <h2>🦞 REPORT</h2>
      </div>
      <div class="mobile-menu-container">
        <el-menu
          :default-active="activeMenu"
          background-color="#304156"
          text-color="#bfcbd9"
          active-text-color="#409EFF"
          router
          @select="handleMobileMenuSelect"
        >
          <el-menu-item
            v-for="menu in menus"
            :key="menu.path"
            :index="menu.path"
          >
            <el-icon><component :is="menu.icon" /></el-icon>
            <span>{{ menu.title }}</span>
          </el-menu-item>
        </el-menu>
        
        <!-- 移动端版本号 -->
        <div class="mobile-version-info">
          <span>v{{ version }}</span>
        </div>
      </div>
    </el-drawer>

    <!-- 右侧内容区 -->
    <el-container class="main-container">
      <!-- 顶部导航 -->
      <el-header class="header">
        <div class="header-left">
          <!-- 移动端菜单按钮 -->
          <el-icon class="menu-toggle mobile-only" @click="mobileDrawerVisible = true">
            <Fold />
          </el-icon>
          <!-- PC 端折叠按钮 -->
          <el-icon class="menu-toggle pc-only" @click="toggleSidebar">
            <Fold v-if="!sidebarCollapse" />
            <Expand v-else />
          </el-icon>
          <span class="breadcrumb">首页 / {{ currentTitle }}</span>
        </div>
        <div class="header-right">
          <el-dropdown>
            <span class="user-info">
              <el-avatar :size="32" icon="UserFilled" />
              <span class="username pc-only">{{ userInfo.username || '管理员' }}</span>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item>个人设置</el-dropdown-item>
                <el-dropdown-item divided @click="handleLogout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <!-- 内容区域 -->
      <el-main class="content">
        <router-view />
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { logout } from '@/api/auth'
// 导入所有菜单图标
import {
  DataAnalysis,
  Goods,
  DataLine,
  Box,
  List,
  Document,
  Picture,
  Camera,
  FolderOpened,
  TrendCharts,
  ShoppingCart,
  Star,
  Fold,
  Expand
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()

// 版本号（格式：1.0.yymmdd.HHMM）
const version = '1.0.260408.2240'

const menus = [
  { path: '/sales-analysis', title: '销售分析', icon: 'DataAnalysis' },
  { path: '/goods-analysis', title: '商品销售分析', icon: 'Goods' },
  { path: '/category-sale-analysis', title: '品类销售分析', icon: 'DataLine' },
  { path: '/stock-summary', title: '库存分析', icon: 'Box' },
  { path: '/stock-analysis', title: '商品库存分析', icon: 'Box' },
  { path: '/dcp-sale-qty', title: '商品销售明细', icon: 'List' },
  { path: '/api-doc', title: 'API 文档', icon: 'Document' },
  { path: '/product-recognition', title: '商品识别', icon: 'Picture' },
  { path: '/product-training', title: '商品训练', icon: 'Camera' },
  { path: '/training-library', title: '训练库管理', icon: 'FolderOpened' },
  { path: '/retrain', title: '商品重新训练', icon: 'RefreshRight' },
  { path: '/sales-accuracy-analysis', title: '销售预估准确性', icon: 'TrendCharts' },
  { path: '/goods-forecast-accuracy', title: '单品预估准确性', icon: 'ShoppingCart' },
  { path: '/selection-simulator', title: '甄选模拟', icon: 'Star' },
  { path: '/selection-simulator2', title: '甄选模拟 2', icon: 'ShoppingCart' }
]

// 用户信息
const userInfo = ref({})

// 加载用户信息
const loadUserInfo = () => {
  const opno = localStorage.getItem('opno')
  const eid = localStorage.getItem('eid')
  if (opno) {
    userInfo.value = {
      opno,
      eid,
      username: opno
    }
  }
}

// 退出登录
const handleLogout = async () => {
  try {
    await logout()
    ElMessage.success('已退出登录')
    router.push('/login')
  } catch (error) {
    // 即使接口失败也清除本地状态
    localStorage.removeItem('token')
    localStorage.removeItem('opno')
    localStorage.removeItem('eid')
    localStorage.removeItem('userInfo')
    ElMessage.success('已退出登录')
    router.push('/login')
  }
}

const activeMenu = computed(() => route.path)
const currentTitle = computed(() => {
  const menu = menus.find(m => m.path === route.path)
  return menu ? menu.title : '首页'
})

// 侧边栏状态
const sidebarCollapse = ref(false)
const mobileDrawerVisible = ref(false)

// 判断是否为移动端
const isMobile = ref(false)

const checkMobile = () => {
  isMobile.value = window.innerWidth <= 768
  if (isMobile.value) {
    sidebarCollapse.value = true
  } else {
    sidebarCollapse.value = false
  }
}

// 切换侧边栏
const toggleSidebar = () => {
  sidebarCollapse.value = !sidebarCollapse.value
}

// PC 端菜单选择处理
const handleMenuSelect = (index) => {
  console.log('菜单点击:', index)
  router.push(index)
}

// 移动端菜单选择后关闭抽屉
const handleMobileMenuSelect = () => {
  mobileDrawerVisible.value = false
}

// 监听窗口大小变化
const handleResize = () => {
  checkMobile()
}

onMounted(() => {
  checkMobile()
  loadUserInfo()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
})
</script>

<style scoped>
.layout-container {
  display: flex;
  height: 100vh;
  width: 100%;
}

/* 左侧菜单 */
.sidebar {
  background-color: #304156;
  color: #fff;
  overflow-x: hidden;
  transition: width 0.3s;
  display: flex;
  flex-direction: column;
  position: relative;
}

.sidebar-collapse {
  width: 64px !important;
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #2b3a4b;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  overflow: hidden;
}

.logo h2 {
  color: #fff;
  font-size: 20px;
  font-weight: 600;
  white-space: nowrap;
  margin: 0;
}

.el-menu {
  border-right: none;
  cursor: pointer;
  flex: 1;
}

.el-menu-item {
  height: 50px;
  line-height: 50px;
  font-size: 14px;
  cursor: pointer;
  pointer-events: auto !important;
}

.el-menu-item .el-icon {
  margin-right: 10px;
  font-size: 16px;
  pointer-events: none;
}

.el-menu-item span {
  font-size: 14px;
  color: #bfcbd9;
  pointer-events: none;
}

.el-menu-item:hover {
  background-color: #263445 !important;
}

.el-menu-item:hover span {
  color: #fff;
}

.el-menu-item.is-active {
  background-color: #409EFF !important;
}

.el-menu-item.is-active span {
  color: #fff;
}

/* 版本号样式 */
.version-info {
  margin-top: auto;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #263445;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  font-size: 11px;
  color: #7a8c99;
  transition: all 0.3s;
  flex-shrink: 0;
}

.version-info.version-collapse {
  height: 50px;
}

.version-info span {
  font-family: 'Courier New', monospace;
  letter-spacing: 1px;
}

/* 移动端版本号可见性 */
@media screen and (max-width: 768px) {
  .version-info {
    font-size: 10px;
    color: #8a9caa;
  }
  
  .version-info span {
    font-size: 9px;
  }
}

/* 移动端抽屉菜单 */
.mobile-drawer :deep(.el-drawer__header) {
  margin-bottom: 0;
  padding: 15px;
  background-color: #2b3a4b;
}

.mobile-drawer :deep(.el-drawer__title) {
  color: #fff;
  font-size: 18px;
}

.mobile-drawer :deep(.el-drawer__body) {
  padding: 0;
}

.mobile-logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #2b3a4b;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.mobile-logo h2 {
  color: #fff;
  font-size: 18px;
  margin: 0;
}

/* 移动端菜单容器 */
.mobile-menu-container {
  display: flex;
  flex-direction: column;
  height: calc(100vh - 60px);
}

.mobile-menu-container :deep(.el-menu) {
  flex: 1;
  overflow-y: auto;
  border-right: none;
}

/* 移动端版本号样式 */
.mobile-version-info {
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #263445;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  font-size: 10px;
  color: #8a9caa;
  flex-shrink: 0;
}

.mobile-version-info span {
  font-family: 'Courier New', monospace;
  letter-spacing: 1px;
}

/* 主容器 */
.main-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

/* 顶部导航 */
.header {
  height: 60px;
  background-color: #fff;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 10px;
}

.menu-toggle {
  font-size: 20px;
  cursor: pointer;
  color: #606266;
  padding: 5px;
  border-radius: 4px;
  transition: background-color 0.3s;
}

.menu-toggle:hover {
  background-color: #f5f5f5;
}

.breadcrumb {
  font-size: 14px;
  color: #606266;
}

.header-right {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
}

.username {
  font-size: 14px;
  color: #606266;
}

/* 内容区域 */
.content {
  flex: 1;
  background-color: #f0f2f5;
  padding: 20px;
  overflow-y: auto;
}

/* 响应式样式 */
@media screen and (max-width: 768px) {
  /* 隐藏 PC 侧边栏 */
  .pc-sidebar {
    display: none;
  }

  /* 移动端显示的元素 */
  .mobile-only {
    display: inline-flex !important;
  }

  /* PC 端显示的元素 */
  .pc-only {
    display: none !important;
  }

  /* 移动端头部调整 */
  .header {
    padding: 0 15px;
  }

  .breadcrumb {
    font-size: 13px;
  }

  /* 移动端内容区调整 */
  .content {
    padding: 10px;
  }
}

@media screen and (min-width: 769px) {
  /* 隐藏移动端元素 */
  .mobile-only {
    display: none !important;
  }

  /* PC 端显示的元素 */
  .pc-only {
    display: inline-flex !important;
  }
}
</style>
