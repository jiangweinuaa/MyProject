<template>
  <div class="goods-forecast-accuracy">
    <!-- 返回按钮和标题 -->
    <div class="page-header">
      <el-button @click="goBack" :icon="ArrowLeft" size="default">
        返回
      </el-button>
      <span class="page-title">📦 销售预估准确性分析（单品）</span>
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
            <el-icon><ShoppingCart /></el-icon>
          </div>
          <div class="metric-info">
            <div class="metric-label">SKU 总数</div>
            <div class="metric-value">{{ metrics.totalSku }} 个</div>
            <div class="metric-desc">有效 SKU：{{ metrics.validSku }} 个</div>
          </div>
        </el-card>
      </el-col>

      <el-col :xs="24" :sm="12" :md="6">
        <el-card shadow="hover" class="metric-card">
          <div class="metric-icon" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%)">
            <el-icon><TrendCharts /></el-icon>
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
          <div class="metric-icon" style="background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)">
            <el-icon><DataLine /></el-icon>
          </div>
          <div class="metric-info">
            <div class="metric-label">MAPE</div>
            <div class="metric-value">{{ metrics.mape }}%</div>
            <div class="metric-desc">平均绝对百分比误差</div>
          </div>
        </el-card>
      </el-col>

      <el-col :xs="24" :sm="12" :md="6">
        <el-card shadow="hover" class="metric-card">
          <div class="metric-icon" style="background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)">
            <el-icon><Box /></el-icon>
          </div>
          <div class="metric-info">
            <div class="metric-label">预估总量</div>
            <div class="metric-value">{{ formatNumber(metrics.totalForecastQty) }}</div>
            <div class="metric-desc">实际：{{ formatNumber(metrics.totalActualQty) }} 件</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 偏差分析 -->
    <el-row :gutter="15" class="bias-row">
      <el-col :xs="24" :md="12">
        <el-card shadow="hover">
          <template #header>
            <span>📈 偏差分析</span>
          </template>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="Bias(均值)">
              {{ bias.bias > 0 ? '+' : '' }}{{ bias.bias }} 件/SKU
            </el-descriptions-item>
            <el-descriptions-item label="偏差方向">
              <el-tag :type="bias.biasDirection === '高估' ? 'warning' : (bias.biasDirection === '低估' ? 'success' : 'info')">
                {{ bias.biasDirection }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="总偏差率">
              {{ bias.totalBiasRate }}%
            </el-descriptions-item>
            <el-descriptions-item label="低估 SKU 数">
              <el-tag type="success">{{ bias.underestimateCount }} 个</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="高估 SKU 数">
              <el-tag type="warning">{{ bias.overestimateCount }} 个</el-tag>
            </el-descriptions-item>
          </el-descriptions>
        </el-card>
      </el-col>

      <el-col :xs="24" :md="12">
        <el-card shadow="hover">
          <template #header>
            <span>💰 误差指标</span>
          </template>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="MAE">
              {{ metrics.mae }} 件
            </el-descriptions-item>
            <el-descriptions-item label="RMSE">
              {{ metrics.rmse }}
            </el-descriptions-item>
            <el-descriptions-item label="MAPE">
              {{ metrics.mape }}%
            </el-descriptions-item>
            <el-descriptions-item label="综合准确率">
              <el-tag :type="getAccuracyType(metrics.accuracyRate)">
                {{ metrics.accuracyRate }}%
              </el-tag>
            </el-descriptions-item>
          </el-descriptions>
        </el-card>
      </el-col>
    </el-row>

    <!-- 综合评级 -->
    <el-row :gutter="15" class="rating-row">
      <el-col :xs="24">
        <el-card shadow="hover" class="rating-card">
          <div class="rating-content">
            <div class="rating-title">🏆 综合评级</div>
            <div class="rating-body">
              <div style="display: flex; align-items: center; gap: 15px">
                <el-rate v-model="rating.stars" disabled :colors="['#F56C6C', '#E6A23C', '#67C23A']" />
                <span style="font-size: 16px; font-weight: bold; color: #303133">{{ rating.text }}</span>
                <el-tooltip placement="top">
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
                  <el-icon style="cursor: pointer; color: #909399; font-size: 18px"><QuestionFilled /></el-icon>
                </el-tooltip>
              </div>
              <div class="rating-desc">{{ rating.description }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- SKU 明细表格 -->
    <el-row :gutter="15" class="sku-row">
      <el-col :xs="24">
        <el-card shadow="hover">
          <template #header>
            <div style="display: flex; justify-content: space-between; align-items: center">
              <span>📋 SKU 明细（全部 {{ skuDetail.length }} 个）</span>
              <el-input
                v-model="skuFilter"
                placeholder="搜索品号或品名..."
                clearable
                style="width: 250px"
                :prefix-icon="Search"
              />
            </div>
          </template>
          <el-table 
            :data="filteredSkuDetail" 
            style="width: 100%" 
            max-height="600"
            :default-sort="{prop: 'mape', order: 'descending'}"
          >
            <el-table-column prop="pluno" label="品号" width="120" fixed sortable />
            <el-table-column prop="pluName" label="品名" width="250" show-overflow-tooltip sortable />
            <el-table-column prop="forecastQty" label="预估销量" width="100" align="center" sortable>
              <template #default="{ row }">{{ formatNumber(row.forecastQty) }}</template>
            </el-table-column>
            <el-table-column prop="actualQty" label="实际销量" width="100" align="center" sortable>
              <template #default="{ row }">{{ formatNumber(row.actualQty) }}</template>
            </el-table-column>
            <el-table-column prop="error" label="误差" width="100" align="center" sortable>
              <template #default="{ row }">
                <el-tag :type="row.error > 0 ? 'warning' : (row.error < 0 ? 'success' : 'info')" size="small">
                  {{ row.error > 0 ? '+' : '' }}{{ formatNumber(row.error) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="mape" label="MAPE (%)" width="100" align="center" sortable>
              <template #default="{ row }">
                <el-tag :type="row.mape < 10 ? 'success' : (row.mape < 20 ? 'primary' : (row.mape < 30 ? 'warning' : 'danger'))" size="small">
                  {{ row.mape }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="accuracy" label="准确率 (%)" width="100" align="center" sortable>
              <template #default="{ row }">
                <el-tag :type="getAccuracyType(row.accuracy)" size="small">
                  {{ row.accuracy }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="salesLevel" label="销量级别" width="120" align="center" sortable />
            <el-table-column prop="biasDirection" label="偏差" width="80" align="center">
              <template #default="{ row }">
                <el-tag :type="row.biasDirection === '高估' ? 'warning' : (row.biasDirection === '低估' ? 'success' : 'info')" size="mini">
                  {{ row.biasDirection }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <!-- 误差区间分析 -->
    <el-row :gutter="15" class="error-dist-row">
      <el-col :xs="24" :md="12">
        <el-card shadow="hover">
          <template #header>
            <span>📊 误差区间分析</span>
          </template>
          <el-table :data="errorDist" style="width: 100%">
            <el-table-column prop="range" label="误差区间" width="100" />
            <el-table-column prop="count" label="SKU 数" width="80" align="center" />
            <el-table-column prop="percentage" label="占比 (%)" width="100" align="center">
              <template #default="{ row }">{{ row.percentage }}%</template>
            </el-table-column>
            <el-table-column prop="cumulativePercentage" label="累计占比 (%)" width="120" align="center">
              <template #default="{ row }">{{ row.cumulativePercentage }}%</template>
            </el-table-column>
            <el-table-column prop="quality" label="质量评价" align="center">
              <template #default="{ row }">
                <el-tag :type="row.quality === '极准确' || row.quality === '准确' ? 'success' : (row.quality === '尚可' ? 'primary' : (row.quality === '偏差较大' ? 'warning' : 'danger'))">
                  {{ row.quality }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>

      <el-col :xs="24" :md="12">
        <el-card shadow="hover">
          <template #header>
            <span>📈 按销量级别分析</span>
          </template>
          <el-table :data="salesLevelAnalysis" style="width: 100%">
            <el-table-column prop="level" label="销量级别" width="110" />
            <el-table-column prop="skuCount" label="SKU 数" width="80" align="center" />
            <el-table-column prop="forecastAvg" label="预估均值" width="90" align="center">
              <template #default="{ row }">{{ row.forecastAvg }}</template>
            </el-table-column>
            <el-table-column prop="actualAvg" label="实际均值" width="90" align="center">
              <template #default="{ row }">{{ row.actualAvg }}</template>
            </el-table-column>
            <el-table-column prop="mape" label="MAPE (%)" width="90" align="center">
              <template #default="{ row }">
                <el-tag :type="row.mape < 20 ? 'success' : (row.mape < 40 ? 'warning' : 'danger')" size="small">
                  {{ row.mape }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="accuracy" label="准确率 (%)" width="90" align="center">
              <template #default="{ row }">
                <el-tag :type="getAccuracyType(row.accuracy)" size="small">
                  {{ row.accuracy }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="bias" label="Bias" width="80" align="center">
              <template #default="{ row }">
                <span :style="row.bias > 0 ? 'color: #E6A23C' : (row.bias < 0 ? 'color: #67C23A' : '')">
                  {{ row.bias > 0 ? '+' : '' }}{{ row.bias }}
                </span>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <!-- 重点关注 SKU -->
    <el-row :gutter="15" class="top20-row">
      <el-col :xs="24">
        <el-card shadow="hover">
          <template #header>
            <span>⚠️ 重点关注 SKU（误差最大的 Top 20）</span>
          </template>
          <el-table :data="top20Sku" style="width: 100%">
            <el-table-column prop="rank" label="排名" width="70" align="center" type="index">
              <template #default="{ $index }">
                <el-tag :type="$index < 3 ? 'danger' : 'info'" size="small" effect="dark">
                  {{ $index + 1 }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="pluno" label="品号" width="120" sortable />
            <el-table-column prop="pluName" label="品名" width="200" show-overflow-tooltip />
            <el-table-column prop="forecastQty" label="预估销量" width="100" align="center">
              <template #default="{ row }">{{ formatNumber(row.forecastQty) }}</template>
            </el-table-column>
            <el-table-column prop="actualQty" label="实际销量" width="100" align="center">
              <template #default="{ row }">{{ formatNumber(row.actualQty) }}</template>
            </el-table-column>
            <el-table-column prop="error" label="误差" width="100" align="center" sortable>
              <template #default="{ row }">
                <el-tag :type="row.error > 0 ? 'warning' : 'success'" size="small">
                  {{ row.error > 0 ? '+' : '' }}{{ formatNumber(row.error) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="mape" label="MAPE (%)" width="100" align="center" sortable>
              <template #default="{ row }">
                <el-tag type="danger" size="small">{{ row.mape }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="salesLevel" label="销量级别" width="110" align="center" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ShoppingCart, TrendCharts, DataLine, Box, ArrowLeft, Search, Refresh, QuestionFilled } from '@element-plus/icons-vue'
import { queryGoodsAccuracyAnalysisFull } from '@/api/goodsForecast'

const router = useRouter()
const loading = ref(false)
const skuFilter = ref('')

// 是否移动端
const isMobile = computed(() => {
  return window.innerWidth < 768
})

// 获取日期范围：默认为昨天
const getDateRange = () => {
  const now = new Date()
  // 昨天
  const yesterday = new Date(now)
  yesterday.setDate(yesterday.getDate() - 1)
  
  const year = yesterday.getFullYear()
  const month = String(yesterday.getMonth() + 1).padStart(2, '0')
  const day = String(yesterday.getDate()).padStart(2, '0')
  
  const startDate = `${year}-${month}-${day}`
  const endDate = `${year}-${month}-${day}`
  
  return { startDate, endDate }
}

// 查询表单
const dateRange = getDateRange()
const searchForm = reactive({
  shopId: '',
  startDate: dateRange.startDate,
  endDate: dateRange.endDate
})

// 核心指标
const metrics = reactive({
  totalSku: 0,
  validSku: 0,
  totalForecastQty: 0,
  totalActualQty: 0,
  mae: 0,
  rmse: 0,
  mape: 0,
  accuracyRate: 0
})

// 偏差分析
const bias = reactive({
  bias: 0,
  biasDirection: '-',
  totalBiasRate: 0,
  overestimateCount: 0,
  underestimateCount: 0
})

// SKU 明细
const skuDetail = ref([])

// 误差区间
const errorDist = ref([])

// 销量级别分析
const salesLevelAnalysis = ref([])

// Top20 SKU
const top20Sku = ref([])

// 综合评级
const rating = reactive({
  stars: 3,
  text: '-',
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

// 过滤后的 SKU 明细
const filteredSkuDetail = computed(() => {
  if (!skuFilter.value) return skuDetail.value
  const filter = skuFilter.value.toLowerCase()
  return skuDetail.value.filter(item => 
    item.pluno.toLowerCase().includes(filter) || 
    (item.pluName && item.pluName.toLowerCase().includes(filter))
  )
})

// 查询
const handleSearch = async () => {
  loading.value = true
  try {
    const params = {
      shopId: searchForm.shopId || '',
      startDate: searchForm.startDate,
      endDate: searchForm.endDate
    }
    await loadData(params)
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

// 加载数据
const loadData = async (params = {}) => {
  try {
    const queryParams = {
      shopId: localStorage.getItem('shopid') || '',
      startDate: searchForm.startDate,
      endDate: searchForm.endDate,
      ...params
    }
    
    const response = await queryGoodsAccuracyAnalysisFull(queryParams)
    if (response.success && response.datas) {
      const data = response.datas
      
      // 更新指标
      if (data.metrics) {
        const m = data.metrics
        metrics.totalSku = m.totalSku || 0
        metrics.validSku = m.validSku || 0
        metrics.totalForecastQty = m.totalForecastQty || 0
        metrics.totalActualQty = m.totalActualQty || 0
        metrics.mae = m.mae || 0
        metrics.rmse = m.rmse || 0
        metrics.mape = m.mape || 0
        metrics.accuracyRate = m.accuracyRate || 0
      }
      
      // 更新偏差
      if (data.bias) {
        const b = data.bias
        bias.bias = b.bias || 0
        bias.biasDirection = b.biasDirection || '-'
        bias.totalBiasRate = b.totalBiasRate || 0
        bias.overestimateCount = b.overestimateCount || 0
        bias.underestimateCount = b.underestimateCount || 0
      }
      
      // 更新 SKU 明细
      if (data.skuDetail) {
        skuDetail.value = data.skuDetail
      }
      
      // 更新误差区间
      if (data.errorDist) {
        errorDist.value = data.errorDist
      }
      
      // 更新销量级别分析
      if (data.salesLevelAnalysis) {
        salesLevelAnalysis.value = data.salesLevelAnalysis
      }
      
      // 更新 Top20 SKU
      if (data.top20Sku) {
        top20Sku.value = data.top20Sku
      }
      
      // 更新评级
      if (data.rating) {
        rating.stars = data.rating.stars || 3
        rating.text = data.rating.text || '-'
        rating.description = data.rating.description || '-'
      }
    }
  } catch (error) {
    console.error('加载数据失败:', error)
  }
}

// 返回
const goBack = () => {
  router.back()
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.goods-forecast-accuracy {
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
.rating-row,
.sku-row,
.error-dist-row,
.top20-row {
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

.metric-desc {
  font-size: 12px;
  color: #909399;
  margin-top: 5px;
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

.rating-body {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
}

.rating-desc {
  font-size: 14px;
  color: #606266;
}

/* 移动端优化 */
@media screen and (max-width: 768px) {
  .goods-forecast-accuracy {
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
