<template>
  <div class="goods-analysis">
    <!-- 返回按钮和标题 -->
    <div class="page-header">
      <el-button @click="goBack" :icon="ArrowLeft" size="default">
        返回
      </el-button>
      <span class="page-title">商品销售分析</span>
    </div>

    <!-- 查询条件 -->
    <el-card class="search-card" shadow="hover">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="开始日期">
          <el-date-picker
            v-model="searchForm.startDate"
            type="date"
            placeholder="选择开始日期"
            value-format="YYYY-MM-DD"
            :style="isMobile ? 'width: 100%' : 'width: 160px'"
          />
        </el-form-item>
        <el-form-item label="截止日期">
          <el-date-picker
            v-model="searchForm.endDate"
            type="date"
            placeholder="选择截止日期"
            value-format="YYYY-MM-DD"
            :style="isMobile ? 'width: 100%' : 'width: 160px'"
          />
        </el-form-item>
        <el-form-item label="门店">
          <el-input
            v-model="searchForm.shopId"
            placeholder="门店 ID"
            :style="isMobile ? 'width: 100%' : 'width: 150px'"
            clearable
          />
        </el-form-item>
        <el-form-item class="button-group">
          <el-button type="primary" @click="handleSearch" :loading="loading" :icon="Search">
            查询
          </el-button>
          <el-button @click="handleReset" :icon="Refresh">
            重置
          </el-button>
          <el-button type="success" @click="handleExport" :loading="exporting" :icon="Download">
            导出
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
            <span>销售总额</span>
          </div>
          <div class="card-value">¥ {{ formatNumber(summaryData.totalAmount) }}</div>
          <div class="card-footer">
            <span class="trend-label">总销售额</span>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="8">
        <el-card shadow="hover" class="data-card">
          <div class="card-header">
            <el-icon class="card-icon" style="color: #67C23A"><Goods /></el-icon>
            <span>销售数量</span>
          </div>
          <div class="card-value">{{ formatNumber(summaryData.totalQty) }}</div>
          <div class="card-footer">
            <span class="trend-label">总销售件数</span>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="8">
        <el-card shadow="hover" class="data-card">
          <div class="card-header">
            <el-icon class="card-icon" style="color: #E6A23C"><List /></el-icon>
            <span>商品种类</span>
          </div>
          <div class="card-value">{{ formatNumber(summaryData.goodsCount) }}</div>
          <div class="card-footer">
            <span class="trend-label">销售商品数</span>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 商品信息提示 -->
    <el-alert
      title="商品销售明细"
      type="info"
      :closable="false"
      show-icon
      class="info-alert"
    >
      <template #default>
        <span v-if="searchForm.shopId">
          门店：<strong>{{ searchForm.shopId }}</strong> | 
          日期：<strong>{{ searchForm.startDate }}</strong> 至 <strong>{{ searchForm.endDate }}</strong>
        </span>
        <span v-else>
          日期：<strong>{{ searchForm.startDate }}</strong> 至 <strong>{{ searchForm.endDate }}</strong>
          <el-tag size="small" type="warning" style="margin-left: 10px">全部门店</el-tag>
        </span>
      </template>
    </el-alert>

    <!-- 渠道销售占比饼图 -->
    <el-card class="chart-card" shadow="hover">
      <template #header>
        <div class="card-title">
          <el-icon><PieChart /></el-icon>
          <span>各渠道销售占比</span>
        </div>
      </template>
      <div class="chart-container" ref="channelPieChartRef" id="channelPieChart"></div>
    </el-card>

    <!-- 商品销售明细表 -->
    <el-card class="table-card" shadow="hover">
      <template #header>
        <div class="card-title">
          <el-icon><Goods /></el-icon>
          <span>商品销售明细</span>
        </div>
      </template>
      <el-table :data="goodsList" style="width: 100%" v-loading="loading" :default-sort="{prop: 'amount', order: 'descending'}">
        <el-table-column type="index" label="序号" width="80" align="center" />
        <el-table-column prop="PLUNO" label="商品编码" width="150" sortable />
        <el-table-column prop="PLU_NAME" label="商品名称" min-width="200" />
        <el-table-column prop="AMOUNT" label="销售金额" width="120" align="right" sortable>
          <template #default="{ row }">
            ¥ {{ formatNumber(row.AMOUNT) }}
          </template>
        </el-table-column>
        <el-table-column prop="QTY" label="销售数量" width="100" align="right" sortable />
        <el-table-column label="单价" width="100" align="right">
          <template #default="{ row }">
            ¥ {{ formatNumber(row.QTY > 0 ? row.AMOUNT / row.QTY : 0) }}
          </template>
        </el-table-column>
        <el-table-column label="占比" width="120" align="right">
          <template #default="{ row }">
            <el-progress 
              :percentage="calculatePercent(row.AMOUNT)" 
              :stroke-width="12"
              :color="getProgressColor(calculatePercent(row.AMOUNT))"
            />
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getDayShopGoodsQuery, getDayShopChannelQuery } from '@/api/report'
import * as echarts from 'echarts'

