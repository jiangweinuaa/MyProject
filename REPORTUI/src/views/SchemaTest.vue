<template>
  <div class="schema-test-page">
    <div class="schema-test-container">
      <div class="page-header">
        <h1>🔍 Schema 向量检索测试</h1>
        <p>输入用户问题，查看向量相似度检索结果</p>
      </div>

      <!-- 向量库状态 -->
      <div class="status-card">
        <div class="status-header">
          <h3>📊 向量库状态</h3>
          <el-button 
            size="small" 
            @click="loadStatus" 
            :loading="loadingStatus"
          >
            刷新状态
          </el-button>
        </div>
        <div class="status-content" v-if="status">
          <el-row :gutter="20">
            <el-col :span="8">
              <div class="status-item">
                <span class="label">AI_TABLE_FILTER 表数</span>
                <span class="value">{{ status.totalInFilter }}</span>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="status-item">
                <span class="label">已向量化</span>
                <span class="value success">{{ status.embeddedCount }}</span>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="status-item">
                <span class="label">未向量化</span>
                <span class="value danger">{{ status.notEmbeddedCount }}</span>
              </div>
            </el-col>
          </el-row>
          <div class="rebuild-section">
            <el-button 
              type="primary" 
              @click="rebuild" 
              :loading="loadingRebuild"
              :disabled="status.totalInFilter === 0"
            >
              🔄 重新生成向量库
            </el-button>
            <span class="hint">点击后将重新计算所有表的向量</span>
          </div>
        </div>
      </div>

      <!-- 检索测试 -->
      <div class="test-card">
        <h3>🔍 检索测试</h3>
        <div class="input-section">
          <el-input
            v-model="question"
            placeholder="输入用户问题，例如：按门店看下上个月的销售额"
            @keyup.enter="testRetrieval"
            :disabled="loadingTest"
            clearable
          />
          <el-button 
            type="primary" 
            @click="testRetrieval" 
            :loading="loadingTest"
            :disabled="!question.trim()"
          >
            检索
          </el-button>
        </div>

        <!-- 检索结果 -->
        <div class="result-section" v-if="result">
          <div class="result-header">
            <span>检索到 <strong>{{ result.totalTables }}</strong> 张表（向量维度：{{ result.vectorDim }}）</span>
          </div>
          <el-table 
            :data="result.tables" 
            stripe 
            border
            style="width: 100%"
          >
            <el-table-column type="index" label="#" width="60" />
            <el-table-column prop="tableName" label="表名" width="200">
              <template slot-scope="{row}">
                <el-tag size="small" type="primary">{{ row.tableName }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="tableDesc" label="描述" min-width="200" />
            <el-table-column prop="similarity" label="相似度" width="120" sortable>
              <template slot-scope="{row}">
                <div class="similarity-bar">
                  <div class="bar-bg">
                    <div class="bar-fill" :style="{width: row.similarity + '%'}"></div>
                  </div>
                  <span class="bar-text">{{ row.similarity }}%</span>
                </div>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>

      <!-- 未向量化表列表 -->
      <div class="not-embedded-card" v-if="status && status.notEmbeddedTables.length > 0">
        <h3>⚠️ 未向量化表</h3>
        <el-tag 
          v-for="t in status.notEmbeddedTables" 
          :key="t" 
          size="small" 
          type="warning" 
          style="margin: 4px"
        >
          {{ t }}
        </el-tag>
      </div>
    </div>
  </div>
</template>

<script>
import request from '@/api/request'

export default {
  name: 'SchemaTest',
  data() {
    return {
      question: '',
      status: null,
      result: null,
      loadingStatus: false,
      loadingRebuild: false,
      loadingTest: false
    }
  },
  mounted() {
    this.loadStatus()
  },
  methods: {
    async loadStatus() {
      this.loadingStatus = true
      try {
        const token = localStorage.getItem('token')
        const headers = token ? { Authorization: `Bearer ${token}` } : {}
        const response = await request.get('/schema/status', { headers })
        this.status = response
      } catch (e) {
        this.$message.error('获取状态失败：' + e.message)
      } finally {
        this.loadingStatus = false
      }
    },

    async rebuild() {
      this.$confirm('确定要重新生成所有表的向量吗？这可能需要几分钟。', '确认', {
        type: 'warning'
      }).then(async () => {
        this.loadingRebuild = true
        try {
          const token = localStorage.getItem('token')
          const headers = token ? { Authorization: `Bearer ${token}` } : {}
          const response = await request.post('/schema/rebuild', {}, { 
            headers,
            timeout: 120000  // 2 分钟超时
          })
          if (response.success) {
            this.$message.success(`向量库重建完成！成功 ${response.successCount}/${response.total} 张表`)
            await this.loadStatus()
          } else {
            this.$message.error('重建失败：' + response.message)
          }
        } catch (e) {
          this.$message.error('重建失败：' + e.message)
        } finally {
          this.loadingRebuild = false
        }
      }).catch(() => {})
    },

    async testRetrieval() {
      if (!this.question.trim()) return

      this.loadingTest = true
      this.result = null
      try {
        const token = localStorage.getItem('token')
        const headers = token ? { Authorization: `Bearer ${token}` } : {}
        const response = await request.get('/schema/test', {
          params: { question: this.question },
          headers
        })
        if (response.success) {
          this.result = response
        } else {
          this.$message.error('检索失败：' + response.message)
        }
      } catch (e) {
        this.$message.error('检索失败：' + e.message)
      } finally {
        this.loadingTest = false
      }
    }
  }
}
</script>

<style scoped lang="scss">
.schema-test-page {
  min-height: 100vh;
  background: #f5f7fa;
  padding: 20px;
}

.schema-test-container {
  max-width: 1200px;
  margin: 0 auto;
}

.page-header {
  text-align: center;
  margin-bottom: 30px;

  h1 {
    font-size: 24px;
    color: #303133;
    margin: 0 0 8px 0;
  }

  p {
    color: #909399;
    margin: 0;
  }
}

.status-card,
.test-card,
.not-embedded-card {
  background: white;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 20px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.08);
}

