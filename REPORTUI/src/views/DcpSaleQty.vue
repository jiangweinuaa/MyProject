<template>
  <div class="dcp-sale-qty">
    <!-- 返回按钮和标题 -->
    <div class="page-header">
      <el-button @click="goBack" :icon="ArrowLeft" size="default">
        返回
      </el-button>
      <span class="page-title">商品销售明细</span>
    </div>

    <!-- 查询条件 -->
    <el-card class="search-card" shadow="hover">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="门店">
          <el-input
            v-model="searchForm.shopId"
            placeholder="门店 ID"
            :style="isMobile ? 'width: 100%' : 'width: 150px'"
            clearable
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

    <!-- 数据表格 -->
    <el-card class="table-card" shadow="hover">
      <template #header>
        <div class="card-title">
          <el-icon><List /></el-icon>
          <span>销售明细列表</span>
        </div>
      </template>
      <el-table :data="saleList" style="width: 100%" v-loading="loading" show-summary :summary-method="getSummaries">
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="SHOPID" label="门店 ID" width="100" sortable />
        <el-table-column prop="SALENO" label="销售单号" width="150" sortable />
        <el-table-column prop="TOT_OLDAMT" label="原金额" width="100" align="right" sortable>
          <template #default="{ row }">
            ¥ {{ formatNumber(row.TOT_OLDAMT) }}
          </template>
        </el-table-column>
        <el-table-column prop="TOT_DISC" label="折扣" width="100" align="right" sortable>
          <template #default="{ row }">
            ¥ {{ formatNumber(row.TOT_DISC) }}
          </template>
        </el-table-column>
        <el-table-column prop="TOT_AMT" label="销售金额" width="100" align="right" sortable>
          <template #default="{ row }">
            ¥ {{ formatNumber(row.TOT_AMT) }}
          </template>
        </el-table-column>
        <el-table-column prop="BDATE" label="业务日期" width="100" sortable>
          <template #default="{ row }">
            {{ formatDate(row.BDATE) }}
          </template>
        </el-table-column>
        <el-table-column prop="WORKNO" label="工作日" width="80" />
        <el-table-column prop="OPNO" label="操作员" width="80" />
        <el-table-column prop="MACHINE" label="机台" width="80" />
        <el-table-column prop="SDATE" label="系统日期" width="100">
          <template #default="{ row }">
            {{ formatDate(row.SDATE) }}
          </template>
        </el-table-column>
        <el-table-column prop="STIME" label="系统时间" width="80" />
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pagination.pageNumber"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50, 100, 500, 1000, 10000]"
          :size-labels="['10 条', '20 条', '50 条', '100 条', '500 条', '1000 条', '不分页']"
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
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getDcpSaleQty } from '@/api/report'

const router = useRouter()

// 判断是否为移动端
const isMobile = ref(false)

const checkMobile = () => {
  isMobile.value = window.innerWidth <= 768
}

// 获取今天日期 YYYY-MM-DD
const getToday = () => {
  const today = new Date()
  const year = today.getFullYear()
  const month = String(today.getMonth() + 1).padStart(2, '0')
  const day = String(today.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
}

const today = getToday()

// 查询表单
const searchForm = reactive({
  shopId: '',
  startDate: today,
  endDate: today
})

const loading = ref(false)
const saleList = ref([])

// 汇总数据
const summaryData = ref({
  totOldAmt: 0,  // 原金额合计
  totDisc: 0,    // 折扣合计
  totAmt: 0,     // 销售金额合计
  count: 0       // 记录数
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

// 格式化日期 YYYYMMDD -> YYYY-MM-DD
const formatDate = (dateStr) => {
  if (!dateStr || dateStr.length !== 8) return dateStr
  return `${dateStr.substring(0, 4)}-${dateStr.substring(4, 6)}-${dateStr.substring(6, 8)}`
}

// 格式化日期 YYYY-MM-DD -> YYYYMMDD
const formatDateToDB = (dateStr) => {
  if (!dateStr) return ''
  return dateStr.replace(/-/g, '')
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
    
    const res = await getDcpSaleQty(params)
    
    if (res.datas && res.datas.list) {
      saleList.value = res.datas.list
      
      // 更新分页信息
      pagination.totalRecords = res.totalRecords || 0
      pagination.totalPages = res.totalPages || 0
      
      // 计算汇总数据
      calculateSummary(res.datas.list)
      
      ElMessage.success(`查询成功，共 ${res.totalRecords || 0} 条记录`)
    }
  } catch (error) {
    console.error('查询失败:', error)
    ElMessage.error('查询失败：' + error.message)
  } finally {
    loading.value = false
  }
}

// 计算汇总数据
const calculateSummary = (list) => {
  summaryData.value = {
    totOldAmt: 0,
    totDisc: 0,
    totAmt: 0,
    count: list.length
  }
  
  list.forEach(item => {
    summaryData.value.totOldAmt += Number(item.TOT_OLDAMT || 0)
    summaryData.value.totDisc += Number(item.TOT_DISC || 0)
    summaryData.value.totAmt += Number(item.TOT_AMT || 0)
  })
}

// 表格汇总行
const getSummaries = (param) => {
  const { columns, data } = param
  const sums = []
  
  columns.forEach((column, index) => {
    if (index === 0) {
      sums[index] = '汇总'
      return
    }
    
    // 原金额列
    if (column.property === 'TOT_OLDAMT') {
      sums[index] = '¥ ' + formatNumber(summaryData.value.totOldAmt)
      return
    }
    
    // 折扣列
    if (column.property === 'TOT_DISC') {
      sums[index] = '¥ ' + formatNumber(summaryData.value.totDisc)
      return
    }
    
    // 销售金额列
    if (column.property === 'TOT_AMT') {
      sums[index] = '¥ ' + formatNumber(summaryData.value.totAmt)
      return
    }
    
    // 其他列显示记录数
    if (column.property === 'SHOPID') {
      sums[index] = `${summaryData.value.count} 条`
      return
    }
    
    sums[index] = ''
  })
  
  return sums
}

// 重置
const handleReset = () => {
  searchForm.shopId = ''
  searchForm.startDate = today
  searchForm.endDate = today
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

// 返回上一页
const goBack = () => {
  router.back()
}

onMounted(() => {
  checkMobile()
  handleSearch()
  window.addEventListener('resize', checkMobile)
})
</script>

<style scoped>
.dcp-sale-qty {
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
