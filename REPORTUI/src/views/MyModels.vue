<template>
  <div class="my-models-page">
    <div class="page-container">
      <!-- 页面标题 -->
      <div class="page-header">
        <h1>🤖 我的模型</h1>
        <div class="header-actions">
          <el-button 
            type="primary" 
            size="large"
            @click="syncModels"
            :loading="syncing"
          >
            🔄 同步阿里云模型
          </el-button>
        </div>
      </div>
      
      <!-- 提示信息 -->
      <el-alert
        title="💡 数据来源"
        type="info"
        :closable="false"
        show-icon
        style="margin-bottom: 20px"
      >
        <p>以下数据<strong>直接从阿里云百炼 API 实时获取</strong>，显示所有可用模型的详细信息。</p>
        <p>点击"🔄 同步阿里云模型"可刷新最新数据。</p>
      </el-alert>
      
      <!-- 模型列表 -->
      <el-card shadow="hover" class="table-card">
        <template #header>
          <div class="card-header">
            <span>📋 模型列表</span>
            <el-input
              v-model="searchText"
              placeholder="搜索模型..."
              style="width: 300px"
              clearable
              prefix-icon="Search"
            />
          </div>
        </template>
        
        <el-table 
          :data="filteredModels" 
          style="width: 100%" 
          border 
          stripe
          :default-sort="{prop: 'model_id', order: 'ascending'}"
        >
          <el-table-column prop="model" label="模型 ID" min-width="200" sortable />
          <el-table-column prop="name" label="模型名称" min-width="200" />
          <el-table-column prop="description" label="描述" min-width="300" show-overflow-tooltip />
          <el-table-column prop="provider" label="提供商" width="120" />
          <el-table-column label="能力" min-width="200">
            <template #default="{ row }">
              <el-tag 
                v-for="cap in (row.capabilities || [])" 
                :key="cap" 
                size="small" 
                style="margin-right: 5px"
              >
                {{ cap }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="published_time" label="发布时间" width="180" sortable />
          <el-table-column label="价格" width="150">
            <template #default="{ row }">
              <span v-if="row.price">
                ¥{{ row.price }} / {{ row.price_unit || '百万 tokens' }}
              </span>
              <span v-else style="color: #999">-</span>
            </template>
          </el-table-column>
        </el-table>
      </el-card>
    </div>
  </div>
</template>

<script>
import { Files, CircleCheck, Clock } from '@element-plus/icons-vue'
import request from '@/api/request'

export default {
  name: 'MyModels',
  components: {
    Files,
    CircleCheck,
    Clock
  },
  data() {
    return {
      models: [],
      searchText: '',
      syncing: false,
      lastSyncTime: ''
    }
  },
  computed: {
    filteredModels() {
      if (!this.searchText) {
        return this.models
      }
      const text = this.searchText.toLowerCase()
      return this.models.filter(m => 
        (m.model && m.model.toLowerCase().includes(text)) ||
        (m.name && m.name.toLowerCase().includes(text)) ||
        (m.description && m.description.toLowerCase().includes(text)) ||
        (m.provider && m.provider.toLowerCase().includes(text))
      )
    }
  },
  mounted() {
    this.loadModels()
  },
  methods: {
    async loadModels() {
      try {
        const response = await request({
          url: '/ai/models',
          method: 'get'
        })
        
        if (response.success) {
          this.models = response.data || []
          this.lastSyncTime = this.models.length > 0 ? '已同步' : '从未'
        } else {
          this.$message.error('加载模型列表失败：' + (response.message || '未知错误'))
        }
      } catch (error) {
        this.$message.error('加载模型列表失败：' + error.message)
      }
    },
    
    async syncModels() {
      this.syncing = true
      try {
        const response = await request({
          url: '/ai/sync-models',
          method: 'post'
        })
        
        if (response.success) {
          this.$message.success(response.message || '同步成功')
          // 重新加载模型列表
          await this.loadModels()
        } else {
          this.$message.error('同步失败：' + (response.message || '未知错误'))
        }
      } catch (error) {
        this.$message.error('同步失败：' + error.message)
      } finally {
        this.syncing = false
      }
    }
  }
}
</script>

<style scoped>
.my-models-page {
  height: 100%;
  background: #f0f2f5;
  padding: 20px;
  overflow: auto;
}

.page-container {
  max-width: 1400px;
  margin: 0 auto;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-header h1 {
  margin: 0;
  font-size: 24px;
  color: #303133;
}

.stats-row {
  margin-bottom: 20px;
}

.stat-card {
  display: flex;
  align-items: center;
  padding: 20px;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 20px;
  color: white;
  font-size: 28px;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-top: 5px;
}

.table-card {
  margin-top: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
