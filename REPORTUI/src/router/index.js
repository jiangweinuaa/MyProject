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
    redirect: '/smart-query',
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
        path: '/category-sale-analysis',
        name: 'CategorySaleAnalysis',
        component: () => import('@/views/CategorySaleAnalysis.vue'),
        meta: { title: '品类销售分析', icon: 'DataLine' }
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
      },
      {
        path: '/product-recognition',
        name: 'ProductRecognition',
        component: () => import('@/views/ProductRecognition.vue'),
        meta: { title: '商品识别', icon: 'Picture' }
      },
      {
        path: '/training-library',
        name: 'TrainingLibrary',
        component: () => import('@/views/TrainingLibrary.vue'),
        meta: { title: '训练库管理', icon: 'FolderOpened' }
      },
      {
        path: '/smart-query',
        name: 'SmartQuery',
        component: () => import('@/views/SmartQuery.vue'),
        meta: { title: '🤖 智问', icon: 'ChatDotRound' }
      },
      {
        path: '/report-designer',
        name: 'ReportDesigner',
        component: () => import('@/views/ReportDesigner.vue'),
        meta: { title: '📊 AI 报表设计器', icon: 'DataBoard' }
      },
      {
        path: '/prompt-config',
        name: 'PromptConfig',
        component: () => import('@/views/PromptConfig.vue'),
        meta: { title: '⚙️ 智问配置', icon: 'Setting' }
      },
      {
        path: '/my-models',
        name: 'MyModels',
        component: () => import('@/views/MyModels.vue'),
        meta: { title: '🤖 我的模型', icon: 'Cpu' }
      },
      {
        path: '/retrain',
        name: 'Retrain',
        component: () => import('@/views/Retrain.vue'),
        meta: { title: '商品重新训练', icon: 'RefreshRight' }
      },
      {
        path: '/sales-accuracy-analysis',
        name: 'SalesAccuracyAnalysis',
        component: () => import('@/views/SalesAccuracyAnalysis.vue'),
        meta: { title: '销售预估准确性', icon: 'TrendCharts' }
      },
      {
        path: '/goods-forecast-accuracy',
        name: 'GoodsForecastAccuracy',
        component: () => import('@/views/GoodsForecastAccuracyAnalysis.vue'),
        meta: { title: '单品预估准确性', icon: 'ShoppingCart' }
      },
      {
        path: '/selection-simulator',
        name: 'SelectionSimulator',
        component: () => import('@/views/SelectionSimulator.vue'),
        meta: { title: '甄选模拟', icon: 'Star' }
      },
      {
        path: '/selection-simulator2',
        name: 'SelectionSimulator2',
        component: () => import('@/views/SelectionSimulator2.vue'),
        meta: { title: '甄选模拟 2', icon: 'ShoppingCart' }
      },
      {
        path: '/api-doc',
        name: 'ApiDoc',
        component: () => import('@/views/ApiDoc.vue'),
        meta: { title: 'API 文档', icon: 'Document' }
      },
      {
        path: '/schema-test',
        name: 'SchemaTest',
        component: () => import('@/views/SchemaTest.vue'),
        meta: { title: '🔍 Schema 检索测试', icon: 'Search' }
      },
      {
        path: '/dashvector-test',
        name: 'DashVectorTest',
        component: () => import('@/views/DashVectorTest.vue'),
        meta: { title: '🔮 DashVector 检索测试', icon: 'MagicStick' }
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
    // 优先从 URL 参数获取 token，其次从 localStorage
    const urlToken = to.query.token
    const localToken = localStorage.getItem('token')
    const token = urlToken || localToken
    
    if (token) {
      // 如果 URL 有 token，保存到 localStorage
      if (urlToken) {
        localStorage.setItem('token', urlToken)
      }
      next()
    } else {
      next('/login')
    }
  }
})

export default router
