<template>
  <div class="training-library">
    <!-- 页面标题 -->
    <div class="page-header">
      <span class="page-title">📚 训练库管理</span>
    </div>

    <!-- 统计卡片 -->
    <el-row :gutter="15" class="stats-row">
      <el-col :xs="24" :sm="12" :md="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%)">
            <el-icon><Goods /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.productCount }}</div>
            <div class="stat-label">训练商品数</div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :xs="24" :sm="12" :md="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%)">
            <el-icon><Picture /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.imageCount }}</div>
            <div class="stat-label">训练图片数</div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :xs="24" :sm="12" :md="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon" style="background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)">
            <el-icon><DataAnalysis /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.avgImagesPerProduct }}</div>
            <div class="stat-label">平均每商品图片</div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :xs="24" :sm="12" :md="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon" style="background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)">
            <el-icon><CircleCheck /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.accuracy }}%</div>
            <div class="stat-label">模型准确率</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 搜索和筛选 -->
    <el-card class="search-card" shadow="hover">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="商品品号">
          <el-input v-model="searchForm.pluno" placeholder="输入品号" clearable style="width: 200px" />
        </el-form-item>
        
        <el-form-item label="商品名称">
          <el-input v-model="searchForm.productName" placeholder="输入名称" clearable style="width: 200px" />
        </el-form-item>
        
        <el-form-item label="商品类别">
          <el-select v-model="searchForm.category" placeholder="全部类别" clearable style="width: 150px">
            <el-option label="面包" value="面包" />
            <el-option label="蛋糕" value="蛋糕" />
            <el-option label="西点" value="西点" />
            <el-option label="饮品" value="饮品" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="loadData" :icon="Search">查询</el-button>
          <el-button @click="resetSearch" :icon="Refresh">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 训练数据列表 - 卡片形式 -->
    <el-card class="table-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <span>📋 训练数据列表</span>
          <el-button type="success" :icon="Download" @click="exportData">导出数据</el-button>
        </div>
      </template>
      
      <!-- 商品卡片网格 -->
      <div class="product-grid" v-loading="loading">
        <div class="product-card" v-for="(row, index) in tableData" :key="index">
          <div class="product-header">
            <span class="product-pluno">{{ row.PLUNO }}</span>
            <el-tag :type="getCategoryType(row.CATEGORY)" size="small">{{ row.CATEGORY }}</el-tag>
          </div>
          
          <div class="product-name">{{ row.PRODUCT_NAME }}</div>
          
          <div class="product-stats">
            <div class="stat-item">
              <el-tag :type="row.IMAGE_COUNT >= 5 ? 'success' : 'warning'" size="small">
                📷 {{ row.IMAGE_COUNT }}
              </el-tag>
            </div>
            <div class="stat-item" v-if="row.accuracy !== undefined">
              <el-tag :type="getAccuracyType(row.accuracy)" size="small">
                🎯 {{ row.accuracy }}%
              </el-tag>
            </div>
          </div>
          
          <div class="product-quality">
            <span class="quality-label">质量:</span>
            <el-rate v-model="row.QUALITY_SCORE" disabled size="small" :colors="['#99A9BF', '#F7BA2A', '#67C23A']" />
          </div>
          
          <div class="product-time">
            <el-icon><Clock /></el-icon>
            <span>{{ row.CREATED_TIME }}</span>
          </div>
          
          <div class="product-actions">
            <el-button size="small" type="primary" @click="viewImages(row)">
              👁️
            </el-button>
            <el-button size="small" type="danger" @click="deleteProduct(row)">
              🗑️
            </el-button>
          </div>
        </div>
      </div>
      
      <!-- 空状态 -->
      <el-empty v-if="tableData.length === 0 && !loading" description="暂无训练数据" :image-size="80" />
      
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

    <!-- 图片查看对话框 -->
    <el-dialog 
      v-model="dialogVisible" 
      title="📸 训练图片" 
      width="80%" 
      top="5vh"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
      @close="handleDialogClose"
    >
      <el-row :gutter="15">
        <el-col :xs="24" :sm="12" :md="8" :lg="6" v-for="(img, index) in currentImages" :key="index">
          <div class="image-item">
            <el-image :src="img.imageUrl" fit="cover" class="training-image" :preview-src-list="[img.imageUrl]" />
            <div class="image-info">
              <span>{{ img.taskName || '训练图片' }}</span>
              <span class="image-time">{{ formatTime(img.createdTime) }}</span>
            </div>
          </div>
        </el-col>
      </el-row>
      
      <template #footer>
        <el-button @click="dialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Download, Goods, Picture, DataAnalysis, CircleCheck, Clock } from '@element-plus/icons-vue'
import request from '@/api/request'

// 统计数据
const stats = ref({
  productCount: 0,
  imageCount: 0,
  avgImagesPerProduct: 0,
  accuracy: 0
})

// 搜索表单
const searchForm = reactive({
  pluno: '',
  productName: '',
  category: ''
})

// 表格数据
const tableData = ref([])
const loading = ref(false)

