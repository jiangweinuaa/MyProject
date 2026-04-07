<template>
  <div class="stock-summary">
    <!-- 返回按钮和标题 -->
    <div class="page-header">
      <el-button @click="goBack" :icon="ArrowLeft" size="default">
        返回
      </el-button>
      <span class="page-title">库存分析</span>
    </div>

    <!-- 查询条件 -->
    <el-card class="search-card" shadow="hover">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item class="button-group">
          <el-button type="primary" @click="handleSearch" :loading="loading" :icon="Search">
            查询
          </el-button>
          <el-button @click="handleReset" :icon="Refresh">
            重置
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 数据卡片 -->
    <el-row :gutter="10" class="data-cards">
      <el-col :xs="24" :sm="12" :md="8">
        <el-card shadow="hover" class="data-card">
          <div class="card-header">
            <el-icon class="card-icon" style="color: #409EFF"><ShoppingCart /></el-icon>
            <span>库存总金额</span>
          </div>
          <div class="card-value">¥ {{ formatNumber(summaryData.totalAmt) }}</div>
          <div class="card-footer">
            <span class="trend-label">总库存金额</span>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="8">
        <el-card shadow="hover" class="data-card">
          <div class="card-header">
            <el-icon class="card-icon" style="color: #67C23A"><Goods /></el-icon>
            <span>库存总数量</span>
          </div>
          <div class="card-value">{{ formatNumber(summaryData.totalQty) }}</div>
          <div class="card-footer">
            <span class="trend-label">总库存件数</span>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="8">
        <el-card shadow="hover" class="data-card">
          <div class="card-header">
            <el-icon class="card-icon" style="color: #E6A23C"><Shop /></el-icon>
            <span>门店数量</span>
          </div>
          <div class="card-value">{{ formatNumber(summaryData.shopCount) }}</div>
          <div class="card-footer">
            <span class="trend-label">有库存门店</span>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 门店库存占比饼图 -->
    <el-card class="chart-card" shadow="hover">
      <template #header>
        <div class="card-title">
          <el-icon><PieChart /></el-icon>
          <span>各门店库存金额占比</span>
        </div>
      </template>
      <div class="chart-container" ref="shopPieChartRef" id="shopPieChart"></div>
    </el-card>

    <!-- 库存明细表 -->
    <el-card class="table-card" shadow="hover">
      <template #header>
        <div class="card-title">
          <el-icon><Shop /></el-icon>
          <span>门店库存明细</span>
        </div>
      </template>
      <el-table :data="stockList" style="width: 100%" v-loading="loading" :default-sort="{prop: 'STOCK_AMT', order: 'descending'}">
        <el-table-column type="index" label="序号" width="80" align="center" />
        <el-table-column label="门店 ID" width="120" sortable>
          <template #default="{ row }">
            <el-link type="primary" @click.stop="navigateToStockAnalysis(row.SHOPID)" :underline="false">
              {{ row.SHOPID }}
            </el-link>
          </template>
        </el-table-column>
        <el-table-column label="门店名称" min-width="200">
          <template #default="{ row }">
            <el-link type="primary" @click.stop="navigateToStockAnalysis(row.SHOPID)" :underline="false">
              {{ row.ORG_NAME }}
            </el-link>
          </template>
        </el-table-column>
        <el-table-column prop="STOCK_QTY" label="库存数量" width="120" align="right" sortable />
        <el-table-column prop="STOCK_AMT" label="库存金额" width="140" align="right" sortable>
          <template #default="{ row }">
            ¥ {{ formatNumber(row.STOCK_AMT) }}
          </template>
        </el-table-column>
        <el-table-column label="占比" width="120" align="right">
          <template #default="{ row }">
            <el-progress 
              :percentage="calculatePercent(row.STOCK_AMT)" 
              :stroke-width="12"
              :color="getProgressColor(calculatePercent(row.STOCK_AMT))"
            />
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getStockSumQuery } from '@/api/report'
import * as echarts from 'echarts'

const router = useRouter()

// 判断是否为移动端
const isMobile = ref(false)

const checkMobile = () => {
  isMobile.value = window.innerWidth <= 768
}

// 查询表单
const searchForm = reactive({})

const loading = ref(false)
const shopPieChartRef = ref(null)
let shopPieChart = null

// 汇总数据
const summaryData = ref({
  totalAmt: 0,
  totalQty: 0,
  shopCount: 0
})

// 库存列表数据
const stockList = ref([])

// 格式化数字
const formatNumber = (num) => {
  if (!num && num !== 0) return '0.00'
  return Number(num).toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
}

// 计算占比
const calculatePercent = (amount) => {
  if (summaryData.value.totalAmt === 0) return 0
  return Math.round((amount / summaryData.value.totalAmt) * 100)
}

// 获取进度条颜色
const getProgressColor = (percent) => {
  if (percent >= 50) return '#F56C6C'
  if (percent >= 20) return '#E6A23C'
  return '#67C23A'
}

