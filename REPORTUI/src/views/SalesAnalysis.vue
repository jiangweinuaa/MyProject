<template>
  <div class="sales-analysis">
    <!-- 返回按钮和标题 -->
    <div class="page-header">
      <el-button @click="goBack" :icon="ArrowLeft" size="default" v-if="showBackButton">
        返回
      </el-button>
      <span class="page-title">销售分析</span>
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
      <el-col :xs="24" :sm="12" :md="6">
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
      <el-col :xs="24" :sm="12" :md="6">
        <el-card shadow="hover" class="data-card">
          <div class="card-header">
            <el-icon class="card-icon" style="color: #67C23A"><Ticket /></el-icon>
            <span>订单数量</span>
          </div>
          <div class="card-value">{{ formatNumber(summaryData.orderCount) }}</div>
          <div class="card-footer">
            <span class="trend-label">总订单数</span>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card shadow="hover" class="data-card">
          <div class="card-header">
            <el-icon class="card-icon" style="color: #E6A23C"><Shop /></el-icon>
            <span>门店数量</span>
          </div>
          <div class="card-value">{{ formatNumber(summaryData.shopCount) }}</div>
          <div class="card-footer">
            <span class="trend-label">参与门店</span>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card shadow="hover" class="data-card">
          <div class="card-header">
            <el-icon class="card-icon" style="color: #F56C6C"><DataLine /></el-icon>
            <span>客单价</span>
          </div>
          <div class="card-value">¥ {{ formatNumber(summaryData.avgOrderValue) }}</div>
          <div class="card-footer">
            <span class="trend-label">平均每单</span>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 门店销售排行 -->
    <el-card class="table-card" shadow="hover">
      <template #header>
        <div class="card-title">
          <el-icon><Shop /></el-icon>
          <span>门店销售排行</span>
        </div>
      </template>
      <el-table :data="shopRankingList" style="width: 100%" v-loading="loading" :default-sort="{prop: 'totalAmount', order: 'descending'}">
        <el-table-column type="index" label="排名" width="80" align="center" />
        <el-table-column label="门店 ID" width="100">
          <template #default="{ row }">
            <el-link type="primary" @click="navigateToGoodsAnalysis(row.shopId)" :underline="false">
              {{ row.shopId }}
            </el-link>
          </template>
        </el-table-column>
        <el-table-column label="门店名称" min-width="180">
          <template #default="{ row }">
            <el-link type="primary" @click="navigateToGoodsAnalysis(row.shopId)" :underline="false">
              {{ row.shopName }}
            </el-link>
          </template>
        </el-table-column>
        <el-table-column prop="totalAmount" label="销售金额" width="120" align="right" sortable>
          <template #default="{ row }">
            ¥ {{ formatNumber(row.totalAmount) }}
          </template>
        </el-table-column>
        <el-table-column prop="totalOrderCount" label="订单数" width="100" align="right" sortable />
        <el-table-column prop="avgOrderValue" label="客单价" width="100" align="right" sortable>
          <template #default="{ row }">
            ¥ {{ formatNumber(row.avgOrderValue) }}
          </template>
        </el-table-column>
        <el-table-column label="占比" width="100" align="right">
          <template #default="{ row }">
            <el-progress :percentage="calculatePercent(row.totalAmount)" :stroke-width="12" />
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 图表区域 -->
    <el-row :gutter="15">
      <!-- 各门店销售占比饼图 -->
      <el-col :xs="24" :sm="24" :md="12">
        <el-card class="chart-card" shadow="hover">
          <template #header>
            <div class="card-title">
              <el-icon><PieChart /></el-icon>
              <span>各门店销售占比</span>
            </div>
          </template>
          <div class="chart-container" ref="shopPieChartRef" id="shopPieChart"></div>
        </el-card>
      </el-col>
      
      <!-- 按日期汇总柱状图 -->
      <el-col :xs="24" :sm="24" :md="12">
        <el-card class="chart-card" shadow="hover">
          <template #header>
            <div class="card-title">
              <el-icon><Histogram /></el-icon>
              <span>每日销售汇总</span>
            </div>
          </template>
          <div class="chart-container" ref="dateBarChartRef" id="dateBarChart"></div>
        </el-card>
      </el-col>
    </el-row>

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

    <!-- 销售趋势图 -->
    <el-card class="chart-card" shadow="hover">
      <template #header>
        <div class="card-title">
          <el-icon><TrendCharts /></el-icon>
          <span>每日销售趋势</span>
        </div>
      </template>
      <div class="chart-container" ref="trendChartRef" id="trendChart"></div>
    </el-card>

    <!-- 每日销售明细 -->
    <el-card class="table-card" shadow="hover">
      <template #header>
        <div class="card-title">
          <el-icon><Calendar /></el-icon>
          <span>每日销售明细</span>
        </div>
      </template>
      <el-table :data="daySaleList" style="width: 100%" v-loading="loading" :default-sort="{prop: 'saleDate', order: 'descending'}">
        <el-table-column label="日期" width="120" sortable>
          <template #default="{ row }">
            <el-link type="primary" @click="navigateToGoodsAnalysisByDate(row.saleDate, row.shopName, row.shopId)" :underline="false">
              {{ row.saleDate }}
            </el-link>
          </template>
        </el-table-column>
        <el-table-column label="门店" min-width="150">
          <template #default="{ row }">
            <el-link type="primary" @click="navigateToGoodsAnalysis(row.shopId)" :underline="false">
              {{ row.shopName }}
            </el-link>
          </template>
        </el-table-column>
        <el-table-column prop="amount" label="销售金额" width="120" align="right" sortable>
          <template #default="{ row }">
            ¥ {{ formatNumber(row.amount) }}
          </template>
        </el-table-column>
        <el-table-column prop="orderCount" label="订单数" width="100" align="right" sortable />
        <el-table-column prop="avgOrderValue" label="客单价" width="100" align="right">
          <template #default="{ row }">
            ¥ {{ formatNumber(row.avgOrderValue) }}
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted, nextTick } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getDaySaleQuery, getDayChannelQuery } from '@/api/report'
import * as echarts from 'echarts'