.status-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;

  h3 {
    margin: 0;
    font-size: 16px;
    color: #303133;
  }
}

.status-content {
  .status-item {
    text-align: center;
    padding: 16px;
    background: #f5f7fa;
    border-radius: 8px;

    .label {
      display: block;
      font-size: 12px;
      color: #909399;
      margin-bottom: 8px;
    }

    .value {
      display: block;
      font-size: 28px;
      font-weight: bold;
      color: #303133;

      &.success {
        color: #67c23a;
      }

      &.danger {
        color: #f56c6c;
      }
    }
  }
}

.rebuild-section {
  margin-top: 20px;
  text-align: center;

  .hint {
    margin-left: 12px;
    font-size: 12px;
    color: #909399;
  }
}

.input-section {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;

  .el-input {
    flex: 1;
  }
}

.result-section {
  .result-header {
    margin-bottom: 16px;
    font-size: 14px;
    color: #606266;

    strong {
      color: #409eff;
    }
  }
}

.similarity-bar {
  display: flex;
  align-items: center;
  gap: 8px;

  .bar-bg {
    flex: 1;
    height: 8px;
    background: #ebeef5;
    border-radius: 4px;
    overflow: hidden;

    .bar-fill {
      height: 100%;
      background: linear-gradient(90deg, #409eff, #67c23a);
      border-radius: 4px;
      transition: width 0.3s;
    }
  }

  .bar-text {
    font-size: 12px;
    color: #606266;
    min-width: 45px;
    text-align: right;
  }
}

.not-embedded-card {
  h3 {
    margin: 0 0 12px 0;
    font-size: 16px;
    color: #e6a23c;
  }
}
</style>
