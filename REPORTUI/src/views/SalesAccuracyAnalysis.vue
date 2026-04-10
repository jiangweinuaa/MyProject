<template>
  <div class="sales-accuracy-analysis">
    <!-- 返回按钮和标题 -->
    <div class="page-header">
      <el-button @click="goBack" :icon="ArrowLeft" size="default" v-if="showBackButton">
        返回
      </el-button>
      <span class="page-title">📊 销售预估准确性分析</span>
    </div>

    <!-- 查询条件 -->
    <el-card class="search-card" shadow="hover">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="门店 ID">
          <el-input
            v-model="searchForm.shopId"
            placeholder="请输入门店 ID（可选）"
            clearable
            :style="isMobile ? 'width: 100%' : 'width: 200px'"
          />
        </el-form-item>
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
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 核心指标卡片 -->
    <el-row :gutter="15" class="metrics-row">
      <el-col :xs="24" :sm="12" :md="6">
        <el-card shadow="hover" class="metric-card">
          <div class="metric-icon" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%)">
            <el-icon><DataLine /></el-icon>
          </div>
          <div class="metric-info">
            <div class="metric-label">综合准确率</div>
            <div class="metric-value">{{ metrics.accuracyRate }}%</div>
            <div class="metric-desc">100% - MAPE</div>
          </div>
        </el-card>
      </el-col>

      <el-col :xs="24" :sm="12" :md="6">
        <el-card shadow="hover" class="metric-card">
          <div class="metric-icon" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%)">
            <el-icon><TrendCharts /></el-icon>
          </div>
          <div class="metric-info">
            <div class="metric-label">销售额 MAPE</div>
            <div class="metric-value">{{ metrics.salesMAPE }}%</div>
            <div class="metric-desc">平均绝对百分比误差</div>
          </div>
        </el-card>
      </el-col>

      <el-col :xs="24" :sm="12" :md="6">
        <el-card shadow="hover" class="metric-card">
          <div class="metric-icon" style="background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)">
            <el-icon><Money /></el-icon>
          </div>
          <div class="metric-info">
            <div class="metric-label">销售额 MAE</div>
            <div class="metric-value">¥{{ formatNumber(bias.salesMAE) }}</div>
            <div class="metric-desc">平均绝对误差</div>
          </div>
        </el-card>
      </el-col>

      <el-col :xs="24" :sm="12" :md="6">
        <el-card shadow="hover" class="metric-card">
          <div class="metric-icon" style="background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)">
            <el-icon><CircleCheck /></el-icon>
          </div>
          <div class="metric-info">
            <div class="metric-label">有效分析天数</div>
            <div class="metric-value">{{ metrics.validDays }}</div>
            <div class="metric-desc">剔除 {{ metrics.abnormalDays }} 天异常</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 偏差分析 -->
    <el-row :gutter="15" class="bias-row">
      <el-col :xs="24" :md="12">
        <el-card shadow="hover">
          <template #header>
            <span>📈 销售额偏差分析</span>
          </template>
          <el-descriptions :column="1" border>
            <el-descriptions-item label="销售额偏差 (Bias)">
              <el-tag :type="bias.salesBias > 0 ? 'warning' : 'success'">
                {{ bias.salesBias > 0 ? '+' : '' }}¥{{ formatNumber(bias.salesBias) }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="偏差方向">
              <el-tag :type="bias.biasDirection === '高估' ? 'danger' : 'success'">
                {{ bias.biasDirection }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="销售额总偏差率">
              {{ bias.totalBiasRate }}%
            </el-descriptions-item>
            <el-descriptions-item label="预估总销售额">
              ¥{{ formatNumber(bias.totalForecastSales) }}
            </el-descriptions-item>
            <el-descriptions-item label="实际总销售额">
              ¥{{ formatNumber(bias.totalActualSales) }}
            </el-descriptions-item>
            <el-descriptions-item label="差异">
              <el-tag :type="bias.difference > 0 ? 'warning' : 'success'">
                {{ bias.difference > 0 ? '+' : '' }}¥{{ formatNumber(Math.abs(bias.difference)) }}
              </el-tag>
            </el-descriptions-item>
          </el-descriptions>
        </el-card>
      </el-col>

      <el-col :xs="24" :md="12">
        <el-card shadow="hover">
          <template #header>
            <span>💰 误差指标</span>
          </template>
          <el-descriptions :column="1" border>
            <el-descriptions-item label="销售额 MAE">
              ¥{{ formatNumber(bias.salesMAE) }}
            </el-descriptions-item>
            <el-descriptions-item label="销售额 RMSE">
              ¥{{ formatNumber(metrics.salesRMSE) }}
            </el-descriptions-item>
            <el-descriptions-item label="销售额 MAPE">
              {{ metrics.salesMAPE }}%
            </el-descriptions-item>
            <el-descriptions-item label="综合准确率">
              <el-tag :type="getAccuracyType(metrics.accuracyRate)">
                {{ metrics.accuracyRate }}%
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="综合评级">
              <div style="display: flex; align-items: center; gap: 8px">
                <div style="display: flex; flex-direction: column; gap: 5px; flex: 1">
                  <el-rate v-model="rating.stars" disabled :colors="['#F56C6C', '#E6A23C', '#67C23A']" />
                  <span style="font-size: 13px; color: #606266">{{ rating.text }}</span>
                </div>
                <el-tooltip placement="top" :offset="10">
                  <template #content>
                    <div style="font-size: 12px; line-height: 1.6">
                      <div style="font-weight: bold; margin-bottom: 6px">⭐ 评级标准</div>
                      <div><span style="color: #67C23A">●</span> ≥90%：优秀 ★★★★★</div>
                      <div><span style="color: #67C23A">●</span> ≥80%：良好 ★★★★☆</div>
                      <div><span style="color: #E6A23C">●</span> ≥70%：一般 ★★★☆☆</div>
                      <div><span style="color: #E6A23C">●</span> ≥60%：较差 ★★☆☆☆</div>
                      <div><span style="color: #F56C6C">●</span> <60%：差 ★☆☆☆☆</div>
                    </div>
                  </template>
                  <el-icon style="cursor: pointer; color: #909399; font-size: 16px" @click.stop><QuestionFilled /></el-icon>
                </el-tooltip>
              </div>
            </el-descriptions-item>
          </el-descriptions>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="15" class="charts-row">
      <el-col :xs="24" :md="12">
        <el-card shadow="hover">
          <template #header>
            <span>📅 每日销售额预估 vs 实际对比</span>
          </template>
          <div ref="dailyChartRef" class="chart-container" style="height: 400px"></div>
        </el-card>
      </el-col>

      <el-col :xs="24" :md="12">
        <el-card shadow="hover">
          <template #header>
            <span>📊 误差分布分析</span>
          </template>
          <div ref="errorDistChartRef" class="chart-container" style="height: 400px"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 按日期维度分析 -->
    <el-row :gutter="15" class="date-row">
      <el-col :xs="24">
        <el-card shadow="hover">
          <template #header>
            <span>📅 按日期维度的预估准确性分析</span>
          </template>
          <el-table :data="dateData" style="width: 100%" :default-sort="{prop: 'date', order: 'ascending'}" show-summary :summary-method="summaryMethod">
            <el-table-column prop="date" label="日期" width="120" sortable />
            <el-table-column prop="weekday" label="星期" width="80" />
            <el-table-column prop="forecastSales" label="预估销售额 (元)" align="center" sortable>
              <template #default="{ row }">
                <span :style="row.abnormal ? 'color: #F56C6C; font-weight: bold;' : ''">
                  {{ formatNumber(row.forecastSales) }}
                </span>
              </template>
            </el-table-column>
            <el-table-column prop="actualSales" label="实际销售额 (元)" align="center" sortable>
              <template #default="{ row }">
                {{ formatNumber(row.actualSales) }}
              </template>
            </el-table-column>
            <el-table-column prop="errorSales" label="误差销售额 (元)" align="center" sortable>
              <template #default="{ row }">
                <el-tag :type="row.errorSales > 0 ? 'warning' : (row.errorSales < 0 ? 'success' : 'info')" size="small">
                  {{ row.errorSales > 0 ? '+' : '' }}{{ formatNumber(row.errorSales) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="errorRate" label="误差率 (%)" align="center" sortable>
              <template #default="{ row }">
                <el-tag :type="Math.abs(row.errorRate) < 10 ? 'success' : (Math.abs(row.errorRate) < 20 ? 'primary' : (Math.abs(row.errorRate) < 30 ? 'warning' : 'danger'))" size="small">
                  {{ row.errorRate > 0 ? '+' : '' }}{{ row.errorRate }}%
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="mape" label="MAPE (%)" align="center" sortable>
              <template #default="{ row }">
                <el-tag :type="row.mape < 10 ? 'success' : (row.mape < 20 ? 'primary' : (row.mape < 30 ? 'warning' : 'danger'))" size="small">
                  {{ row.mape }}%
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="accuracy" label="准确率 (%)" align="center" sortable>
              <template #default="{ row }">
                <el-tag :type="getAccuracyType(row.accuracy)" size="small">
                  {{ row.accuracy }}%
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <!-- 按星期维度分析 -->
    <el-row :gutter="15" class="weekday-row">
      <el-col :xs="24">
        <el-card shadow="hover">
          <template #header>
            <span>📆 按星期维度的预估准确性分析</span>
          </template>
          <el-table :data="weekdayData" style="width: 100%" :default-sort="{prop: 'weekdayIndex', order: 'ascending'}">
            <el-table-column prop="weekday" label="星期" width="100" />
            <el-table-column prop="weekdayIndex" label="" width="0" :sortable="true" :show-overflow-tooltip="true" v-if="false" />
            <el-table-column prop="days" label="天数" width="80" align="center" />
            <el-table-column prop="forecastAvg" label="预估均值 (元)" align="center" />
            <el-table-column prop="actualAvg" label="实际均值 (元)" align="center" />
            <el-table-column prop="mae" label="MAE" align="center" />
            <el-table-column prop="rmse" label="RMSE" align="center" />
            <el-table-column prop="bias" label="Bias" align="center">
              <template #default="{ row }">
                <el-tag :type="row.bias > 0 ? 'warning' : 'success'" size="small">
                  {{ row.bias > 0 ? '+' : '' }}{{ row.bias }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="accuracy" label="准确率" align="center">
              <template #default="{ row }">
                <el-tag :type="getAccuracyType(row.accuracy)" size="small">
                  {{ row.accuracy }}%
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <!-- 异常说明 -->
    <el-row :gutter="15" class="abnormal-row">
      <el-col :xs="24">
        <el-card shadow="hover">
          <template #header>
            <span>⚠️ 异常说明</span>
          </template>
          <el-alert
            title="预估算法异常"
            type="warning"
            :closable="false"
            show-icon
          >
            <template #default>
              <p>以下日期预估值为负数，属预估算法异常，已单独标注：</p>
              <ul>
                <li v-for="abnormal in abnormalDays" :key="abnormal.date">
                  {{ abnormal.date }}（{{ abnormal.value }}）
                </li>
              </ul>
            </template>
          </el-alert>
        </el-card>
      </el-col>
    </el-row>

    <!-- 未来预估参考 -->
    <el-row :gutter="15" class="forecast-row">
      <el-col :xs="24">
        <el-card shadow="hover">
          <template #header>
            <span>🔮 未来预估参考（2026 年 4 月 1 日 -5 日）</span>
          </template>
          <el-table :data="futureForecast" style="width: 100%">
            <el-table-column prop="date" label="日期" width="150" />
            <el-table-column prop="forecastSales" label="预估销售额 (元)" align="center" />
            <el-table-column prop="forecastRevenue" label="预估销售额 (元)" align="center" />
            <el-table-column prop="historicalAvg" label="历史同星期实际均值" align="center" />
            <el-table-column prop="biasCorrection" label="参考偏差修正" align="center">
              <template #default="{ row }">
                <el-tag :type="row.biasCorrection > 0 ? 'warning' : 'success'" size="small">
                  {{ row.biasCorrection > 0 ? '+' : '' }}{{ row.biasCorrection }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <!-- 综合评级 -->
    <el-row :gutter="15" class="rating-row">
      <el-col :xs="24">
        <el-card shadow="hover" class="rating-card">
          <div class="rating-content">
            <div class="rating-title">🏆 综合评级</div>
            <div class="rating-stars">
              <el-rate v-model="rating.stars" disabled :colors="['#99A9BF', '#F7BA2A', '#67C23A']" />
            </div>
            <div class="rating-text">{{ rating.text }}</div>
            <div class="rating-desc">{{ rating.description }}</div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { DataLine, TrendCharts, Money, CircleCheck, ArrowUp, ArrowDown, ArrowLeft, Search, Refresh, QuestionFilled } from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import { queryAccuracyAnalysisFull } from '@/api/shopSaleForecast'

const router = useRouter()

// 是否显示返回按钮
const showBackButton = true

// 是否移动端
const isMobile = computed(() => {
  return window.innerWidth < 768
})

// 获取日期范围：上个月第一天到这个月最后一天
const getDateRange = () => {
  const now = new Date()
  const currentYear = now.getFullYear()
  const currentMonth = now.getMonth() // 0-11，当前月份索引
  
  // 上个月第一天：当前月份 -1，日期为 1
  const lastMonthIndex = currentMonth - 1
  const startDateObj = new Date(currentYear, lastMonthIndex, 1)
  // 使用本地时间格式化，避免 UTC 转换问题
  const startDate = `${startDateObj.getFullYear()}-${String(startDateObj.getMonth() + 1).padStart(2, '0')}-${String(startDateObj.getDate()).padStart(2, '0')}`
  
  // 这个月最后一天：当前月份 +1，日期为 0（即上个月最后一天）
  const nextMonthIndex = currentMonth + 1
  const endDateObj = new Date(currentYear, nextMonthIndex, 0)
  const endDate = `${endDateObj.getFullYear()}-${String(endDateObj.getMonth() + 1).padStart(2, '0')}-${String(endDateObj.getDate()).padStart(2, '0')}`
  
  console.log('日期范围计算:', {
    today: now.toISOString().split('T')[0],
    currentMonth: currentMonth + 1, // 显示为 1-12
    startDate,
    endDate
  })
  
  return { startDate, endDate }
}

// 查询表单
const dateRange = getDateRange()
const searchForm = reactive({
  shopId: '',
  startDate: dateRange.startDate,
  endDate: dateRange.endDate
})

// 加载状态
const loading = ref(false)

// 门店信息
const storeInfo = reactive({
  storeCode: localStorage.getItem('shopid') || '120021',
  storeName: '测试门店'
})

// 偏差分析
const bias = reactive({
  salesBias: 0,
  biasDirection: '-',
  totalBiasRate: 0,
  totalForecastSales: 0,
  totalActualSales: 0,
  difference: 0,
  totalForecastRevenue: 0,
  totalActualRevenue: 0,
  revenueDifference: 0,
  revenueMAE: 0,
  salesMAE: 0
})

// 核心指标（包含 RMSE）
const metrics = reactive({
  accuracyRate: 0,
  accuracyTrend: 'down',
  salesMAPE: 0,
  revenueMAPE: 0,
  validDays: 0,
  abnormalDays: 0,
  salesRMSE: 0
})

// 图表引用
const dailyChartRef = ref(null)
const errorDistChartRef = ref(null)

// 日期数据
const dateData = ref([])

// 星期数据
const weekdayData = ref([])

// 异常天数
const abnormalDays = ref([])

// 未来预估
const futureForecast = ref([])

// 逐日数据
const dailyData = ref([])

// 综合评级
const rating = reactive({
  stars: 3,
  text: '一般 ★★★☆☆',
  description: '-'
})

// 获取准确率类型
const getAccuracyType = (accuracy) => {
  if (accuracy >= 90) return 'success'
  if (accuracy >= 80) return 'primary'
  if (accuracy >= 70) return 'warning'
  return 'danger'
}

// 格式化数字
const formatNumber = (num) => {
  if (num === null || num === undefined) return '0'
  return Number(num).toLocaleString('zh-CN', {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2
  })
}

// 表格汇总方法
const summaryMethod = (param) => {
  const { columns, data } = param
  const sums = []
  
  columns.forEach((column, index) => {
    if (index === 0) {
      sums[index] = '汇总'
      return
    }
    
    const values = data.map(item => Number(item[column.property]))
    if (values.every(value => !isNaN(value))) {
      if (column.property === 'mape' || column.property === 'accuracy' || column.property === 'errorRate') {
        // MAPE、准确率和误差率计算平均值，添加%号
        const avg = values.reduce((prev, curr) => prev + curr, 0) / values.length
        sums[index] = avg.toFixed(1) + '%'
      } else if (column.property === 'errorSales') {
        // 误差销售额计算总和
        const sum = values.reduce((prev, curr) => prev + curr, 0)
        sums[index] = formatNumber(sum)
      } else {
        // 其他数值字段计算总和
        const sum = values.reduce((prev, curr) => prev + curr, 0)
        sums[index] = formatNumber(sum)
      }
    } else {
      sums[index] = ''
    }
  })
  
  return sums
}

// 返回上一页
const goBack = () => {
  router.back()
}

// 查询
const handleSearch = async () => {
  loading.value = true
  try {
    const params = {
      shopId: searchForm.shopId || '',
      startDate: searchForm.startDate,
      endDate: searchForm.endDate
    }
    await loadAllData(params)
  } catch (error) {
    console.error('查询失败:', error)
  } finally {
    loading.value = false
  }
}

// 重置
const handleReset = () => {
  const dateRange = getDateRange()
  searchForm.shopId = ''
  searchForm.startDate = dateRange.startDate
  searchForm.endDate = dateRange.endDate
  handleSearch()
}

// 加载完整数据
const loadAllData = async (params = {}) => {
  try {
    const queryParams = {
      shopId: localStorage.getItem('shopid') || '120021',
      startDate: searchForm.startDate,
      endDate: searchForm.endDate,
      ...params
    }
    
    const response = await queryAccuracyAnalysisFull(queryParams)
    if (response.success && response.datas) {
      const data = response.datas
      
      // 更新指标
      if (data.metrics) {
        const m = data.metrics
        metrics.accuracyRate = m.accuracyRate || 0
        metrics.salesMAPE = m.salesMAPE || 0
        metrics.salesRMSE = m.salesRMSE || 0
        metrics.validDays = m.validDays || 0
        metrics.abnormalDays = m.abnormalDays || 0
      }
      
      // 更新偏差
      if (data.bias) {
        const b = data.bias
        bias.salesBias = b.salesBias || 0
        bias.biasDirection = b.biasDirection || '-'
        bias.totalBiasRate = b.totalBiasRate || 0
        bias.totalForecastSales = b.totalForecastSales || 0
        bias.totalActualSales = b.totalActualSales || 0
        bias.difference = b.difference || 0
        bias.salesMAE = b.salesMAE || 0
      }
      
      // 更新按日期维度数据
      if (data.dateAnalysis) {
        console.log('日期维度数据:', data.dateAnalysis)
        dateData.value = data.dateAnalysis
      } else {
        console.log('未找到日期维度数据，完整返回:', data)
      }
      
      // 更新星期数据
      if (data.weekdayData) {
        weekdayData.value = data.weekdayData
      }
      
      // 更新误差分布
      if (data.errorDist) {
        initErrorDistChart(data.errorDist)
      }
      
      // 更新逐日数据
      if (data.dailyData) {
        dailyData.value = data.dailyData.filter(d => !d.abnormal)
        if (dailyChartRef.value) {
          initDailyChart()
        }
      }
      
      // 更新异常天数
      if (data.abnormalDays) {
        abnormalDays.value = data.abnormalDays
      }
      
      // 更新评级
      if (data.rating) {
        rating.stars = data.rating.stars || 3
        rating.text = data.rating.text || '-'
        rating.description = data.rating.description || '-'
        console.log('评级数据:', rating)
      }
    }
  } catch (error) {
    console.error('加载数据失败:', error)
  }
}

// 初始化逐日对比图
const initDailyChart = () => {
  if (!dailyChartRef.value || dailyData.value.length === 0) return
  
  const dailyChart = echarts.init(dailyChartRef.value)
  dailyChart.setOption({
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'cross' }
    },
    legend: {
      data: ['预估销售额', '实际销售额', '准确率'],
      top: '5%',
      type: 'scroll'
    },
    grid: {
      left: '3%',
      right: '4%',
      top: '15%',
      bottom: '12%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: dailyData.value.map(d => d.date.slice(5)),
      axisLabel: { 
        rotate: 45,
        interval: 0
      }
    },
    yAxis: [
      {
        type: 'value',
        name: '销售额 (元)',
        position: 'left',
        axisLabel: {
          formatter: '{value}'
        }
      },
      {
        type: 'value',
        name: '准确率 (%)',
        min: 0,
        max: 100,
        position: 'right'
      }
    ],
    series: [
      {
        name: '预估销售额',
        type: 'line',
        smooth: true,
        data: dailyData.value.map(d => d.forecast),
        itemStyle: { color: '#91CC75' },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(145,204,117,0.3)' },
            { offset: 1, color: 'rgba(145,204,117,0.01)' }
          ])
        }
      },
      {
        name: '实际销售额',
        type: 'line',
        smooth: true,
        data: dailyData.value.map(d => d.actual),
        itemStyle: { color: '#5470C6' },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(84,112,198,0.3)' },
            { offset: 1, color: 'rgba(84,112,198,0.01)' }
          ])
        }
      },
      {
        name: '准确率',
        type: 'line',
        yAxisIndex: 1,
        smooth: true,
        data: dailyData.value.map(d => d.accuracy),
        itemStyle: { color: '#FAC858' },
        markLine: {
          data: [{ yAxis: 70, name: '及格线' }],
          lineStyle: { color: '#EE6666', type: 'dashed' }
        }
      }
    ]
  })
}