// 分页
const pagination = reactive({
  pageNumber: 1,
  pageSize: 20,
  totalRecords: 0
})

// 图片查看对话框
const dialogVisible = ref(false)
const currentImages = ref([])

// 加载统计数据
const loadStats = async () => {
  try {
    const response = await fetch('http://47.100.138.89:8110/api/product/training-stats', {
      method: 'GET'
    }).then(res => res.json())
    
    if (response.success) {
      stats.value = {
        productCount: response.productCount || 0,
        imageCount: response.imageCount || 0,
        avgImagesPerProduct: response.avgImagesPerProduct || 0,
        accuracy: response.accuracy || 0
      }
    }
  } catch (error) {
    console.error('加载统计数据失败:', error)
  }
}

// 加载训练数据
const loadData = async () => {
  loading.value = true
  
  try {
    // 构建查询参数
    const params = new URLSearchParams()
    params.append('pageNumber', pagination.pageNumber)
    params.append('pageSize', pagination.pageSize)
    
    if (searchForm.pluno) params.append('pluno', searchForm.pluno)
    if (searchForm.productName) params.append('productName', searchForm.productName)
    if (searchForm.category) params.append('category', searchForm.category)
    
    // 调用后端接口
    const response = await fetch(`http://47.100.138.89:8110/api/training/list?${params.toString()}`, {
      method: 'GET'
    }).then(res => res.json())
    
    if (response.success) {
      tableData.value = response.data || []
      pagination.totalRecords = response.totalRecords || 0
      
      // 加载每个商品的识别准确率
      loadProductAccuracies()
    } else {
      ElMessage.error('加载数据失败：' + response.message)
      tableData.value = []
      pagination.totalRecords = 0
    }
  } catch (error) {
    console.error('加载数据失败:', error)
    ElMessage.error('加载数据失败：' + error.message)
    tableData.value = []
    pagination.totalRecords = 0
  } finally {
    loading.value = false
  }
}

// 加载所有商品的识别准确率
const loadProductAccuracies = async () => {
  try {
    // 并发请求所有商品的准确率
    const promises = tableData.value.map(async (row) => {
      const pluno = row.PLUNO || row.pluno
      if (!pluno) return
      
      try {
        const response = await fetch(`http://47.100.138.89:8110/api/product/product-accuracy?pluno=${encodeURIComponent(pluno)}`, {
          method: 'GET'
        }).then(res => res.json())
        
        if (response) {
          row.accuracy = response.accuracy || 0
          row.accuracyTotal = response.total || 0
          row.accuracyCorrect = response.correct || 0
        }
      } catch (error) {
        console.warn(`加载商品 ${pluno} 准确率失败:`, error)
      }
    })
    
    await Promise.all(promises)
    
  } catch (error) {
    console.error('加载准确率失败:', error)
  }
}

// 重置搜索
const resetSearch = () => {
  searchForm.pluno = ''
  searchForm.productName = ''
  searchForm.category = ''
  loadData()
}

// 分页大小变化
const handleSizeChange = (size) => {
  pagination.pageSize = size
  pagination.pageNumber = 1
  loadData()
}

// 页码变化
const handleCurrentChange = (page) => {
  pagination.pageNumber = page
  loadData()
}

// 查看图片
const viewImages = async (row) => {
  try {
    const pluno = row.PLUNO || row.pluno
    const response = await fetch(`http://47.100.138.89:8110/api/training/images?pluno=${pluno}`, {
      method: 'GET'
    }).then(res => res.json())
    
    if (response.success) {
      currentImages.value = response.data.map(img => ({
        imageUrl: img.IMAGE_URL || img.imageUrl ? 'http://47.100.138.89:8110' + (img.IMAGE_URL || img.imageUrl) : '',
        taskName: img.METADATA ? JSON.parse(img.METADATA).taskName : '训练图片',
        createdTime: img.CREATED_TIME || img.createdTime
      }))
      dialogVisible.value = true
      // 推入历史记录，拦截返回按钮
      history.pushState({ dialog: 'training-images' }, '', null)
    } else {
      ElMessage.error('查询图片失败：' + response.message)
    }
  } catch (error) {
    ElMessage.error('查询图片失败：' + error.message)
  }
}

// 删除商品
const deleteProduct = (row) => {
  const productName = row.PRODUCT_NAME || row.productName || '未知商品'
  const pluno = row.PLUNO || row.pluno
  
  ElMessageBox.confirm(`确定要删除商品"${productName}"的训练数据吗？`, '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const response = await fetch(`http://47.100.138.89:8110/api/training/delete?pluno=${pluno}`, {
        method: 'DELETE'
      }).then(res => res.json())
      
      if (response.success) {
        ElMessage.success('删除成功')
        loadData()
        loadStats()
      } else {
        ElMessage.error('删除失败：' + response.message)
      }
    } catch (error) {
      ElMessage.error('删除失败：' + error.message)
    }
  }).catch(() => {})
}

// 导出数据
const exportData = () => {
  // 调用后端导出接口（待实现）
  window.open('http://47.100.138.89:8110/api/training/export', '_blank')
  ElMessage.info('导出功能开发中...')
}

