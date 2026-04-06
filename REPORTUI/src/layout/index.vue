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
        router
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
              <span class="username pc-only">管理员</span>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item>个人设置</el-dropdown-item>
                <el-dropdown-item divided>退出登录</el-dropdown-item>
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
import { useRoute } from 'vue-router'

const route = useRoute()

const menus = [
  { path: '/sales-analysis', title: '销售分析', icon: 'DataAnalysis' },
  { path: '/goods-analysis', title: '商品销售分析', icon: 'Goods' },
  { path: '/stock-summary', title: '库存分析', icon: 'Box' },
  { path: '/stock-analysis', title: '商品库存分析', icon: 'Box' }
]

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
}

.el-menu-item {
  height: 50px;
  line-height: 50px;
  font-size: 14px;
}

.el-menu-item .el-icon {
  margin-right: 10px;
  font-size: 16px;
}

.el-menu-item span {
  font-size: 14px;
  color: #bfcbd9;
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