// 查询
const handleSearch = async () => {
  loading.value = true
  try {
    const params = {}
    
    console.log('查询参数:', params)
    
    const res = await getStockSumQuery(params)
    
    // 处理返回数据
    if (res.datas && res.datas.list) {
      stockList.value = res.datas.list
      
      // 设置汇总数据
      if (res.datas.summary) {
        summaryData.value = {
          totalAmt: res.datas.summary.totalAmt || 0,
          totalQty: res.datas.summary.totalQty || 0,
          shopCount: res.datas.summary.shopCount || 0
        }
      }
      
      nextTick(() => {
        renderShopPieChart(stockList.value)
      })
      
      ElMessage.success(`查询成功，共 ${res.datas.total} 家门店`)
    }
  } catch (error) {
    console.error('查询失败:', error)
    ElMessage.error('查询失败：' + error.message)
  } finally {
    loading.value = false
  }
}

// 重置
const handleReset = () => {
  handleSearch()
}

// 跳转到商品库存分析页面
const navigateToStockAnalysis = (shopId) => {
  router.push({
    path: '/stock-analysis',
    query: {
      shopId: shopId
    }
  })
}

// 渲染门店占比饼图
const renderShopPieChart = (data) => {
  if (!shopPieChartRef.value) return
  
  if (shopPieChart) {
    shopPieChart.dispose()
  }
  
  shopPieChart = echarts.init(shopPieChartRef.value)
  
  const colors = ['#409EFF', '#67C23A', '#E6A23C', '#F56C6C', '#909399', '#13C2C2', '#722ED1', '#FA541C', '#1890FF', '#13C2C2']
  
  const pieData = data.map((shop, index) => ({
    name: shop.ORG_NAME || shop.SHOPID,
    value: shop.STOCK_AMT,
    itemStyle: { color: colors[index % colors.length] }
  }))
  
  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{b}<br/>库存金额：¥{c}<br/>占比：{d}%'
    },
    legend: {
      orient: 'vertical',
      left: 'left',
      type: 'scroll',
      textStyle: {
        fontSize: 11
      }
    },
    series: [{
      name: '门店库存',
      type: 'pie',
      radius: ['40%', '70%'],
      center: ['60%', '50%'],
      avoidLabelOverlap: true,
      itemStyle: {
        borderRadius: 6,
        borderColor: '#fff',
        borderWidth: 2
      },
      label: {
        show: true,
        position: 'outside',
        formatter: '{b}: {d}%'
      },
      emphasis: {
        label: {
          show: true,
          fontSize: 12,
          fontWeight: 'bold'
        },
        itemStyle: {
          shadowBlur: 10,
          shadowOffsetX: 0,
          shadowColor: 'rgba(0, 0, 0, 0.3)'
        }
      },
      data: pieData
    }]
  }
  
  shopPieChart.setOption(option)
}

// 窗口大小变化
const handleResize = () => {
  checkMobile()
  if (shopPieChart) {
    shopPieChart.resize()
  }
}

// 返回上一页
const goBack = () => {
  router.push('/')
}

onMounted(() => {
  checkMobile()
  handleSearch()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  if (shopPieChart) {
    shopPieChart.dispose()
  }
})
</script>

<style scoped>
.stock-summary {
  padding: 0;
}

.page-header {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-bottom: 15px;
  padding: 10px 15px;
  background-color: #fff;
  border-radius: 4px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.page-header .el-button {
  padding: 8px 15px;
}

.page-title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.search-card {
  margin-bottom: 15px;
}

.search-form {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.button-group {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.button-group .el-button {
  flex: 1;
  min-width: 80px;
}

.data-cards {
  margin-bottom: 15px;
}

.data-card {
  text-align: center;
  margin-bottom: 10px;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  color: #909399;
  font-size: 14px;
  margin-bottom: 12px;
}

.card-icon {
  font-size: 20px;
}

.card-value {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 10px;
}

.card-footer {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
  font-size: 12px;
}

.trend-label {
  color: #909399;
}

.chart-card {
  margin-bottom: 15px;
}

.card-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.chart-container {
  height: 400px;
  width: 100%;
}

.table-card {
  margin-bottom: 15px;
}

/* 移动端优化 */
@media screen and (max-width: 768px) {
  .search-form {
    flex-direction: column;
    gap: 12px;
  }

  .search-form :deep(.el-form-item) {
    width: 100%;
    margin-right: 0;
  }

  .search-form :deep(.el-form-item__label) {
    font-size: 13px;
    margin-bottom: 4px;
  }

  .button-group {
    width: 100%;
  }

  .button-group .el-button {
    flex: 1;
    min-width: auto;
    padding: 10px 15px;
    font-size: 14px;
  }

  .card-value {
    font-size: 20px;
  }

  .card-header {
    font-size: 13px;
  }

  .chart-container {
    height: 300px;
  }

  .table-card :deep(.el-table) {
    font-size: 12px;
  }

  .table-card :deep(.el-table th) {
    padding: 8px 0;
    font-size: 12px;
  }

  .table-card :deep(.el-table td) {
    padding: 8px 0;
  }
}

/* PC 端优化 */
@media screen and (min-width: 769px) {
  .search-form :deep(.el-form-item) {
    margin-bottom: 0;
  }
}

:deep(.el-card__header) {
  padding: 12px 15px;
  border-bottom: 1px solid #EBEEF5;
}

:deep(.el-card__body) {
  padding: 15px;
}

:deep(.el-row) {
  margin: 0 -5px;
}

:deep(.el-col) {
  padding: 0 5px;
}
</style>