// 初始化误差分布图
const initErrorDistChart = (data) => {
  if (!errorDistChartRef.value) return
  
  const errorDistChart = echarts.init(errorDistChartRef.value)
  errorDistChart.setOption({
    tooltip: {
      trigger: 'item',
      formatter: '{b}: {c}%'
    },
    legend: {
      orient: 'vertical',
      left: 'left'
    },
    series: [
      {
        name: '误差分布',
        type: 'pie',
        radius: '50%',
        data: data.map(item => ({
          value: item.percentage,
          name: `${item.range}\n${item.count}天`,
          itemStyle: {
            color: item.range === '0-5%' ? '#67C23A' :
                   item.range === '5-10%' ? '#409EFF' :
                   item.range === '10-20%' ? '#E6A23C' :
                   item.range === '20-30%' ? '#F56C6C' :
                   item.range === '30-50%' ? '#F56C6C' : '#909399'
          }
        })),
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)'
          }
        },
        label: {
          formatter: '{b}: {c}%'
        }
      }
    ]
  })
}

// 页面加载时初始化
onMounted(async () => {
  // 再次确认日期范围
  const dateRange = getDateRange()
  console.log('onMounted 日期范围:', dateRange)
  console.log('searchForm 初始值:', searchForm)
  
  await loadAllData()
  
  // 窗口大小变化时重新渲染图表
  window.addEventListener('resize', () => {
    if (dailyChartRef.value) {
      echarts.getInstanceByDom(dailyChartRef.value)?.resize()
    }
    if (errorDistChartRef.value) {
      echarts.getInstanceByDom(errorDistChartRef.value)?.resize()
    }
  })
})
</script>