// 获取类别标签类型
const getCategoryType = (category) => {
  const types = {
    '面包': 'success',
    '蛋糕': 'warning',
    '西点': 'primary',
    '饮品': 'info',
    '其他': 'info'
  }
  return types[category] || 'info'
}

// 根据准确率返回标签类型
const getAccuracyType = (accuracy) => {
  if (accuracy >= 90) return 'success'
  if (accuracy >= 70) return 'primary'
  if (accuracy >= 50) return 'warning'
  return 'danger'
}

// 格式化时间
const formatTime = (time) => {
  if (!time) return ''
  return time
}

// 处理对话框关闭
const handleDialogClose = () => {
  // 清空图片数据
  currentImages.value = []
}

// 监听浏览器返回按钮
window.addEventListener('popstate', () => {
  if (dialogVisible.value) {
    dialogVisible.value = false
    currentImages.value = []
  }
})

// 初始化
onMounted(() => {
  loadStats()
  loadData()
})
</script>

<style scoped>
.training-library {
  padding: 15px;
}

.page-header {
  margin-bottom: 20px;
}

.page-title {
  font-size: 20px;
  font-weight: 600;
  color: #303133;
}

.stats-row {
  margin-bottom: 20px;
}

.stat-card {
  display: flex;
  align-items: center;
  padding: 10px;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 15px;
}

.stat-icon :deep(.el-icon) {
  font-size: 30px;
  color: white;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
}

.stat-label {
  font-size: 13px;
  color: #909399;
  margin-top: 5px;
}

.search-card,
.table-card {
  margin-bottom: 15px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.pagination-container {
  display: flex;
  justify-content: flex-end;
  padding: 15px 0 0 0;
}

/* 商品卡片网格 */
.product-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 6px;
}

.product-card {
  border: 1px solid #EBEEF5;
  border-radius: 6px;
  padding: 6px;
  background: #fff;
  transition: all 0.3s;
  display: flex;
  flex-direction: column;
}

.product-card:hover {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.product-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 4px;
}

.product-pluno {
  font-size: 14px;
  font-weight: 600;
  color: #409EFF;
}

.product-name {
  font-size: 13px;
  color: #303133;
  margin-bottom: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  flex: 1;
}

.product-stats {
  margin-bottom: 6px;
}

.stat-item {
  display: flex;
  align-items: center;
}

.stat-item :deep(.el-tag) {
  transform: scale(0.9);
  transform-origin: left center;
}

.product-quality {
  display: flex;
  align-items: center;
  gap: 4px;
  margin-bottom: 6px;
}

.quality-label {
  font-size: 12px;
  color: #909399;
}

.product-quality :deep(.el-rate) {
  display: inline-flex;
  transform: scale(0.9);
  transform-origin: left center;
}

.product-time {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 11px;
  color: #909399;
  margin-bottom: 8px;
}

.product-time :deep(.el-icon) {
  font-size: 12px;
}

.product-actions {
  display: flex;
  gap: 4px;
  margin-top: auto;
}

.product-actions .el-button {
  flex: 1;
  font-size: 13px;
  padding: 6px 0;
  min-height: 30px;
}

/* 移动端优化 */
@media screen and (max-width: 768px) {
  .product-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 5px;
  }
  
  .product-card {
    padding: 5px;
  }
  
  .product-header {
    margin-bottom: 3px;
  }
  
  .product-pluno {
    font-size: 10px;
  }
  
  .product-header :deep(.el-tag) {
    transform: scale(0.8);
  }
  
  .product-name {
    font-size: 9px;
    margin-bottom: 5px;
  }
  
  .product-stats {
    margin-bottom: 3px;
  }
  
  .stat-item :deep(.el-tag) {
    transform: scale(0.75);
  }
  
  .product-quality {
    margin-bottom: 3px;
    gap: 2px;
  }
  
  .quality-label {
    font-size: 8px;
  }
  
  .product-quality :deep(.el-rate) {
    transform: scale(0.7);
  }
  
  .product-time {
    font-size: 8px;
    margin-bottom: 5px;
  }
  
  .product-time :deep(.el-icon) {
    font-size: 9px;
  }
  
  .product-actions .el-button {
    padding: 3px 0;
    font-size: 14px;
    min-height: 24px;
  }
}

/* 平板和桌面端 */
@media screen and (min-width: 769px) {
  .product-grid {
    grid-template-columns: repeat(3, 1fr);
    gap: 15px;
  }
  
  .product-card {
    padding: 15px;
  }
}

/* 大屏桌面 */
@media screen and (min-width: 1200px) {
  .product-grid {
    grid-template-columns: repeat(4, 1fr);
  }
}

.image-item {
  margin-bottom: 15px;
  border: 1px solid #EBEEF5;
  border-radius: 8px;
  overflow: hidden;
}

.training-image {
  width: 100%;
  height: 200px;
}

.image-info {
  padding: 10px;
  display: flex;
  justify-content: space-between;
  font-size: 13px;
  color: #606266;
}

.image-time {
  color: #909399;
  font-size: 12px;
}
</style>
