import { createRouter, createWebHistory } from 'vue-router'
import Layout from '@/layout/index.vue'

const routes = [
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
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