<style scoped>
.sales-accuracy-analysis {
  padding: 15px;
}

.page-header {
  margin-bottom: 20px;
  display: flex;
  align-items: center;
  gap: 10px;
}

.page-title {
  font-size: 20px;
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
  gap: 10px;
}

.metrics-row,
.bias-row,
.charts-row,
.weekday-row,
.abnormal-row,
.forecast-row,
.rating-row {
  margin-bottom: 15px;
}

.metric-card {
  display: flex;
  align-items: center;
  padding: 10px;
}

.metric-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 15px;
}

.metric-icon :deep(.el-icon) {
  font-size: 30px;
  color: white;
}

.metric-info {
  flex: 1;
}

.metric-label {
  font-size: 13px;
  color: #909399;
  margin-bottom: 5px;
}

.metric-value {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
}

.metric-trend {
  display: flex;
  align-items: center;
  gap: 3px;
  font-size: 12px;
  margin-top: 5px;
}

.metric-trend.up {
  color: #67C23A;
}

.metric-trend.down {
  color: #F56C6C;
}

.metric-desc {
  font-size: 12px;
  color: #909399;
  margin-top: 5px;
}

.chart-container {
  width: 100%;
}

.date-row,
.weekday-row {
  margin-bottom: 15px;
}

.el-table__footer-wrapper {
  font-weight: bold;
}

.el-table__footer .cell {
  color: #303133;
}

.rating-card {
  text-align: center;
}

.rating-content {
  padding: 20px;
}

.rating-title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 15px;
}

.rating-stars {
  margin-bottom: 10px;
}

.rating-text {
  font-size: 16px;
  color: #606266;
  margin-bottom: 10px;
}

.rating-desc {
  font-size: 13px;
  color: #909399;
}

/* 移动端优化 */
@media screen and (max-width: 768px) {
  .sales-accuracy-analysis {
    padding: 10px;
  }
  
  .metric-card {
    margin-bottom: 10px;
  }
  
  .metric-icon {
    width: 50px;
    height: 50px;
  }
  
  .metric-icon :deep(.el-icon) {
    font-size: 24px;
  }
  
  .metric-value {
    font-size: 20px;
  }
}
</style>
