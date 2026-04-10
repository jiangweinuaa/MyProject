<template>
  <div class="category-sale-analysis">
    <!-- 返回按钮和标题 -->
    <div class="page-header">
      <el-button @click="goBack" :icon="ArrowLeft" size="default">
        返回
      </el-button>
      <span class="page-title">品类销售分析</span>
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
            placeholder="门店 ID（留空查询全部）"
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
            <el-icon class="card-icon" style="color: #67C23A"><Goods /></el-icon>
            <span>销售数量</span>
          </div>
          <div class="card-value">{{ formatNumber(summaryData.totalQty) }}</div>
          <div class="card-footer">
            <span class="trend-label">总销售件数</span>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card shadow="hover" class="data-card">
          <div class="card-header">
            <el-icon class="card-icon" style="color: #E6A23C"><List /></el-icon>
            <span>品类数量</span>
          </div>
          <div class="card-value">{{ formatNumber(summaryData.categoryCount) }}</div>
          <div class="card-footer">
            <span class="trend-label">销售品类数</span>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card shadow="hover" class="data-card">
          <div class="card-header">
            <el-icon class="card-icon" style="color: #F56C6C"><Ticket /></el-icon>
            <span>订单数量</span>
          </div>
          <div class="card-value">{{ formatNumber(summaryData.totalOrderCount) }}</div>
          <div class="card-footer">
            <span class="trend-label">总订单数</span>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 品类占比饼图 -->
    <el-card class="chart-card" shadow="hover">
      <template #header>
        <div class="card-title">
          <el-icon><PieChart /></el-icon>
          <span>各品类销售占比</span>
        </div>
      </template>
      <div class="chart-container" ref="categoryPieChartRef" id="categoryPieChart"></div>
    </el-card>

    <!-- 品类销售明细表 -->
    <el-card class="table-card" shadow="hover">
      <template #header>
        <div class="card-title">
          <el-icon><List /></el-icon>
          <span>品类销售明细</span>
        </div>
      </template>
      <el-table :data="categoryList" style="width: 100%" v-loading="loading" :default-sort="{prop: 'SALE_AMT', order: 'descending'}">
        <el-table-column type="index" label="排名" width="60" align="center" />
        <el-table-column prop="CATEGORYID" label="品类 ID" width="100" sortable />
        <el-table-column prop="CATEGORY_NAME" label="品类名称" min-width="150" />
        <el-table-column prop="SALE_AMT" label="销售金额" width="120" align="right" sortable>
          <template #default="{ row }">
            ¥ {{ formatNumber(row.SALE_AMT) }}
          </template>
        </el-table-column>
        <el-table-column prop="SALE_QTY" label="销售数量" width="100" align="right" sortable />
        <el-table-column prop="ORDER_COUNT" label="订单数" width="80" align="right" sortable />
        <el-table-column label="占比" width="150" align="right">
          <template #default="{ row }">
            <el-progress 
              :percentage="calculatePercent(row.SALE_AMT)" 
              :stroke-width="12"
              :color="getProgressColor(calculatePercent(row.SALE_AMT))"
            />
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pagination.pageNumber"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.totalRecords"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getCategorySaleQuery } from '@/api/report'
import * as echarts from 'echarts'
import { getDefaultDateRange, formatDateToDB } from '@/utils/date'

const router = useRouter()

// 判断是否为移动端
const isMobile = ref(false)

const checkMobile = () => {
  isMobile.value = window.innerWidth <= 768
}

// 查询表单 - 默认本月第一天到今天
const defaultDateRange = getDefaultDateRange()

const searchForm = reactive({
  shopId: '',
  startDate: defaultDateRange.startDate,
  endDate: defaultDateRange.endDate
})

const loading = ref(false)
const categoryList = ref([])
const categoryPieChartRef = ref(null)
let categoryPieChart = null

// 汇总数据
const summaryData = ref({
  totalAmount: 0,
  totalQty: 0,
  categoryCount: 0,
  totalOrderCount: 0
})

// 分页信息
const pagination = reactive({
  pageNumber: 1,
  pageSize: 20,
  totalRecords: 0,
  totalPages: 0
})

// 格式化数字
const formatNumber = (num) => {
  if (!num && num !== 0) return '0.00'
  return Number(num).toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
}

// 格式化日期使用工具函数（已导入）

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
    const params = {
      shopId: searchForm.shopId ? searchForm.shopId.trim() : '',
      startDate: formatDateToDB(searchForm.startDate),
      endDate: formatDateToDB(searchForm.endDate),
      pageNumber: pagination.pageNumber,
      pageSize: pagination.pageSize
    }
    
    console.log('查询参数:', params)
    
    const res = await getCategorySaleQuery(params)
    
    if (res.datas && res.datas.list) {
      categoryList.value = res.datas.list
      
      // 设置汇总数据
      if (res.datas.summary) {
        summaryData.value = {
          totalAmount: res.datas.summary.totalAmount || 0,
          totalQty: res.datas.summary.totalQty || 0,
          categoryCount: res.datas.summary.categoryCount || 0,
          totalOrderCount: res.datas.summary.totalOrderCount || 0
        }
      }
      
      // 更新分页信息
      pagination.totalRecords = res.totalRecords || 0
      pagination.totalPages = res.totalPages || 0
      
      // 渲染图表
      nextTick(() => {
        renderCategoryPieChart(categoryList.value)
      })
      
      ElMessage.success(`查询成功，共 ${res.totalRecords || 0} 个品类`)
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
  searchForm.shopId = ''
  searchForm.startDate = defaultDateRange.startDate
  searchForm.endDate = defaultDateRange.endDate
  pagination.pageNumber = 1
  handleSearch()
}

// 分页大小变化
const handleSizeChange = (size) => {
  pagination.pageSize = size
  pagination.pageNumber = 1
  handleSearch()
}

// 页码变化
const handleCurrentChange = (page) => {
  pagination.pageNumber = page
  handleSearch()
}

// 渲染品类占比饼图
const renderCategoryPieChart = (data) => {
  if (!categoryPieChartRef.value) return
  
  if (categoryPieChart) {
    categoryPieChart.dispose()
  }
  
  categoryPieChart = echarts.init(categoryPieChartRef.value)
  
  const colors = ['#409EFF', '#67C23A', '#E6A23C', '#F56C6C', '#909399', '#13C2C2', '#722ED1', '#FA541C', '#1890FF', '#13C2C2']
  
  const pieData = data.slice(0, 10).map((item, index) => ({
    name: item.CATEGORY_NAME || item.CATEGORYID || '未知品类',
    value: item.SALE_AMT || 0,
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
      name: '品类销售',
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
  
  categoryPieChart.setOption(option)
}

// 窗口大小变化
const handleResize = () => {
  checkMobile()
  if (categoryPieChart) {
    categoryPieChart.resize()
  }
}

// 返回
const goBack = () => {
  router.back()
}

onMounted(() => {
  checkMobile()
  handleSearch()
  window.addEventListener('resize', handleResize)
})
</script>

<style scoped>
.category-sale-analysis {
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

.chart-card,
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

.chart-container {
  height: 400px;
  width: 100%;
}

.pagination-container {
  display: flex;
  justify-content: flex-end;
  padding: 15px 0 0 0;
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

  .pagination-container {
    justify-content: center;
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
