<template>
  <div class="dashvector-test-page">
    <div class="dashvector-test-container">
      <div class="page-header">
        <h1>🔮 DashVector Schema 检索测试</h1>
        <p>使用阿里云 DashVector 进行表结构向量检索</p>
      </div>

      <!-- 状态卡片 -->
      <div class="status-card">
        <div class="status-header">
          <h3>📊 DashVector 状态</h3>
          <el-button size="small" @click="loadStatus" :loading="loadingStatus">
            刷新状态
          </el-button>
        </div>
        <div class="status-content" v-if="status">
          <el-descriptions :column="3" border>
            <el-descriptions-item label="配置状态">
              <el-tag :type="status.hasConfig ? 'success' : 'danger'">
                {{ status.hasConfig ? '已配置' : '未配置' }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="Collection 状态">
              <el-tag :type="status.collectionExists ? 'success' : 'warning'">
                {{ status.collectionExists ? '已存在' : '不存在（将自动创建）' }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="文档数量">
              {{ status.docCount || 0 }}
            </el-descriptions-item>
          </el-descriptions>
          <div class="rebuild-section">
            <el-button 
              type="primary" 
              @click="rebuild" 
              :loading="loadingRebuild"
              :disabled="!status.hasConfig"
            >
              🔄 重建向量库
            </el-button>
            <span class="hint">将 AI_TABLE_FILTER 中的表结构向量化并存入 DashVector（首次点击会自动创建 Collection）</span>
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
            @keyup.enter="retrieve"
            :disabled="loadingRetrieve"
            clearable
          />
          <el-button 
            type="primary" 
            @click="retrieve" 
            :loading="loadingRetrieve"
            :disabled="!question.trim()"
          >
            检索
          </el-button>
        </div>

        <!-- 检索结果 -->
        <div class="result-section" v-if="result">
          <div class="result-header">
            <span>检索到 <strong>{{ result.tables.length }}</strong> 张相关表（向量维度：{{ result.vectorDim }}）</span>
          </div>
          <el-table :data="result.tables" stripe border style="width: 100%">
            <el-table-column type="index" label="#" width="60" />
            <el-table-column prop="tableName" label="表名" width="200">
              <template slot-scope="{row}">
                <el-tag size="small" type="primary">{{ row.tableName }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="tableComment" label="表注释" width="180" />
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
            <el-table-column label="表结构描述" min-width="300">
              <template slot-scope="{row}">
                <pre class="schema-desc">{{ row.schemaDesc }}</pre>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import request from '@/api/request'

export default {
  name: 'DashVectorTest',
  data() {
    return {
      question: '',
      status: null,
      result: null,
      loadingStatus: false,
      loadingRebuild: false,
      loadingRetrieve: false
    }
  },
  mounted() {
    this.loadStatus()
  },
  methods: {
    async loadStatus() {
      this.loadingStatus = true
      try {
        this.status = await request.get('/dashvector/status')
      } catch (e) {
        this.$message.error('获取状态失败：' + e.message)
      } finally {
        this.loadingStatus = false
      }
    },

    async rebuild() {
      this.$confirm('确定要重建 DashVector 向量库吗？这可能需要几分钟。', '确认', {
        type: 'warning'
      }).then(async () => {
        this.loadingRebuild = true
        try {
          const response = await request.post('/dashvector/rebuild', {}, { 
            timeout: 120000
          })
          if (response.success) {
            this.$message.success(`向量库重建完成！成功 ${response.successCount}/${response.total} 张表`)
            await this.loadStatus()
          } else {
            this.$message.error('重建失败：' + (response.message || '未知错误'))
          }
        } catch (e) {
          this.$message.error('重建失败：' + e.message)
        } finally {
          this.loadingRebuild = false
        }
      }).catch(() => {})
    },

    async retrieve() {
      if (!this.question.trim()) return

      this.loadingRetrieve = true
      this.result = null
      try {
        const response = await request.get('/dashvector/retrieve', {
          params: { question: this.question }
        })
        if (response.success) {
          this.result = response
        } else {
          this.$message.error('检索失败：' + (response.message || '未知错误'))
        }
      } catch (e) {
        this.$message.error('检索失败：' + e.message)
      } finally {
        this.loadingRetrieve = false
      }
    }
  }
}
</script>

<style scoped lang="scss">
.dashvector-test-page {
  min-height: 100vh;
  background: #f5f7fa;
  padding: 20px;
}

.dashvector-test-container {
  max-width: 1400px;
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
.test-card {
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

.schema-desc {
  margin: 0;
  padding: 8px;
  background: #f5f7fa;
  border-radius: 4px;
  font-size: 12px;
  line-height: 1.5;
  max-height: 200px;
  overflow-y: auto;
  white-space: pre-wrap;
  word-break: break-word;
}
</style>
