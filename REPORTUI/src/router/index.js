import { createRouter, createWebHistory } from 'vue-router'
import Layout from '@/layout/index.vue'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/',
    component: Layout,
    redirect: '/sales-analysis',
    children: [
      {
        path: '/sales-analysis',
        name: 'SalesAnalysis',
        component: () => import('@/views/SalesAnalysis.vue'),
        meta: { title: '销售分析', icon: 'DataAnalysis' }
      },
      {
        path: '/goods-analysis',
        name: 'GoodsAnalysis',
        component: () => import('@/views/GoodsAnalysis.vue'),
        meta: { title: '商品销售分析', icon: 'Goods' }
      },
      {
        path: '/stock-summary',
        name: 'StockSummary',
        component: () => import('@/views/StockSummary.vue'),
        meta: { title: '库存分析', icon: 'Box' }
      },
      {
        path: '/stock-analysis',
        name: 'StockAnalysis',
        component: () => import('@/views/StockAnalysis.vue'),
        meta: { title: '商品库存分析', icon: 'Box' }
      },
      {
        path: '/dcp-sale-qty',
        name: 'DcpSaleQty',
        component: () => import('@/views/DcpSaleQty.vue'),
        meta: { title: '商品销售明细', icon: 'List' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫 - 强制登录
router.beforeEach((to, from, next) => {
  // 如果访问登录页，直接放行
  if (to.path === '/login') {
    next()
  } else {
    // 访问其他页面，检查是否登录
    const token = localStorage.getItem('token')
    if (token) {
      next()
    } else {
      next('/login')
    }
  }
})

export default router
