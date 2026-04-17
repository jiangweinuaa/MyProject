<template>
  <el-container class="layout-container">
    <!-- 侧边栏 -->
    <el-aside :width="isCollapse ? '64px' : '220px'" class="layout-aside">
      <div class="logo-container">
        <h2 v-if="!isCollapse">IBPUI</h2>
        <h2 v-else>IBP</h2>
      </div>
      <el-menu
        :default-active="activeMenu"
        :collapse="isCollapse"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
        router
      >
        <template v-for="menu in menus" :key="menu.path">
          <el-menu-item v-if="!menu.children" :index="menu.path">
            <el-icon><component :is="menu.icon" /></el-icon>
            <template #title>{{ menu.title }}</template>
          </el-menu-item>
          <el-sub-menu v-else :index="menu.path">
            <template #title>
              <el-icon><component :is="menu.icon" /></el-icon>
              <span>{{ menu.title }}</span>
            </template>
            <el-menu-item
              v-for="child in menu.children"
              :key="child.path"
              :index="child.path"
            >
              {{ child.title }}
            </el-menu-item>
          </el-sub-menu>
        </template>
      </el-menu>
    </el-aside>

    <el-container>
      <!-- 顶部导航 -->
      <el-header class="layout-header">
        <div class="header-left">
          <el-icon class="collapse-btn" @click="toggleCollapse">
            <Fold v-if="!isCollapse" />
            <Expand v-else />
          </el-icon>
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item v-if="currentRoute">{{ currentRoute }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <span class="user-info">
              <el-avatar :size="32" :icon="User" />
              <span class="username">{{ username }}</span>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                <el-dropdown-item command="settings">系统设置</el-dropdown-item>
                <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <!-- 主内容区 -->
      <el-main class="layout-main">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { User, Fold, Expand } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const isCollapse = ref(false)
const username = computed(() => userStore.userInfo?.username || '用户')

const activeMenu = computed(() => route.path)
const currentRoute = computed(() => route.meta.title as string)

// 菜单配置 - 从登录返回的 myPower 读取（与 V3XUI 一致）
const menus = ref<any[]>([])

// 初始化菜单（从 userInfo 中读取 myPower）
const initMenus = () => {
  const userInfo = userStore.userInfo
  if (userInfo?.myPower && Array.isArray(userInfo.myPower)) {
    // 将 V3XUI 的 myPower 格式转换为 Element Plus 菜单格式
    menus.value = userInfo.myPower.map((item: any) => {
      const menuItem: any = {
        path: item.proName ? `/${item.proName}` : '#',
        title: item.modularName,
        icon: item.icon || 'Folder',
        modularNo: item.modularNo,
        modularLevel: item.modularLevel
      }
      
      // 处理子菜单
      if (item.children && item.children.length > 0) {
        menuItem.children = item.children.map((child: any) => ({
          path: child.proName ? `/${child.proName}` : '#',
          title: child.modularName,
          modularNo: child.modularNo
        }))
      }
      
      return menuItem
    }).filter((item: any) => item.modularLevel === '1') // 只取一级菜单
  } else {
    // 如果没有 myPower，使用默认菜单（开发用）
    menus.value = [
      {
        path: '/home',
        title: '首页',
        icon: 'Home'
      },
      {
        path: '/system',
        title: '系统管理',
        icon: 'Setting',
        children: [
          { path: '/system/user', title: '用户管理' },
          { path: '/system/role', title: '角色管理' },
          { path: '/system/menu', title: '菜单管理' },
          { path: '/system/dept', title: '部门管理' },
          { path: '/system/post', title: '岗位管理' }
        ]
      }
    ]
  }
}

// 监听 userInfo 变化
watch(() => userStore.userInfo, () => {
  initMenus()
}, { immediate: true, deep: true })

const toggleCollapse = () => {
  isCollapse.value = !isCollapse.value
}

const handleCommand = (command: string) => {
  switch (command) {
    case 'logout':
      userStore.logout()
      router.push('/login')
      break
    case 'profile':
      router.push('/profile')
      break
    case 'settings':
      router.push('/settings')
      break
  }
}
</script>

<style lang="scss" scoped>
.layout-container {
  height: 100vh;
}

.layout-aside {
  background-color: #304156;
  transition: width 0.3s;
  overflow: hidden;

  .logo-container {
    height: 60px;
    display: flex;
    align-items: center;
    justify-content: center;
    background-color: #2b3a4b;

    h2 {
      color: #fff;
      font-size: 18px;
      font-weight: 600;
      margin: 0;
    }
  }

  :deep(.el-menu) {
    border-right: none;
  }
}

.layout-header {
  background-color: #fff;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;

  .header-left {
    display: flex;
    align-items: center;
    gap: 16px;

    .collapse-btn {
      font-size: 20px;
      cursor: pointer;
      transition: color 0.3s;

      &:hover {
        color: $primary-color;
      }
    }
  }

  .header-right {
    .user-info {
      display: flex;
      align-items: center;
      gap: 8px;
      cursor: pointer;

      .username {
        font-size: 14px;
        color: #606266;
      }
    }
  }
}

.layout-main {
  background-color: #f5f7fa;
  padding: 20px;
}

// 过渡动画
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