const router = useRouter()
const route = useRoute()

// 是否显示返回按钮
const showBackButton = ref(false)

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
  endDate: today
})

const loading = ref(false)
const exporting = ref(false)
const trendChartRef = ref(null)
const shopPieChartRef = ref(null)
const dateBarChartRef = ref(null)
const channelPieChartRef = ref(null)
let trendChart = null
let shopPieChart = null
let dateBarChart = null
let channelPieChart = null

// 渠道数据
const channelData = ref([])

// 汇总数据
const summaryData = ref({
  totalAmount: 0,
  orderCount: 0,
  shopCount: 0,
  avgOrderValue: 0
})

// 门店排行数据
const shopRankingList = ref([])

// 每日销售数据
const daySaleList = ref([])

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

// 格式化日期显示 YYYYMMDD -> YYYY-MM-DD
const formatDateToDisplay = (dateStr) => {
  if (!dateStr || dateStr.length !== 8) return dateStr
  return `${dateStr.substring(0, 4)}-${dateStr.substring(4, 6)}-${dateStr.substring(6, 8)}`
}

// 计算占比
const calculatePercent = (amount) => {
  if (summaryData.value.totalAmount === 0) return 0
  return Math.round((amount / summaryData.value.totalAmount) * 100)
}

// 查询
const handleSearch = async () => {
  loading.value = true
  try {
    const startDate = formatDateToDB(searchForm.startDate)
    const endDate = formatDateToDB(searchForm.endDate)
    
    const params = {
      startDate: startDate,
      endDate: endDate
    }
    
    console.log('查询参数:', params)
    
    // 并行查询销售数据和渠道数据
    const [saleRes, channelRes] = await Promise.all([
      getDaySaleQuery(params),
      getDayChannelQuery(params)
    ])
    
    const res = saleRes
    
    // 处理返回数据
    if (res.datas && res.datas.list) {
      const rawData = res.datas.list
      
      // 设置汇总数据
      if (res.datas.summary) {
        summaryData.value = {
          totalAmount: res.datas.summary.totalAmount || 0,
          orderCount: res.datas.summary.totalOrderCount || 0,
          shopCount: res.datas.summary.shopCount || 0,
          avgOrderValue: res.datas.summary.avgOrderValue || 0
        }
      }
      
      // 处理门店排行数据
      const shopMap = new Map()
      rawData.forEach(item => {
        const shopId = item.SHOPID || '未知'
        const shopName = item.ORG_NAME || '未知门店'
        
        if (!shopMap.has(shopId)) {
          shopMap.set(shopId, {
            shopId: shopId,
            shopName: shopName,
            totalAmount: 0,
            totalOrderCount: 0
          })
        }
        
        const shop = shopMap.get(shopId)
        shop.totalAmount += (item.AMOUNT || 0)
        shop.totalOrderCount += (item.ORDERCOUNT || 0)
      })
      
      // 计算各门店客单价
      shopRankingList.value = Array.from(shopMap.values()).map(shop => ({
        ...shop,
        avgOrderValue: shop.totalOrderCount > 0 ? shop.totalAmount / shop.totalOrderCount : 0
      })).sort((a, b) => b.totalAmount - a.totalAmount)
      
      // 处理每日销售数据（按日期 + 门店汇总）
      const dateShopMap = new Map()
      rawData.forEach(item => {
        const dateKey = item.SALEDATE || ''
        const shopId = item.SHOPID || ''
        const shopName = item.ORG_NAME || '未知门店'
        const compositeKey = `${dateKey}|${shopId}` // 使用日期 + 门店作为唯一 key
        
        if (!dateShopMap.has(compositeKey)) {
          dateShopMap.set(compositeKey, {
            saleDate: formatDateToDisplay(dateKey),
            saleDateRaw: dateKey,
            shopId: shopId,
            shopName: shopName,
            amount: 0,
            orderCount: 0
          })
        }
        
        const data = dateShopMap.get(compositeKey)
        data.amount += (item.AMOUNT || 0)
        data.orderCount += (item.ORDERCOUNT || 0)
      })
      
      daySaleList.value = Array.from(dateShopMap.values()).map(item => ({
        ...item,
        avgOrderValue: item.orderCount > 0 ? item.amount / item.orderCount : 0
      })).sort((a, b) => {
        // 先按日期排序，再按门店排序
        const dateCompare = b.saleDate.localeCompare(a.saleDate)
        if (dateCompare !== 0) return dateCompare
        return a.shopId.localeCompare(b.shopId)
      })
      
      // 渲染图表（按日期汇总，不分门店）
      const chartDataMap = new Map()
      rawData.forEach(item => {
        const dateKey = item.SALEDATE || ''
        if (!chartDataMap.has(dateKey)) {
          chartDataMap.set(dateKey, {
            saleDate: formatDateToDisplay(dateKey),
            amount: 0
          })
        }
        const data = chartDataMap.get(dateKey)
        data.amount += (item.AMOUNT || 0)
      })
      const chartData = Array.from(chartDataMap.values())
        .sort((a, b) => a.saleDate.localeCompare(b.saleDate))
      
      nextTick(() => {
        renderTrendChart(chartData)
        renderShopPieChart(shopRankingList.value)
        renderDateBarChart(chartData)
      })
      
      // 处理渠道数据
      if (channelRes.datas && channelRes.datas.list) {
        const channelRawData = channelRes.datas.list
        const channelMap = new Map()
        
        channelRawData.forEach(item => {
          const channelId = item.CHANNELID || '未知'
          const channelName = item.CHANNEL_NAME || '未知渠道'
          
          if (!channelMap.has(channelId)) {
            channelMap.set(channelId, {
              channelId: channelId,
              channelName: channelName,
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
      }
      
      const channelCount = channelData.value.length
      ElMessage.success(`查询成功，共 ${rawData.length} 条记录，${shopMap.size} 家门店，${channelCount} 个渠道`)
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
  handleSearch()
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

// 渲染销售趋势图（折线图）
const renderTrendChart = (data) => {
  if (!trendChartRef.value) return
  
  if (trendChart) {
    trendChart.dispose()
  }
  
  trendChart = echarts.init(trendChartRef.value)
  
  const dates = data.map(item => item.saleDate)
  const amounts = data.map(item => item.amount)
  
  const option = {
    tooltip: {
      trigger: 'axis',
      formatter: '{b}<br/>销售额：¥{c}'
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      top: '10%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: dates,
      axisLabel: {
        rotate: 45,
        interval: 'auto'
      }
    },
    yAxis: {
      type: 'value',
      name: '销售额 (元)',
      axisLabel: {
        formatter: '¥{value}'
      }
    },
    series: [{
      name: '销售额',
      type: 'line',
      data: amounts,
      smooth: true,
      areaStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: 'rgba(64, 158, 255, 0.5)' },
          { offset: 1, color: 'rgba(64, 158, 255, 0.05)' }
        ])
      },
      itemStyle: {
        color: '#409EFF'
      },
      lineStyle: {
        width: 2
      }
    }]
  }
  
  trendChart.setOption(option)
}

// 渲染门店占比饼图
const renderShopPieChart = (shopData) => {
  if (!shopPieChartRef.value) return
  
  if (shopPieChart) {
    shopPieChart.dispose()
  }
  
  shopPieChart = echarts.init(shopPieChartRef.value)
  
  const colors = ['#409EFF', '#67C23A', '#E6A23C', '#F56C6C', '#909399', '#13C2C2', '#722ED1', '#FA541C', '#1890FF', '#13C2C2']
  
  const pieData = shopData.slice(0, 10).map((shop, index) => ({
    name: shop.shopName || shop.shopId,
    value: shop.totalAmount,
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
      name: '门店销售',
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

// 渲染每日销售柱状图（按日期升序排序）
const renderDateBarChart = (data) => {
  if (!dateBarChartRef.value) return
  
  if (dateBarChart) {
    dateBarChart.dispose()
  }
  
  dateBarChart = echarts.init(dateBarChartRef.value)
  
  // 按日期升序排序
  const sortedData = [...data].sort((a, b) => a.saleDate.localeCompare(b.saleDate))
  const dates = sortedData.map(item => item.saleDate)
  const amounts = sortedData.map(item => item.amount)
  
  // 计算增长率：(当天 - 前一天) / 前一天 * 100
  const growthRates = sortedData.map((item, index) => {
    if (index === 0) return null // 第一天没有增长率
    const prevAmount = sortedData[index - 1].amount
    if (prevAmount === 0) return 0
    return ((item.amount - prevAmount) / prevAmount * 100).toFixed(2)
  })
  
  // 根据增长率设置柱子颜色（涨为红色，跌为绿色）
  const barColors = amounts.map((amount, index) => {
    if (index === 0) return '#67C23A'
    const rate = parseFloat(growthRates[index])
    if (rate > 0) return '#F56C6C' // 增长用红色
    if (rate < 0) return '#67C23A' // 下降用绿色
    return '#909399' // 持平用灰色
  })
  
  const option = {
    tooltip: {
      trigger: 'axis',
      formatter: (params) => {
        const idx = params[0].dataIndex
        const rate = growthRates[idx]
        let rateText = ''
        if (rate === null) {
          rateText = '（首日）'
        } else if (rate > 0) {
          rateText = `（+${rate}% 📈）`
        } else if (rate < 0) {
          rateText = `（${rate}% 📉）`
        } else {
          rateText = `（持平）`
        }
        return `${params[0].name}<br/>销售额：¥${params[0].value}<br/>较前日：${rateText}`
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      top: '15%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: dates,
      axisLabel: {
        rotate: 45,
        interval: 'auto',
        fontSize: 10
      }
    },
    yAxis: {
      type: 'value',
      name: '销售额 (元)',
      axisLabel: {
        formatter: '¥{value}'
      }
    },
    series: [{
      name: '销售额',
      type: 'bar',
      data: amounts.map((amount, idx) => ({
        value: amount,
        itemStyle: { color: barColors[idx] }
      })),
      barWidth: '60%',
      itemStyle: {
        borderRadius: [4, 4, 0, 0]
      },
      // 在柱子顶部显示增长率标签
      label: {
        show: true,
        position: 'top',
        fontSize: 10,
        fontWeight: 'bold',
        formatter: (params) => {
          const idx = params.dataIndex
          const rate = growthRates[idx]
          if (rate === null) return ''
          if (rate > 0) return `+${rate}%`
          if (rate < 0) return `${rate}%`
          return '持平'
        },
        color: '#606266'
      }
    }]
  }
  
  dateBarChart.setOption(option)
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

// 窗口大小变化时重新渲染图表
const handleResize = () => {
  if (trendChart) {
    trendChart.resize()
  }
  if (shopPieChart) {
    shopPieChart.resize()
  }
  if (dateBarChart) {
    dateBarChart.resize()
  }
  if (channelPieChart) {
    channelPieChart.resize()
  }
}

// 跳转到商品分析页面（按门店）
const navigateToGoodsAnalysis = (shopId) => {
  router.push({
    path: '/goods-analysis',
    query: {
      shopId: shopId,
      startDate: searchForm.startDate,
      endDate: searchForm.endDate
    }
  })
}

// 跳转到商品分析页面（按日期）
const navigateToGoodsAnalysisByDate = (saleDate, shopName, shopId) => {
  console.log('跳转参数:', { saleDate, shopName, shopId })
  router.push({
    path: '/goods-analysis',
    query: {
      shopId: shopId || '', // 传递门店 ID
      startDate: saleDate, // 直接使用 YYYY-MM-DD 格式
      endDate: saleDate,   // 同一天
      shopName: shopName || '' // 传递门店名称用于显示
    }
  })
}

// 返回上一页
const goBack = () => {
  router.push('/')
}

onMounted(() => {
  checkMobile()
  // 如果是从其他页面带参数跳转过来的，显示返回按钮
  showBackButton.value = !!(route.query.from || route.query.backUrl)
  handleSearch()
  window.addEventListener('resize', handleResize)
  window.addEventListener('resize', checkMobile)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  window.removeEventListener('resize', checkMobile)
  if (trendChart) {
    trendChart.dispose()
  }
  if (shopPieChart) {
    shopPieChart.dispose()
  }
  if (dateBarChart) {
    dateBarChart.dispose()
  }
  if (channelPieChart) {
    channelPieChart.dispose()
  }
})
</script>

<style scoped>
.sales-analysis {
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

.trend {
  display: flex;
  align-items: center;
  gap: 2px;
}

.trend.up {
  color: #67C23A;
}

.trend.down {
  color: #F56C6C;
}

.trend-label {
  color: #909399;
}

.chart-card, .table-card {
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
  height: 300px;
  width: 100%;
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
    height: 250px;
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
