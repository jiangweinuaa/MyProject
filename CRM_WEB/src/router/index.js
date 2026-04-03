import { createRouter, createWebHistory } from 'vue-router'
import Layout from '../views/Layout.vue'
import CardQuery from '../views/CardQuery.vue'

const routes = [
  {
    path: '/',
    component: Layout,
    redirect: '/card-query',
    children: [
      {
        path: '/card-query',
        name: 'CardQuery',
        component: CardQuery,
        meta: { title: '卡信息查询' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