const route = useRoute()
const router = useRouter()

// 判断是否为移动端
const isMobile = ref(false)

const checkMobile = () => {
  isMobile.value = window.innerWidth <= 768
}

// 查询表单 - 默认本月第一天到今天
const today = new Date().toISOString().split('T')[0]
const firstDayOfMonth = new Date()
firstDayOfMonth.setDate(1)
const firstDayStr = firstDayOfMonth.toISOString().split('T')[0]

const searchForm = reactive({
  startDate: firstDayStr,
  endDate: today,
  shopId: ''
})

const loading = ref(false)
const exporting = ref(false)
const channelPieChartRef = ref(null)
let channelPieChart = null

// 汇总数据
const summaryData = ref({
  totalAmount: 0,
  totalQty: 0,
  goodsCount: 0
})

// 商品列表数据
const goodsList = ref([])

// 渠道数据
const channelData = ref([])

// 格式化数字
const formatNumber = (num) => {
  if (!num && num !== 0) return '0.00'
  return Number(num).toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
}

// 格式化日期 YYYY-MM-DD -> YYYYMMDD
const formatDateToDB = (dateStr) => {
  if (!dateStr) return ''
  return dateStr.replace(/-/g, '')
}

// 计算占比
const calculatePercent = (amount) => {
  if (summaryData.value.totalAmount === 0) return 0
  return Math.round((amount / summaryData.value.totalAmount) * 100)
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
    const startDate = formatDateToDB(searchForm.startDate)
    const endDate = formatDateToDB(searchForm.endDate)
    
    const params = {
      startDate: startDate,
      endDate: endDate,
      shopId: searchForm.shopId ? searchForm.shopId.trim() : ''
    }
    
    console.log('查询参数:', params)
    
    // 并行查询商品数据和渠道数据（按门店过滤）
    const [goodsRes, channelRes] = await Promise.all([
      getDayShopGoodsQuery(params),
      getDayShopChannelQuery(params)
    ])
    
    // 处理商品数据
    if (goodsRes.datas && goodsRes.datas.list) {
      goodsList.value = goodsRes.datas.list
      
      // 设置汇总数据
      if (goodsRes.datas.summary) {
        summaryData.value = {
          totalAmount: goodsRes.datas.summary.totalAmount || 0,
          totalQty: goodsRes.datas.summary.totalQty || 0,
          goodsCount: goodsRes.datas.summary.goodsCount || 0
        }
      }
      
      ElMessage.success(`查询成功，共 ${goodsRes.datas.total} 种商品`)
    }
    
    // 处理渠道数据
    if (channelRes.datas && channelRes.datas.list) {
      const channelRawData = channelRes.datas.list
      const channelMap = new Map()
      
      channelRawData.forEach(item => {
        const channelId = item.CHANNELID || '未知'
        
        if (!channelMap.has(channelId)) {
          channelMap.set(channelId, {
            channelId: channelId,
            channelName: channelId,
            totalAmount: 0,
            totalOrderCount: 0
          })
        }
        
        const channel = channelMap.get(channelId)
        channel.totalAmount += (item.AMOUNT || 0)
        channel.totalOrderCount += (item.ORDERCOUNT || 0)
      })
      
      channelData.value = Array.from(channelMap.values()).map(channel => ({
        ...channel,
        avgOrderValue: channel.totalOrderCount > 0 ? channel.totalAmount / channel.totalOrderCount : 0
      })).sort((a, b) => b.totalAmount - a.totalAmount)
      
      nextTick(() => {
        renderChannelPieChart(channelData.value)
      })
      
      ElMessage.success(`查询成功，共 ${goodsRes.datas.total} 种商品，${channelData.value.length} 个渠道`)
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
  searchForm.startDate = firstDayStr
  searchForm.endDate = today
  searchForm.shopId = ''
  // 清除 URL 参数
  if (route.query.shopId || route.query.startDate || route.query.endDate) {
    window.history.replaceState({}, '', window.location.pathname)
  }
}

// 导出
const handleExport = async () => {
  exporting.value = true
  try {
    await new Promise(resolve => setTimeout(resolve, 1000))
    ElMessage.success('导出成功')
  } finally {
    exporting.value = false
  }
}

// 从 URL 参数加载
const loadFromUrlParams = () => {
  const shopId = route.query.shopId
  const startDate = route.query.startDate
  const endDate = route.query.endDate
  
  if (shopId) {
    searchForm.shopId = shopId
  }
  if (startDate) {
    searchForm.startDate = startDate
  }
  if (endDate) {
    searchForm.endDate = endDate
  }
  
  // 如果有门店 ID 或日期参数，自动查询
  if (shopId || (startDate && endDate)) {
    handleSearch()
  }
}

// 返回上一页
const goBack = () => {
  // 如果有来源页面，返回来源页面
  if (route.query.shopId) {
    // 如果是从销售分析跳转过来的，带参数返回
    router.push({
      path: '/sales-analysis',
      query: {
        startDate: searchForm.startDate,
        endDate: searchForm.endDate
      }
    })
  } else {
    // 否则返回商品分析首页
    router.push('/goods-analysis')
  }
}

// 渲染渠道占比饼图
const renderChannelPieChart = (data) => {
  if (!channelPieChartRef.value) return
  
  if (channelPieChart) {
    channelPieChart.dispose()
  }
  
  channelPieChart = echarts.init(channelPieChartRef.value)
  
  const colors = ['#409EFF', '#67C23A', '#E6A23C', '#F56C6C', '#909399', '#13C2C2', '#722ED1', '#FA541C', '#1890FF', '#13C2C2']
  
  const pieData = data.map((channel, index) => ({
    name: channel.channelName || channel.channelId,
    value: channel.totalAmount,
    itemStyle: { color: colors[index % colors.length] }
  }))
  
  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{b}<br/>销售额：¥{c}<br/>占比：{d}%'
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
      name: '渠道销售',
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
  
  channelPieChart.setOption(option)
}

// 窗口大小变化
const handleResize = () => {
  checkMobile()
  if (channelPieChart) {
    channelPieChart.resize()
  }
}

onMounted(() => {
  checkMobile()
  loadFromUrlParams()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  if (channelPieChart) {
    channelPieChart.dispose()
  }
})
</script>

<style scoped>
.goods-analysis {
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

.info-alert {
  margin-bottom: 15px;
}

.chart-card {
  margin-bottom: 15px;
}

.chart-container {
  height: 350px;
  width: 100%;
}

.table-card {
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
    height: 280px;
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
